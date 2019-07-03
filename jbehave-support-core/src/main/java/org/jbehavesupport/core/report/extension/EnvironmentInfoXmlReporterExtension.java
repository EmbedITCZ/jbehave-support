package org.jbehavesupport.core.report.extension;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.report.ReportContext;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

@RequiredArgsConstructor
public class EnvironmentInfoXmlReporterExtension extends AbstractXmlReporterExtension {

    private static final String ENVIRONMENT_INFO_XML_REPORTER_EXTENSION = "environmentInfo";
    private static final String ENVIRONMENT_INFO_BEGINNING = "environmentInfo.";
    private static final String KEY_VALUE_TAG = "<values><key>%s</key><value><![CDATA[%s]]></value></values>";

    private final Environment environment;

    @Override
    public String getName() {
        return ENVIRONMENT_INFO_XML_REPORTER_EXTENSION;
    }

    @Override
    public Long getPriority() {
        return -1L;
    }

    @Override
    public void print(final Writer writer, final ReportContext reportContext) {
        final Map<String, Object> environmentInfo = environmentInfo();
        environmentInfo.keySet()
            .stream()
            .forEach(key -> printKyeValue(writer, key.substring(ENVIRONMENT_INFO_BEGINNING.length()), environmentInfo.get(key)));
    }

    private void printKyeValue(final Writer writer, final String key, final Object value) {
        printString(writer, String.format(KEY_VALUE_TAG, key, String.valueOf(value)));
    }

    private Map<String, Object> environmentInfo() {
        HashMap<String, Object> environmentInfo = new HashMap<>();
        for (PropertySource<?> propertySource : ((ConfigurableEnvironment) environment).getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                    if (key.startsWith(ENVIRONMENT_INFO_BEGINNING)) {
                        environmentInfo.put(key, environment.getProperty(key));
                    }
                }
            }
        }
        return environmentInfo;
    }
}
