#!/usr/bin/env sh

set -e -x

INPUT_FILE=workshop.adoc
OUTPUT_DIR="${PWD}/target/workshop-doc"
OUTPUT_FILE=index.html

mkdir -p "${OUTPUT_DIR}"
docker run -u $(id -u):$(id -g) -v "${PWD}":/documents/ -v "${OUTPUT_DIR}":/output/ asciidoctor/docker-asciidoctor asciidoctor -r asciidoctor-diagram /documents/${INPUT_FILE} -D /output -o ${OUTPUT_FILE}
