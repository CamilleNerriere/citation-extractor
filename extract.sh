#!/bin/bash

if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <input-pdf-path> <output-path> <format: txt|pdf>"
  exit 1
fi

INPUT="$1"
OUTPUT="$2"
FORMAT="$3"

JAR="target/citation-extractor-1.0-SNAPSHOT-jar-with-dependencies.jar"

if [ ! -f "$JAR" ]; then
  echo "Erreur : le fichier $JAR n'existe pas. Compile d'abord avec 'mvn clean package -Pprod'"
  exit 2
fi

java -jar "$JAR" -i "$INPUT" -o "$OUTPUT" -f "$FORMAT"
