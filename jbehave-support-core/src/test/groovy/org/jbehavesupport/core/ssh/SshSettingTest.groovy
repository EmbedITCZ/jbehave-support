package org.jbehavesupport.core.ssh

import org.junit.Test
import spock.lang.Specification

import static groovy.test.GroovyAssert.shouldFail

class SshSettingTest extends Specification {

    @Test
    void "multiple hostnames, rest single values, user/password"() {
        def hostnames = ["url1", "url2"] as String[]
        def ports = [666] as int[]
        def users = ["user"] as String[]
        def passwords = ["password"] as String[]
        def logs = ["app.log"] as String[]

        def setting1 = SshSetting.builder()
            .hostname("url1")
            .port(666)
            .user("user")
            .password("password")
            .logPath("app.log")
            .build()

        def setting2 = SshSetting.builder()
            .hostname("url2")
            .port(666)
            .user("user")
            .password("password")
            .logPath("app.log")
            .build()

        def settings = SshSetting.arrayHelperBuilder()
            .hostnames(hostnames)
            .ports(ports)
            .users(users)
            .passwords(passwords)
            .logPaths(logs)
            .build()

        expect:
        assert settings.size() == 2
        assert setting1, setting2 in settings
    }

    @Test
    void "multiple values, user/password"() {
        def hostnames = ["url1", "url2"] as String[]
        def ports = [666, 777] as int[]
        def users = ["user", "user2"] as String[]
        def passwords = ["password", "password2"] as String[]
        def logs = ["app.log", "app2.log"] as String[]

        def setting1 = SshSetting.builder()
            .hostname("url1")
            .port(666)
            .user("user")
            .password("password")
            .logPath("app.log")
            .build()

        def setting2 = SshSetting.builder()
            .hostname("url2")
            .port(777)
            .user("user2")
            .password("password2")
            .logPath("app2.log")
            .build()

        def settings = SshSetting.arrayHelperBuilder()
            .hostnames(hostnames)
            .ports(ports)
            .users(users)
            .passwords(passwords)
            .logPaths(logs)
            .build()

        expect:
        assert settings.size() == 2
        assert setting1, setting2 in settings
    }

    @Test
    void "multiple values, user/password + key/keypass"() {
        def hostnames = ["url1", "url2"] as String[]
        def ports = [666, 777] as int[]
        def users = ["user", "user2"] as String[]
        def passwords = ["password", null] as String[]
        def logs = ["app.log", "app2.log"] as String[]
        def keyPaths = [null, "path/to/key"] as String[]
        def keyPassphrases = [null, "passphrase"] as String[]

        def setting1 = SshSetting.builder()
            .hostname("url1")
            .port(666)
            .user("user")
            .password("password")
            .logPath("app.log")
            .build()

        def setting2 = SshSetting.builder()
            .hostname("url2")
            .port(777)
            .user("user2")
            .keyPath("path/to/key")
            .keyPassphrase("passphrase")
            .logPath("app2.log")
            .build()

        def settings = SshSetting.arrayHelperBuilder()
            .hostnames(hostnames)
            .ports(ports)
            .users(users)
            .passwords(passwords)
            .logPaths(logs)
            .keyPaths(keyPaths)
            .keyPassphrases(keyPassphrases)
            .build()

        expect:
        assert settings.size() == 2
        assert setting1, setting2 in settings
    }

    @Test
    void "malformed builder data"() {

        when:
        String causeMsg = shouldFail(IllegalArgumentException.class) {
            SshSetting.arrayHelperBuilder()
                .hostnames(hostnames as String[])
                .ports(ports as int[])
                .users(users as String[])
                .passwords(passwords as String[])
                .keyPaths(keyPaths as String[])
                .keyPassphrases(keyPassphrases as String[])
                .logPaths(logPaths as String[])
                .build()
        }.getMessage()

        then:
        causeMsg == message

        where:
        hostnames | ports | users  | logPaths | passwords | keyPaths | keyPassphrases || message
        null      | null  | null   | null     | null      | null     | null           || "hostnames must be not null nor empty"
        []        | null  | null   | null     | null      | null     | null           || "hostnames must be not null nor empty"
        ["url1"]  | null  | null   | null     | null      | null     | null           || "ports must be not null nor empty"
        ["url1"]  | []    | null   | null     | null      | null     | null           || "ports must be not null nor empty"
        ["url1"]  | [12]  | null   | null     | null      | null     | null           || "users must be not null nor empty"
        ["url1"]  | [12]  | []     | null     | null      | null     | null           || "users must be not null nor empty"
        ["url1"]  | [12]  | ["us"] | null     | null      | null     | null           || "log paths must be not null nor empty"
        ["url1"]  | [12]  | ["us"] | []       | null      | null     | null           || "log paths must be not null nor empty"

        ["url1"]  | [12]  | ["us"] | ["c:"]   | null      | null     | null           || "please provide one of auth principals: password / key path"
        ["url1"]  | [12]  | ["us"] | ["c:"]   | null      | []       | null           || "please provide one of auth principals: password / key path"
        ["url1"]  | [12]  | ["us"] | ["c:"]   | null      | null     | []             || "please provide one of auth principals: password / key path"
        ["url1"]  | [12]  | ["us"] | ["c:"]   | null      | []       | []             || "please provide one of auth principals: password / key path"

        ["url1"]  | [12]  | ["us"] | ["c:"]   | []        | null     | null           || "please provide one of auth principals: password / key path"
        ["url1"]  | [12]  | ["us"] | ["c:"]   | []        | []       | null           || "please provide one of auth principals: password / key path"
        ["url1"]  | [12]  | ["us"] | ["c:"]   | []        | null     | []             || "please provide one of auth principals: password / key path"
        ["url1"]  | [12]  | ["us"] | ["c:"]   | []        | []       | []             || "please provide one of auth principals: password / key path"
    }

    @Test
    void "wrong array sizes"() {
        when:
        String causeMsg = shouldFail(UnsupportedOperationException.class) {
            SshSetting.arrayHelperBuilder()
                .hostnames(["u1", "u2", "u3"] as String[])
                .ports(ports as int[])
                .users(users as String[])
                .passwords(passwords as String[])
                .logPaths(logPaths as String[])
                .build()
        }.getMessage()

        then:
        causeMsg == message

        where:
        ports        | users              | logPaths           | passwords    || message
        [12, 13]     | ["us"]             | ["c:"]             | ["p1"]       || "either fill position 2 for all ssh settings, or leave only one element: [12, 13]"
        [12, 13, 14] | ["us", "cn"]       | ["c:"]             | ["p1"]       || "either fill position 2 for all ssh settings, or leave only one element: [us, cn]"
        [12, 13, 14] | ["us", "cn", "gb"] | ["c:", "d:"]       | ["p1"]       || "either fill position 2 for all ssh settings, or leave only one element: [c:, d:]"
        [12, 13, 14] | ["us", "cn", "gb"] | ["c:", "d:", "f:"] | ["p1", "p2"] || "either fill position 2 for all ssh settings, or leave only one element: [p1, p2]"
    }

}
