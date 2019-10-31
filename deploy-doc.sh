#!/usr/bin/env sh

set -e -x

WORKDIR=$(mktemp -d)
REMOTE_REPO=git@github.com:tsegismont/graphql-api-gateway-workshop.git
WEBSITE_BRANCH=gh-pages
LOCAL_REPO="${WORKDIR}/${WEBSITE_BRANCH}"
INPUT_FILE=workshop.adoc
OUTPUT_FILE=index.html

mkdir -p "${LOCAL_REPO}"
git clone -b ${WEBSITE_BRANCH} ${REMOTE_REPO} "${LOCAL_REPO}"
cp ${INPUT_FILE} "${WORKDIR}"
docker run -u $(id -u):$(id -g) -v "${WORKDIR}":/documents/ -v "${LOCAL_REPO}":/output/ asciidoctor/docker-asciidoctor asciidoctor -r asciidoctor-diagram /documents/${INPUT_FILE} -D /output -o ${OUTPUT_FILE}
cd "${LOCAL_REPO}"
rm -rf .asciidoctor
git add .
git commit -m "Auto generated doc"
git push origin ${WEBSITE_BRANCH}
