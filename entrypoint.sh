#!/bin/sh -l

java -cp '/target/quality-gate-0.0.1-SNAPSHOT.jar:/target/dependency/*' io.github.fwilhe2.Main --directory $GITHUB_WORKSPACE