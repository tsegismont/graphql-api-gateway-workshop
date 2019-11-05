#!/usr/bin/env sh

set -e -x

WORKDIR=$(mktemp -d)
REMOTE_REPO=git@github.com:tsegismont/graphql-api-gateway-workshop.git
WEBSITE_BRANCH=gh-pages
LOCAL_MASTER_REPO="${WORKDIR}/master"
LOCAL_PAGES_REPO="${WORKDIR}/${WEBSITE_BRANCH}"
INPUT_FILE=workshop.adoc
OUTPUT_FILE=index.html

mkdir -p "${LOCAL_MASTER_REPO}"
git clone ${REMOTE_REPO} "${LOCAL_MASTER_REPO}"
mkdir -p "${LOCAL_PAGES_REPO}"
git clone -b ${WEBSITE_BRANCH} ${REMOTE_REPO} "${LOCAL_PAGES_REPO}"
docker run -u $(id -u):$(id -g) -v "${LOCAL_MASTER_REPO}":/documents/ -v "${LOCAL_PAGES_REPO}":/output/ asciidoctor/docker-asciidoctor asciidoctor -r asciidoctor-diagram /documents/${INPUT_FILE} -D /output -o ${OUTPUT_FILE}
cd "${LOCAL_PAGES_REPO}"
rm -rf .asciidoctor
git add .
git commit -m "Auto generated doc"
git push origin ${WEBSITE_BRANCH}
