#!/bin/bash
set -e
mvn clean install
ansible-vault decrypt conf/prodconf.encrypted --output=config.txt
cp target/submitthethird-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
jar uf app.jar config.txt
rm config.txt
zip -r app.zip app.jar Procfile
rm app.jar
eb deploy
rm app.zip
