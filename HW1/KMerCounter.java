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

    // Encoding scheme for a single nucleotide using 2 bits
    private static int encodeNucleotide(char nucleotide) {
        switch (nucleotide) {
            case 'A': return 0b00; // Binary 00
            case 'C': return 0b01; // Binary 01
            case 'G': return 0b10; // Binary 10
            case 'T': return 0b11; // Binary 11
            default: return -1; // Return -1 for invalid nucleotides
        }
    }

    // Count k-mers in a DNA sequence using bitwise operations
    private static void countKmers(String sequence, int k, Map<Long, Integer> kmerCounts) {
        // Remove invalid characters from the sequence
        String cleanedSequence = sequence.replaceAll("[^ACGT]", "");

        // Check if the cleaned sequence is long enough
        if (cleanedSequence.length() < k) return;

        long kmer = 0;
        int bitLength = 2 * k; // Each nucleotide is represented by 2 bits

        // Initialize the first k-mer
        for (int i = 0; i < k; i++) {
            int encoded = encodeNucleotide(cleanedSequence.charAt(i));
            if (encoded == -1) return; // Skip invalid nucleotides
            kmer = (kmer << 2) | encoded;
        }
        kmerCounts.put(kmer, kmerCounts.getOrDefault(kmer, 0) + 1);

        // Slide over the sequence to extract subsequent k-mers
        long mask = (1L << bitLength) - 1; // Mask to keep only the lower bitLength bits
        for (int i = k; i < cleanedSequence.length(); i++) {
            int encoded = encodeNucleotide(cleanedSequence.charAt(i));
            if (encoded == -1) continue; 
            kmer = ((kmer << 2) | encoded) & mask;
            kmerCounts.put(kmer, kmerCounts.getOrDefault(kmer, 0) + 1);
        }
    }

    // Method to sort the k-mers (using PriorityQueue) and write the output lines
    private static void sortAndWriteOutput(Map<Long, Integer> kmerCounts, int k, BufferedWriter bw) throws IOException {
        // Edge case: if no kmers were counted, return without writing anything
        if (kmerCounts.isEmpty()) {
            return;
        }
        
        // Step 1: Use PriorityQueue to find the minimum count needed for the top 100 ranks
        PriorityQueue<Map.Entry<Long, Integer>> topKmerHeap = new PriorityQueue<>(100, (a, b) -> {
            int countComparison = a.getValue().compareTo(b.getValue());
            if (countComparison == 0) {
                return Long.compare(b.getKey(), a.getKey());
            }
            return countComparison;
        });

        for (Map.Entry<Long, Integer> entry : kmerCounts.entrySet()) {
            topKmerHeap.offer(entry);
            if (topKmerHeap.size() > 100) {
                topKmerHeap.poll(); // Keep only the top 100 entries
            }
        }

        // Step 2: Find the minimum count required for the top 100 ranks
        int thresholdCount = topKmerHeap.peek().getValue();

        // Step 3: Filter entries that meet or exceed the threshold count
        List<Map.Entry<Long, Integer>> filteredKmers = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : kmerCounts.entrySet()) {
            if (entry.getValue() >= thresholdCount) {
                filteredKmers.add(entry);
            }
        }

        // Step 4: Sort the filtered entries by descending order of count, then by ascending order alphabetically
        filteredKmers.sort((a, b) -> {
            int countComparison = b.getValue().compareTo(a.getValue());
            if (countComparison == 0) {
                return Long.compare(a.getKey(), b.getKey());
            }
            return countComparison;
        });

        // Step 5: Write the sorted entries to the output file, including all ties at the 100th rank
        int writtenLines = 0;
        for (int i = 0; i < filteredKmers.size(); i++) {
            Map.Entry<Long, Integer> entry = filteredKmers.get(i);

            // Stop if we've reached beyond 100 lines and are past the 100th rank's count
            if (writtenLines >= 100 && entry.getValue() < thresholdCount) {
                break;
            }

            // Write the k-mer and its count
            String line = decodeKmer(entry.getKey(), k) + "," + entry.getValue();
            bw.write(line);
            writtenLines++;

            // Add newline if it's not the last line to be written
            if (i < filteredKmers.size() - 1 && (writtenLines < 100 || filteredKmers.get(i + 1).getValue() == thresholdCount)) {
                bw.newLine();
            }
        }
    }

    // Decoding scheme for k-mer from bit representation to a string
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
