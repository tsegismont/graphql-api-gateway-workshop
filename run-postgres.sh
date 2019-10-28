#!/usr/bin/bash

set -e -x

docker run -p 5432:5432 -e POSTGRES_USER=musicstore -e POSTGRES_PASSWORD=musicstore -d postgres
