#!/bin/bash
python3 cnt_kmer.py $1 $2 > genome1_kmers.txt
python3 cnt_kmer.py $1 $3 > genome2_kmers.txt

javac *.java
java GenomeAlignment $1 genome1_kmers_test.txt genome2_kmers_test.txt $2 $3
