#!/usr/bin/env sh

set -e -x

mvn clean package -Psolution
java -jar target/step-1.jar -Dvertxweb.environment=dev
