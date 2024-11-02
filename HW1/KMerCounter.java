import java.io.*;
import java.util.*;

public class KMerCounter {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java KMerCounter <k-mer length> <input file>"); // DELETE
            return;
        }

        int k = Integer.parseInt(args[0]);
        String inputFile = args[1];
        String outputFile = "202016634.txt";  

        Map<String, Integer> kmerCounts = new HashMap<>();

        try {
            // Read the DNA sequences from the FASTA file and count kmers
            readFastaFile(inputFile, k, kmerCounts);

            // Sort the k-mers and prepare the output lines
            List<String> outputLines = sortAndPrepareOutput(kmerCounts);

            // Write the output to the file
            writeOutputToFile(outputFile, outputLines);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage()); // DELETE
            return;
        }
    }

    // Read the DNA sequence from a FASTA file and count kmers
    private static void readFastaFile(String inputFile, int k, Map<String, Integer> kmerCounts) throws IOException {
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

    // Count k-mers in a DNA sequence
    private static void countKmers(String sequence, int k, Map<String, Integer> kmerCounts) {
         // Remove 'N' characters from the sequence
        String cleanedSequence = sequence.replaceAll("N", "");

        // Count k-mers
        for (int i = 0; i <= cleanedSequence.length() - k; i++) {
            String kmer = cleanedSequence.substring(i, i + k);
            kmerCounts.put(kmer, kmerCounts.getOrDefault(kmer, 0) + 1);
        }
    }

    // Method to sort the k-mers and prepare the output lines
    private static List<String> sortAndPrepareOutput(Map<String, Integer> kmerCounts) {
        List<Map.Entry<String, Integer>> kmerList = new ArrayList<>(kmerCounts.entrySet());

        // Sort the list by count (descending), then alphabetically
        kmerList.sort((a, b) -> {
            int countComparison = b.getValue().compareTo(a.getValue());
            if (countComparison == 0) {
                return a.getKey().compareTo(b.getKey());
            }
            return countComparison;
        });

        // Prepare the output for the top 100 k-mers (or more in case of ties)
        int rank = 1;
        int lastCount = -1;
        List<String> outputLines = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : kmerList) {
            if (rank > 100 && entry.getValue() != lastCount) {
                break; // Stop if we've listed all ties for the 100th rank
            }
            outputLines.add(entry.getKey() + "," + entry.getValue());
            lastCount = entry.getValue();
            rank++;
        }

        return outputLines;
    }

    // Method to write the output to a file
    private static void writeOutputToFile(String outputFile, List<String> outputLines) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            for (String line : outputLines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }
}
