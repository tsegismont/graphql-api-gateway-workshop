#!/usr/bin/env sh

set -e -x

mvn clean package -Psolution
java -jar target/step-6.jar -Dvertxweb.environment=dev
