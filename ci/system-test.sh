#!/usr/bin/env bash

mvn org.jacoco:jacoco-maven-plugin:prepare-agent-integration verify org.jacoco:jacoco-maven-plugin:report-integration -DskipUnitTests=true -Djbehave.report.level=STORY -Pintegration-test -B -V && \
curl --silent -o jbehave-support-core/target/codacy-reporter-latest.jar -L https://github.com/codacy/codacy-coverage-reporter/releases/download/4.0.3/codacy-coverage-reporter-4.0.3-assembly.jar && \
java -jar jbehave-support-core/target/codacy-reporter-latest.jar report -l Java -r jbehave-support-core/target/site/jacoco-it/jacoco.xml --partial && \
java -jar jbehave-support-core/target/codacy-reporter-latest.jar final
