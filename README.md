# Quality Gate

Quality gate based on https://github.com/jenkinsci/analysis-model

Example usage for cli:

```
java -cp 'target/quality-gate-0.0.1-SNAPSHOT.jar:target/dependency/*' io.github.fwilhe2.Main --directory .
```

Example usage for Github actions:

```yaml
      - name: Build with Maven Wrapper
        run: ./mvnw -V -ntp clean verify -Dmaven.test.failure.ignore=true --file pom.xml org.apache.maven.plugins:maven-pmd-plugin:3.21.0:pmd org.apache.maven.plugins:maven-checkstyle-plugin:3.3.0:checkstyle com.github.spotbugs:spotbugs-maven-plugin:4.7.3.4:spotbugs
      - name: Quality gate
        uses: fwilhe2/quality-gate@main
```