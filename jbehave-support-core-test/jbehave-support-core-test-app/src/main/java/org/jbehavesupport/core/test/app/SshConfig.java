package org.jbehavesupport.core.test.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.crsh.plugin.CRaSHPlugin;
import org.crsh.plugin.PluginContext;
import org.crsh.plugin.PluginDiscovery;
import org.crsh.plugin.PluginLifeCycle;
import org.crsh.plugin.ServiceLoaderDiscovery;
import org.crsh.vfs.FS;
import org.crsh.vfs.spi.AbstractFSDriver;
import org.crsh.vfs.spi.FSDriver;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

@Configuration
public class SshConfig {

    @Autowired
    private Environment env;

    @Bean
    public CrshBootstrapBean shellBootstrap() {
        CrshBootstrapBean bootstrapBean = new CrshBootstrapBean();
        Properties config = new Properties();
        config.put("crash.ssh.port", env.getProperty("ssh.port"));
        config.put("crash.ssh.auth_timeout", env.getProperty("ssh.timeouts.auth"));
        config.put("crash.ssh.idle_timeout", env.getProperty("ssh.timeouts.idle"));
        config.put("crash.auth", "simple,dummy-key");
        config.put("crash.auth.simple.username", env.getProperty("ssh.credentials.user"));
        config.put("crash.auth.simple.password", env.getProperty("ssh.credentials.password"));
        config.put("crash.auth.key.path", env.getProperty("ssh.credentials.key"));
        bootstrapBean.setConfig(config);
        return bootstrapBean;
    }

    public static class CrshBootstrapBean extends PluginLifeCycle {

        @Autowired
        private ListableBeanFactory beanFactory;

        @Autowired
        private Environment environment;

        @Autowired
        private ResourcePatternResolver resourceLoader;

        @PreDestroy
        public void destroy() {
            stop();
        }

        @PostConstruct
        public void init() {

            // Patterns to use to look for commands.
            String[] commandPathPatterns = new String[]{"classpath*:/commands/**",
                "classpath*:/crash/commands/**"};

            // Patterns to use to look for configurations.
            String[] configPathPatterns = new String[]{"classpath*:/crash/*"};

            // Comma-separated list of commands to disable.
            String[] disabledCommands = new String[]{"jpa*", "jdbc*", "jndi*"};

            // Comma-separated list of plugins to disable. Certain plugins are disabled by default
            // based on the environment.
            String[] disabledPlugins = new String[0];

            FS commandFileSystem = createFileSystem(
                commandPathPatterns,
                disabledCommands);
            FS configurationFileSystem = createFileSystem(
                configPathPatterns, new String[0]);

            PluginDiscovery discovery = new BeanFactoryFilteringPluginDiscovery(
                this.resourceLoader.getClassLoader(), this.beanFactory,
                disabledPlugins);

            PluginContext context = new PluginContext(discovery,
                createPluginContextAttributes(), commandFileSystem,
                configurationFileSystem, this.resourceLoader.getClassLoader());

            context.refresh();
            start(context);
        }

        FS createFileSystem(String[] pathPatterns, String[] filterPatterns) {
            Assert.notNull(pathPatterns, "PathPatterns must not be null");
            Assert.notNull(filterPatterns, "FilterPatterns must not be null");
            FS fileSystem = new FS();
            for (String pathPattern : pathPatterns) {
                try {
                    fileSystem.mount(new SimpleFileSystemDriver(new DirectoryHandle(
                        pathPattern, this.resourceLoader, filterPatterns)));
                } catch (IOException ex) {
                    throw new IllegalStateException(
                        "Failed to mount file system for '" + pathPattern + "'", ex);
                }
            }
            return fileSystem;
        }

        Map<String, Object> createPluginContextAttributes() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("spring.boot.version", "1.5.1.RELEASE");

            attributes.put("spring.version", SpringVersion.getVersion());
            if (this.beanFactory != null) {
                attributes.put("spring.beanfactory", this.beanFactory);
            }
            if (this.environment != null) {
                attributes.put("spring.environment", this.environment);
            }
            return attributes;
        }

    }

    private static class BeanFactoryFilteringPluginDiscovery
        extends ServiceLoaderDiscovery {

        private final ListableBeanFactory beanFactory;

        private final String[] disabledPlugins;

        BeanFactoryFilteringPluginDiscovery(ClassLoader classLoader,
                                            ListableBeanFactory beanFactory, String[] disabledPlugins)
            throws NullPointerException {
            super(classLoader);
            this.beanFactory = beanFactory;
            this.disabledPlugins = disabledPlugins;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Iterable<CRaSHPlugin<?>> getPlugins() {
            List<CRaSHPlugin<?>> plugins = new ArrayList<>();

            for (CRaSHPlugin<?> p : super.getPlugins()) {
                if (isEnabled(p)) {
                    plugins.add(p);
                }
            }

            Collection<CRaSHPlugin> pluginBeans = this.beanFactory
                .getBeansOfType(CRaSHPlugin.class).values();
            for (CRaSHPlugin<?> pluginBean : pluginBeans) {
                if (isEnabled(pluginBean)) {
                    plugins.add(pluginBean);
                }
            }

            return plugins;
        }

        boolean isEnabled(CRaSHPlugin<?> plugin) {
            Assert.notNull(plugin, "Plugin must not be null");

            if (ObjectUtils.isEmpty(this.disabledPlugins)) {
                return true;
            }

            Set<Class<?>> pluginClasses = ClassUtils.getAllInterfacesAsSet(plugin);
            pluginClasses.add(plugin.getClass());

            for (Class<?> pluginClass : pluginClasses) {
                if (!isEnabled(pluginClass)) {
                    return false;
                }
            }
            return true;
        }

        private boolean isEnabled(Class<?> pluginClass) {
            for (String disabledPlugin : this.disabledPlugins) {
                if (ClassUtils.getShortName(pluginClass).equalsIgnoreCase(disabledPlugin)
                    || ClassUtils.getQualifiedName(pluginClass)
                    .equalsIgnoreCase(disabledPlugin)) {
                    return false;
                }
            }
            return true;
        }

    }

    /**
     * {@link FSDriver} to wrap Spring's {@link Resource} abstraction to CRaSH.
     */
    private static class SimpleFileSystemDriver extends AbstractFSDriver<ResourceHandle> {

        private final ResourceHandle root;

        SimpleFileSystemDriver(ResourceHandle handle) {
            this.root = handle;
        }

        @Override
        public Iterable<ResourceHandle> children(ResourceHandle handle)
            throws IOException {
            if (handle instanceof DirectoryHandle) {
                return ((DirectoryHandle) handle).members();
            }
            return Collections.emptySet();
        }

        @Override
        public long getLastModified(ResourceHandle handle) throws IOException {
            if (handle instanceof FileHandle) {
                return ((FileHandle) handle).getLastModified();
            }
            return -1;
        }

        @Override
        public boolean isDir(ResourceHandle handle) throws IOException {
            return handle instanceof DirectoryHandle;
        }

        @Override
        public String name(ResourceHandle handle) throws IOException {
            return handle.getName();
        }

        @Override
        public Iterator<InputStream> open(ResourceHandle handle) throws IOException {
            if (handle instanceof FileHandle) {
                return Collections.singletonList(((FileHandle) handle).openStream())
                    .iterator();
            }
            return Collections.<InputStream>emptyList().iterator();
        }

        @Override
        public ResourceHandle root() throws IOException {
            return this.root;
        }

    }

    /**
     * Base for handles to Spring {@link Resource}s.
     */
    private abstract static class ResourceHandle {

        private final String name;

        ResourceHandle(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

    }

    /**
     * {@link ResourceHandle} for a file backed by a Spring {@link Resource}.
     */
    private static class FileHandle extends ResourceHandle {

        private final Resource resource;

        FileHandle(String name, Resource resource) {
            super(name);
            this.resource = resource;
        }

        public InputStream openStream() throws IOException {
            return this.resource.getInputStream();
        }

        public long getLastModified() {
            try {
                return this.resource.lastModified();
            } catch (IOException ex) {
                return -1;
            }
        }

    }

    /**
     * {@link ResourceHandle} for a directory.
     */
    private static class DirectoryHandle extends ResourceHandle {

        private final ResourcePatternResolver resourceLoader;

        private final String[] filterPatterns;

        private final AntPathMatcher matcher = new AntPathMatcher();

        DirectoryHandle(String name, ResourcePatternResolver resourceLoader,
                        String[] filterPatterns) {
            super(name);
            this.resourceLoader = resourceLoader;
            this.filterPatterns = filterPatterns;
        }

        public List<ResourceHandle> members() throws IOException {
            Resource[] resources = this.resourceLoader.getResources(getName());
            List<ResourceHandle> files = new ArrayList<>();
            for (Resource resource : resources) {
                if (!resource.getURL().getPath().endsWith("/")
                    && !shouldFilter(resource)) {
                    files.add(new FileHandle(resource.getFilename(), resource));
                }
            }
            return files;
        }

        private boolean shouldFilter(Resource resource) {
            for (String filterPattern : this.filterPatterns) {
                if (this.matcher.match(filterPattern, resource.getFilename())) {
                    return true;
                }
            }
            return false;
        }
    }
}
