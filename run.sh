#!/usr/bin/env bash
mvn clean && mvn install
mvn exec:java -Dexec.mainClass="walter.org.process.Process" -Dexec.args="path-to-extracted-data"
