# Configuration for release to maven central

- Register at [sonatype jira](https://issues.sonatype.org) - credential should be used in ossrh server section.
- Create the task in sonatype's jira for getting permission to deploy (see [OSSRH-44075](https://issues.sonatype.org/browse/OSSRH-44075))
- Generate gpg key for signing artifacts. You can use steps from [sonatype blog](https://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven/) - use only steps for generating keys and registering to the central server - rest should be already configured.

Fragment from settings.xml (replace variables with values above):
```xml
  <servers>
    <server>
      <id>ossrh</id>
      <username>${ossrh.username}</username>
      <password>${ossrh.password}</password>
    </server>
  </servers>

  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.passphrase>${gpg.password}</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
```

Then you can simply run the command below and snapshot are going to be deployed to ossr sonatype repository. 
```bash
mvn clean deploy -Prelease
```

Release is performed by two commands
```bash
mvn release:clean release:prepare -Prelease
mvn release:perform -Prelease
```
