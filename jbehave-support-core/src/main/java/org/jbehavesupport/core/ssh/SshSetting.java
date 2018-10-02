package org.jbehavesupport.core.ssh;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.util.Arrays;
import org.springframework.util.ObjectUtils;

/**
 * This class holds settings for SSH steps/template.
 */
@Value
@Builder
public class SshSetting {

    private String hostname;
    private int port;
    private String user;
    private String password;
    private String logPath;
    private String keyPath;
    private String keyPassphrase;

    public static ArrayHelperBuilder arrayHelperBuilder() {
        return new ArrayHelperBuilder();
    }

    /**
     * Tool for creating ssh connections, when many of them is required.
     * To prevent copy-pasting in beans, you can simply define several fields, that will be combined into valid SSH connections.
     * For manual please see jbehave-support-core/Ssh.md (SSH steps)
     */
    public static class ArrayHelperBuilder {
        private String[] hostnames;
        private int[] ports;
        private String[] users;
        private String[] passwords;
        private String[] logsPaths;
        private String[] keyPaths;
        private String[] keyPassphrases;

        public List<SshSetting> build() {
            checkBuildable();

            Integer[] portsIntegerArray = IntStream.of(ports).boxed().toArray(Integer[]::new);

            List<SshSetting> sshSettings = new ArrayList<>(hostnames.length);
            for (int i = 0; i < hostnames.length; i++) {
                SshSetting sshSetting = SshSetting.builder()
                    .hostname(hostnames[i])
                    .port(getMandatoryArrayValueForPosition(portsIntegerArray, i))
                    .user(getMandatoryArrayValueForPosition(users, i))
                    .password(getOptionalArrayValueForPosition(passwords, i))
                    .keyPath(getOptionalArrayValueForPosition(keyPaths, i))
                    .keyPassphrase(getOptionalArrayValueForPosition(keyPassphrases, i))
                    .logPath(getMandatoryArrayValueForPosition(logsPaths, i))
                    .build();
                sshSettings.add(sshSetting);
            }
            return sshSettings;
        }

        private void checkBuildable() {
            notEmpty(hostnames, "hostnames must be not null nor empty");
            isTrue(ArrayUtils.isNotEmpty(ports), "ports must be not null nor empty");
            notEmpty(users, "users must be not null nor empty");
            notEmpty(logsPaths, "log paths must be not null nor empty");
            isTrue(!ObjectUtils.isEmpty(passwords) || !ObjectUtils.isEmpty(keyPaths), "please provide one of auth principals: password / key path");
        }

        public ArrayHelperBuilder hostnames(String[] hostnames) {
            this.hostnames = hostnames;
            return this;
        }

        public ArrayHelperBuilder ports(int[] ports) {
            this.ports = ports;
            return this;
        }

        public ArrayHelperBuilder users(String[] users) {
            this.users = users;
            return this;
        }

        public ArrayHelperBuilder passwords(String[] passwords) {
            this.passwords = passwords;
            return this;
        }

        public ArrayHelperBuilder keyPaths(String[] keyPaths) {
            this.keyPaths = keyPaths;
            return this;
        }

        public ArrayHelperBuilder keyPassphrases(String[] keyPassphrases) {
            this.keyPassphrases = keyPassphrases;
            return this;
        }

        public ArrayHelperBuilder logPaths(String[] logPaths) {
            this.logsPaths = logPaths;
            return this;
        }

        private static <T> T getMandatoryArrayValueForPosition(T[] array, int position) {
            if (array.length > 1 && position > 0 && array.length - 1 < position) {
                throw new UnsupportedOperationException(
                    "either fill position " + position + " for all ssh settings, or leave only one element: " + java.util.Arrays.toString(array));
            }
            return array.length > 1 ? array[position] : array[0];
        }

        private static <T> T getOptionalArrayValueForPosition(T[] array, int position) {
            return Arrays.isNullOrEmpty(array) ? null : getMandatoryArrayValueForPosition(array, position);
        }
    }

}
