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
            int genome1Size = getGenomeSize(genome1File); // DEBUG: adjust function, theres only 1 header line in fasta files
            int genome2Size = getGenomeSize(genome2File);
            int alpha = 5; // Adjust as needed
            List<String> filteredGenome1KMers = KMerFilter.filterKMers(genome1Kmers, k, genome1Size, alpha);
            List<String> filteredGenome2KMers = KMerFilter.filterKMers(genome2Kmers, k, genome2Size, alpha);

            // Step 3: Parse genome sequences -> get sequence as string
            String genome1 = readGenome(genome1File);
            String genome2 = readGenome(genome2File);

            // Step 4: Find k-mer positions
            Map<String, List<Integer>> genome1Positions = KMerLocator.findKMerPositions(genome1, filteredGenome1KMers);
            Map<String, List<Integer>> genome2Positions = KMerLocator.findKMerPositions(genome2, filteredGenome2KMers);

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

    // Function to get genome size from fasta file (total number of non-header characters or nucleotides in the file)
    public static int getGenomeSize(String fastaFilePath) throws IOException {
        int genomeSize = 0;
        BufferedReader reader = new BufferedReader(new FileReader(fastaFilePath));
        String line;

        while ((line = reader.readLine()) != null) {
            if (!line.startsWith(">")) { // Skip header lines
                genomeSize += line.trim().length(); // Add the length of the nucleotide sequence
            }
        }

        reader.close();
        return genomeSize;
    }

    // Step 3: Parse genome sequence
    private static String readGenome(String filename) throws IOException {
        StringBuilder genome = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith(">")) { // Ignore FASTA headers
                genome.append(line.trim());
            }
        }
        br.close();
        return genome.toString();
    }

}