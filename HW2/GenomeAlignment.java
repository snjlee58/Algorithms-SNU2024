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
        String genome1Name = "genome1"; // FIXME: parse from genome1File
        String genome2Name = "genome2"; // FIXME: parse from genome2File

        try {
            // Step 1: Frequent k-mer 추출 (예시: k=3)
            Map<String, Integer> genome1Kmers = parseKmerFile(genome1KmerFile);
            Map<String, Integer> genome2Kmers = parseKmerFile(genome2KmerFile);

            // Step 2: k-mer 필터링
            // Parse genome sequences -> get sequence as string
            String genome1 = readGenome(genome1File);
            String genome2 = readGenome(genome2File);
            // Genome size
            int genome1Size = genome1.length();
            int genome2Size = genome2.length();
            int alpha = 5; // FIX: Adjust as needed
            List<String> filteredGenome1KMers = KMerFilter.filterKMers(genome1Kmers, k, genome1Size, alpha);
            List<String> filteredGenome2KMers = KMerFilter.filterKMers(genome2Kmers, k, genome2Size, alpha);

            // Step 3: k-mer의 발생 위치 검색
            /*
             */
            // FIXME: change to 
            List<Kmer> genome1KmerPositions = KMerLocator.findKMerPositions(genome1, filteredGenome1KMers, k);
            List<Kmer> genome2KmerPositions = KMerLocator.findKMerPositions(genome2, filteredGenome2KMers, k);

            // for (Kmer kmer : genome1KmerPositions) {
            //     System.out.println(kmer.value + "|" + kmer.position);
            // }

            // // Step 4: Dynamic Programming으로 LCS 계산
            // List<Kmer> lcs = LCS.findLCS(genome1KmerPositions, genome2KmerPositions);

            // // Step 5: 결과물 저장
            // saveResults(studentId, genome1Name, genome2Name, k, lcs);

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

            // System.out.println(kmer + count);
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

     // Step 5: Method to save results to a file
    public static void saveResults(String studentId, String genome1Name, String genome2Name, int k, List<Kmer> lcs) throws IOException {
        // Save LCS sequence as a text file
        String lcsFile = studentId + "_" + k + "_" + genome1Name + "_" + genome2Name + "_LCS.txt";
        try (FileWriter writer = new FileWriter(lcsFile)) {
            StringBuilder lcsSequence = new StringBuilder();
            for (Kmer kmer : lcs) {
                lcsSequence.append(kmer.value).append("-");
            }
            // Remove trailing hyphen
            if (lcsSequence.length() > 0) {
                lcsSequence.setLength(lcsSequence.length() - 1);
            }
            writer.write(lcsSequence.toString());
        }

        // Save LCS k-mers and positions to a CSV file
        String outputFile = studentId + "_" + k + "_" + genome1Name + "_" + genome2Name + "_LCS_positions.csv";
        // try (FileWriter writer = new FileWriter(outputFile)) {
        //     writer.write("LCS k-mer,Position\n");
        //     for (Kmer kmer : lcs) {
        //         writer.write(kmer.value + "," + kmer.position + "\n");
        //     }
        // }

    }
    
}