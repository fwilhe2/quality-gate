FROM eclipse-temurin:17-jre

RUN mkdir /target
ADD https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/fwilhe2/quality-gate/0.0.1-SNAPSHOT/quality-gate-0.0.1-20230524.201104-3.jar /target/quality-gate-0.0.1-SNAPSHOT.jar

COPY entrypoint.sh /

ENTRYPOINT ["/entrypoint.sh"]