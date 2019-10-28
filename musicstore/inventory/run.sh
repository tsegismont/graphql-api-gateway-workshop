#!/usr/bin/bash

set -e -x

mvn clean package -B
java -jar target/inventory.jar
