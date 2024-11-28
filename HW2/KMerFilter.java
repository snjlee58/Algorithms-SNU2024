import java.util.*;

public class KMerFilter {
    public static List<String> filterKMers(Map<String, Integer> kmerCounts, int k, int genomeSize, int alpha) {
        List<String> filteredKMers = new ArrayList<>();
        Set<String> selectedKMers = new HashSet<>();
        double threshold = alpha * genomeSize / Math.pow(4, k);
        for (Map.Entry<String, Integer> entry : kmerCounts.entrySet()) {
            String kmer = entry.getKey();
            int count = entry.getValue();
            
            // Exclude too frequent kmers
            if (count > threshold) continue; 
            
            boolean overlapping = false;
            for (String selected : selectedKMers) {
                if (overlaps(selected, kmer, k)) {
                    overlapping = true;
                    break;
                }
            }
            
            if (!overlapping) {
                filteredKMers.add(kmer);
                selectedKMers.add(kmer);
            }
        }
        return filteredKMers;
    }
    
    private static boolean overlaps(String kmer1, String kmer2, int k) {
        int overlap = (k + 1) / 2; // Define "overlapping" condition
        return kmer1.substring(k - overlap).equals(kmer2.substring(0, overlap));
    }
}
