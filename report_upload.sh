#! /bin/sh

COMMENT_TOKEN=$1
echo "args:"
echo $@
echo ""
echo "gh token:"
echo $COMMENT_TOKEN

if [ "$TRAVIS_PULL_REQUEST" != "false" ] ; then
    zip -r reports.zip jbehave-support-core/target/reports

    FILE_URL=$(curl --upload-file ./reports.zip https://transfer.sh/reports.zip)

    curl -H "Authorization: token ${COMMENT_TOKEN}" -X POST \
    -d "{\"body\": \"Reports: [reports.zip](${FILE_URL})\"}" \
    "https://api.github.com/repos/${TRAVIS_REPO_SLUG}/issues/${TRAVIS_PULL_REQUEST}/comments"
fi
