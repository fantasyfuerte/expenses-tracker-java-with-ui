#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

mkdir -p out
javac -d out *.java
java -cp out App
