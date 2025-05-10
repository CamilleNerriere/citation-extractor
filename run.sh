#!/bin/bash
mvn compile exec:java \
  -Dexec.mainClass="com.citationextractor.App" \
  -Dexec.args="-i $1 -o $2 -f $3"
