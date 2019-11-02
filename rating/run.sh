#!/usr/bin/env sh

set -e -x

mvn clean package -B
java -jar target/rating.jar
