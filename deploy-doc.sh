#!/usr/bin/env sh

set -e -x

WORKDIR=$(mktemp -d)
REMOTE_REPO=git@github.com:tsegismont/graphql-api-gateway-workshop.git
WEBSITE_BRANCH=gh-pages
INPUT_FILE=workshop.adoc
OUTPUT_FILE=index.html

git clone -b ${WEBSITE_BRANCH} ${REMOTE_REPO} "${WORKDIR}"
cp ${INPUT_FILE} "${WORKDIR}"
cd "${WORKDIR}"
docker run -v "${PWD}":/documents/ asciidoctor/docker-asciidoctor asciidoctor /documents/${INPUT_FILE} -o /documents/${OUTPUT_FILE}
git add ${OUTPUT_FILE}
git commit -m "Auto generated doc"
git push origin ${WEBSITE_BRANCH}
