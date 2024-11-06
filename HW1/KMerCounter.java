import java.io.*;
import java.util.*;

public class KMerCounter {
    public static void main(String[] args) {
        if (args.length != 2) {
            return;
        }

        int k = Integer.parseInt(args[0]);
        String inputFile = args[1];
        String outputFile = "202016634.txt";

        Map<Long, Integer> kmerCounts = new HashMap<>();

        try {
            // Read the DNA sequences from the FASTA file and count k-mers
            readFastaFile(inputFile, k, kmerCounts);

            // Sort and write k-mers with counts directly to the file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
                sortAndWriteOutput(kmerCounts, k, bw);
            }
        } catch (IOException e) {
            return;
        }
    }

    // Read the DNA sequence from a FASTA file and count k-mers
    private static void readFastaFile(String inputFile, int k, Map<Long, Integer> kmerCounts) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            StringBuilder currentChromosome = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if (line.startsWith(">")) {
                    // Process the current chromosome sequence if not empty
                    if (currentChromosome.length() > 0) {
                        countKmers(currentChromosome.toString(), k, kmerCounts);
                        currentChromosome.setLength(0); // Reset for the next chromosome
                    }
                } else {
                    currentChromosome.append(line.trim());
                }
            }

            // Process the last chromosome sequence
            if (currentChromosome.length() > 0) {
                countKmers(currentChromosome.toString(), k, kmerCounts);
            }
        }
    }

    // Method to encode a single nucleotide using 2 bits
    private static int encodeNucleotide(char nucleotide) {
        switch (nucleotide) {
            case 'A': return 0b00; // 00 in binary
            case 'C': return 0b01; // 01 in binary
            case 'G': return 0b10; // 10 in binary
            case 'T': return 0b11; // 11 in binary
            // Skip ambiguous characters
            default: return -1; // Return -1 for invalid nucleotides
        }
    }

    // Count k-mers in a DNA sequence using bitwise operations
    private static void countKmers(String sequence, int k, Map<Long, Integer> kmerCounts) {
        // Remove 'N' characters from the sequence
        String cleanedSequence = sequence.replaceAll("[^ACGT]", ""); // Keep only A, C, G, T

        // Check if the cleaned sequence is long enough
        if (cleanedSequence.length() < k) return;

        long kmer = 0; // FIXME: does this need to be long?
        int bitLength = 2 * k; // Each nucleotide is represented by 2 bits

        // Initialize the first k-mer
        for (int i = 0; i < k; i++) {
            int encoded = encodeNucleotide(cleanedSequence.charAt(i));
            if (encoded == -1) return; // Skip this k-mer if any character is invalid
            kmer = (kmer << 2) | encoded;
        }
        kmerCounts.put(kmer, kmerCounts.getOrDefault(kmer, 0) + 1);

        // Slide over the sequence to extract subsequent k-mers
        long mask = (1L << bitLength) - 1; // Mask to keep only the lower bitLength bits
        for (int i = k; i < cleanedSequence.length(); i++) {
            int encoded = encodeNucleotide(cleanedSequence.charAt(i));
            if (encoded == -1) continue; // Skip invalid nucleotides
            kmer = ((kmer << 2) | encoded) & mask;
            kmerCounts.put(kmer, kmerCounts.getOrDefault(kmer, 0) + 1);
        }
    }

    // Method to sort the k-mers and write the output lines
    private static void sortAndWriteOutput(Map<Long, Integer> kmerCounts, int k, BufferedWriter bw) throws IOException {
        List<Map.Entry<Long, Integer>> kmerList = new ArrayList<>(kmerCounts.entrySet());

        // Sort the list by count (descending), then by k-mer value (alphabetically)
        kmerList.sort((a, b) -> {
            int countComparison = b.getValue().compareTo(a.getValue());
            if (countComparison == 0) {
                return Long.compare(a.getKey(), b.getKey());
            }
            return countComparison;
        });

        // Write the top 100 k-mers (or more in case of ties) directly to the file
        int rank = 1;
        int lastCount = -1;
        for (Map.Entry<Long, Integer> entry : kmerList) {
            if (rank > 100 && entry.getValue() != lastCount) {
                break; // Stop if we've listed all ties for the 100th rank
            }
            String line = decodeKmer(entry.getKey(), k) + "," + entry.getValue();
            bw.write(line);
            bw.newLine();
            lastCount = entry.getValue();
            rank++;
        }
    }

    // Method to decode a k-mer from its bit representation to a string
    private static String decodeKmer(long kmer, int k) {
        StringBuilder kmerString = new StringBuilder();
        for (int i = k - 1; i >= 0; i--) {
            int bits = (int) ((kmer >> (2 * i)) & 0b11);
            switch (bits) {
                case 0b00: kmerString.append('A'); break;
                case 0b01: kmerString.append('C'); break;
                case 0b10: kmerString.append('G'); break;
                case 0b11: kmerString.append('T'); break;
            }
        }
        return kmerString.toString();
    }
}
