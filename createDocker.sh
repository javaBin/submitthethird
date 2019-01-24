#!/bin/bash
set -e
mvn clean install
ansible-vault decrypt conf/testconf.encrypted --output=config.txt
docker build -t submitdocker:1.0 .
rm config.txt
