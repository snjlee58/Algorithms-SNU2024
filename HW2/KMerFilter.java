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
    
    public static boolean overlaps(String kmer1, String kmer2, int k) {
        int overlapLength = (k + 1) / 2; // Minimum overlap length

        for (int i=0; i < overlapLength; i++) {
            for (int j=0; j < overlapLength; j++) {
                String kmer1Fragment = kmer1.substring(i, i+overlapLength);
                String kmer2Fragment = kmer2.substring(j, j+overlapLength);
                // System.out.println(overlapLength);
                // System.out.println(kmer1Fragment);
                // System.out.println(kmer2Fragment);
                if (kmer1Fragment.equals(kmer2Fragment)) {
                    System.out.println(kmer1.substring(i, i+overlapLength));
                    return true;
                }
            }
        }

        return false; // No overlap found
    }
}
