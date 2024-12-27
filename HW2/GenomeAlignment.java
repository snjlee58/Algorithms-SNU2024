import java.util.*;
import java.io.*;
import java.nio.file.*;

public class GenomeAlignment {
    public static void main(String[] args) {
        if (args.length < 5) {
            return;
        }

        int k = Integer.parseInt(args[0]);
        String genome1TopNKmerFile = args[1];
        String genome2TopNKmerFile = args[2];
        String genome1FilePath = args[3];
        String genome2FilePath = args[4];
        String genome1Filename = getFileName(genome1FilePath); 
        String genome2Filename = getFileName(genome2FilePath);

        try {
            // Step 1: Frequent k-mer 추출
            Map<String, Integer> genome1KmerCounts = parseKmerFile(genome1TopNKmerFile);
            Map<String, Integer> genome2KmerCounts = parseKmerFile(genome2TopNKmerFile);

            // Step 2: k-mer 필터링
            // Parse genome sequences -> get sequence as string
            String genome1 = readGenome(genome1FilePath);
            String genome2 = readGenome(genome2FilePath);
            // Genome size
            int genome1Size = genome1.length();
            int genome2Size = genome2.length();
            // Filter Kmers
            List<String> filteredGenome1KMers = KMerFilter.filterKMers(genome1KmerCounts, k, genome1Size);
            List<String> filteredGenome2KMers = KMerFilter.filterKMers(genome2KmerCounts, k, genome2Size);

            // Step 3: k-mer의 발생 위치 검색
            List<Kmer> genome1KmerPositions = KMerLocator.findKMerPositions(genome1, filteredGenome1KMers, k);
            List<Kmer> genome2KmerPositions = KMerLocator.findKMerPositions(genome2, filteredGenome2KMers, k);

            // Step 4: Dynamic Programming으로 LCS 계산
            List<LCSKmer> lcs = LCS.findLCS(genome1KmerPositions, genome2KmerPositions);

            // Step 5: 결과물 저장
            saveResults(k, lcs, genome1Filename, genome2Filename);

        } catch (Exception e) {
            // e.printStackTrace();
            return;
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

     // Step 5: Method to save results to a file
    public static void saveResults(int k, List<LCSKmer> lcs, String genome1Filename, String genome2Filename) throws IOException {
        // Save LCS sequence as a text file
        String studentId = "2020-16634";
        String lcsOutputFile = studentId + "_" + k + "_" + genome1Filename + "_" + genome2Filename + "_LCS.txt";
        try (FileWriter writer = new FileWriter(lcsOutputFile)) {
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
        String genome1LcsPositionsOutputFile = studentId + "_" + k + "_" + genome1Filename + "_LCS_positions.csv";
        try (FileWriter writer = new FileWriter(genome1LcsPositionsOutputFile)) {
            writer.write("LCS k-mer,Position\n");
            for (LCSKmer kmer : lcs) {
                int oneBasedIdx = kmer.positionGenome1 + 1;
                writer.write(kmer.value + "," + oneBasedIdx + "\n");
            }
        }

        String genome2LcsPositionsOutputFile = studentId + "_" + k + "_" + genome2Filename + "_LCS_positions.csv";
        try (FileWriter writer = new FileWriter(genome2LcsPositionsOutputFile)) {
            writer.write("LCS k-mer,Position\n");
            for (LCSKmer kmer : lcs) {
                int oneBasedIdx = kmer.positionGenome2 + 1;
                writer.write(kmer.value + "," + oneBasedIdx + "\n");
            }
        }
    }
    
    public static String getFileName(String path) {
        String filename = Paths.get(path).getFileName().toString();
        filename = filename.substring(0, filename.lastIndexOf('.'));
        return filename;
    }  
}