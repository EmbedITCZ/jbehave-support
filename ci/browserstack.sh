#!/usr/bin/env bash

mvn verify -DskipUnitTests=true -Djbehave.report.level=STORY -Dbrowser-stack.username=${BROWSERSTACK_USER} -Dbrowser-stack.key=${BROWSERSTACK_KEY} -Dit.test=**/BrowserStackIT -Pintegration-test -B -V
