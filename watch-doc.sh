#!/usr/bin/env sh

set -e -x

while inotifywait -e close_write workshop.adoc; do ./build-doc.sh; done
