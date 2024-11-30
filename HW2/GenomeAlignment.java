import java.util.*;
import java.io.*;

public class GenomeAlignment {
    public static void main(String[] args) {
        if (args.length < 5) {
            return;
        }

        int k = Integer.parseInt(args[0]);
        String genome1KmerFile = args[1];
        String genome2KmerFile = args[2];
        String genome1File = args[3];
        String genome2File = args[4];
        String genome1Name = removeFastaExtension(genome1File); 
        String genome2Name = removeFastaExtension(genome2File);

        try {
            // Step 1: Frequent k-mer 추출
            Map<String, Integer> genome1Kmers = parseKmerFile(genome1KmerFile);
            Map<String, Integer> genome2Kmers = parseKmerFile(genome2KmerFile);

            // Step 2: k-mer 필터링
            // Parse genome sequences -> get sequence as string
            String genome1 = readGenome(genome1File);
            String genome2 = readGenome(genome2File);
            // Genome size
            int genome1Size = genome1.length();
            int genome2Size = genome2.length();
            List<String> filteredGenome1KMers = KMerFilter.filterKMers(genome1Kmers, k, genome1Size);
            List<String> filteredGenome2KMers = KMerFilter.filterKMers(genome2Kmers, k, genome2Size);
            // System.out.println(filteredGenome1KMers);
            // System.out.println(filteredGenome1KMers.size());
            // System.out.println(filteredGenome2KMers);
            // System.out.println(filteredGenome2KMers.size());

            // Step 3: k-mer의 발생 위치 검색
            List<Kmer> genome1KmerPositions = KMerLocator.findKMerPositions(genome1, filteredGenome1KMers, k);
            List<Kmer> genome2KmerPositions = KMerLocator.findKMerPositions(genome2, filteredGenome2KMers, k);
            // for (Kmer kmer : genome2KmerPositions) {
            //     System.out.println(kmer.value + "|" + kmer.position);
            // }

            // // Step 4: Dynamic Programming으로 LCS 계산
            List<LCSKmer> lcs = LCS.findLCS(genome1KmerPositions, genome2KmerPositions);

            // Step 5: 결과물 저장
            saveResults(k, lcs, genome1Name, genome2Name);

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
    public static void saveResults(int k, List<LCSKmer> lcs, String genome1Name, String genome2Name) throws IOException {
        // Save LCS sequence as a text file
        String studentId = "2020-16634";
        String lcsFile = studentId + "_" + k + "_" + genome1Name + "_" + genome2Name + "_LCS.txt";
        try (FileWriter writer = new FileWriter(lcsFile)) {
            StringBuilder lcsSequence = new StringBuilder();
            for (LCSKmer kmer : lcs) {
                lcsSequence.append(kmer.value).append("-");
            }
            // Remove trailing hyphen
            if (lcsSequence.length() > 0) {
                lcsSequence.setLength(lcsSequence.length() - 1);
            }
            writer.write(lcsSequence.toString());
        }

        // Save LCS k-mers and positions to a CSV file
        String outputFile1 = studentId + "_" + k + "_" + genome1Name + "_LCS_positions.csv";
        try (FileWriter writer = new FileWriter(outputFile1)) {
            writer.write("LCS k-mer,Position\n");
            for (LCSKmer kmer : lcs) {
                // int position = getKmerPosition(genome1KmerPositions, kmer);
                writer.write(kmer.value + "," + kmer.positionGenome1 + "\n");
            }
        }

        String outputFile2 = studentId + "_" + k + "_" + genome2Name + "_LCS_positions.csv";
        try (FileWriter writer = new FileWriter(outputFile2)) {
            writer.write("LCS k-mer,Position\n");
            for (LCSKmer kmer : lcs) {
                // int position = getKmerPosition(genome2KmerPositions, kmer);
                writer.write(kmer.value + "," + kmer.positionGenome2 + "\n");
            }
        }
    }
    
    public static String removeFastaExtension(String fileName) {
        if (fileName.endsWith(".fasta")) {
            return fileName.substring(0, fileName.lastIndexOf(".fasta"));
        }
        return fileName; // Return the original string if it doesn't end with ".fasta"
    }

    public static int getKmerPosition(List<Kmer> kmerList, String key) {
        for (Kmer kmer : kmerList) {
            if (kmer.value.equals(key)) { // Check if the Kmer's value matches the key
                return kmer.position; // Return the position if found
            }
        }
        return -1; // Return -1 if no match is found
    }
    
}