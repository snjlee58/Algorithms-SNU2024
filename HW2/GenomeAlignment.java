import java.util.*;
import java.io.*;

public class GenomeAlignment {
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Usage: java Alignment <k> <genome1_kmers.txt> <genome2_kmers.txt> <genome1.fasta> <genome2.fasta>"); // DEBUG
            return;
        }

        int k = Integer.parseInt(args[0]);
        String genome1KmerFile = args[1];
        String genome2KmerFile = args[2];
        String genome1File = args[3];
        String genome2File = args[4];

        try {
            // Step 1: Parse k-mer files
            Map<String, Integer> genome1Kmers = parseKmerFile(genome1KmerFile);
            Map<String, Integer> genome2Kmers = parseKmerFile(genome2KmerFile);

            // Step 2: Filter k-mers
            int genome1Size = (int) new File(genome1File).length(); // Approx genome size
            int genome2Size = (int) new File(genome2File).length(); // Approx genome size
            double alpha = 5.0; // Adjust as needed
            List<String> filteredGenome1KMers = KMerFilter.filterKMers(genome1Kmers, k, genome1Size, alpha);
            List<String> filteredGenome2KMers = KMerFilter.filterKMers(genome2Kmers, k, genome2Size, alpha);
        } catch (Exception e) {
            e.printStackTrace();
        }         

    }

    // Step 1: Parse k-mer file
    private static Map<String, Integer> parseKmerFile(String filename) throws IOException {
        Map<String, Integer> kmerCounts = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            String kmer = parts[0];
            int count = Integer.parseInt(parts[1]);
            kmerCounts.put(kmer, count);
        }
        br.close();
        return kmerCounts;
    }

}