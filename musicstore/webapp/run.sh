#!/usr/bin/bash

set -e -x

mvn clean package -B
java -jar target/webapp.jar
