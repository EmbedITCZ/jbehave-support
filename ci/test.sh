#!/usr/bin/env bash

mvn org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report -B -V && \
curl --silent -o jbehave-support-core/target/codacy-reporter-latest.jar -L https://github.com/codacy/codacy-coverage-reporter/releases/download/4.0.3/codacy-coverage-reporter-4.0.3-assembly.jar && \
java -jar jbehave-support-core/target/codacy-reporter-latest.jar report -l Java -r jbehave-support-core/target/site/jacoco/jacoco.xml --partial
