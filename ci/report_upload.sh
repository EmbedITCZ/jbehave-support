#! /bin/sh

if [ "$TRAVIS_PULL_REQUEST" != "false" ] ; then
    zip -r -j reports.zip jbehave-support-core/target/reports/*
    curl -F "file=@reports.zip;type=application/zip" -X POST -H "Content-Type: multipart/form-data" -u ${JBUS_REPORT_SERVICE_AUTHENTICATION} ${JBUS_REPORT_SERVICE_ADDRESS}/api/reports/${TRAVIS_PULL_REQUEST}
fi
