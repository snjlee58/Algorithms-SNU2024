#!/bin/bash
# Usage: sh run.sh <k-mer length> <input file>
k_mer=$1
shift
input_file=$*

javac KMerCounter.java
java KMerCounter "$k_mer" "$input_file"
