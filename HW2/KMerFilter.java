import java.util.*;

public class KMerFilter {
    public static List<String> filterKMers(Map<String, Integer> kmerCounts, int k, int genomeSize) {
        List<String> filteredKMers = new ArrayList<>();
        Set<String> selectedKMers = new HashSet<>();
        
        double alphaThreshold = tooFrequentAlphaThreshold(k, genomeSize);
        int top10PercentCount = getTopPercentCount(kmerCounts, 0.1);
        int tooFrequentThreshold = (int) Math.max(10, Math.max(alphaThreshold, top10PercentCount));

        // Iterate through the k-mers and filter
        for (Map.Entry<String, Integer> entry : kmerCounts.entrySet()) {
            String kmer = entry.getKey();
            int count = entry.getValue();

            // // Exclude too frequent kmers
            if (count > tooFrequentThreshold) {
                continue; 
            }

            // Exclude kmers overlapping 50% of any selected kmers
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
    
    public static double tooFrequentAlphaThreshold(int k, int genomeSize) {
        int halfK = k / 2; 
        double expectedFrequency = genomeSize / Math.pow(4, k);
        double threshold = halfK * expectedFrequency;

        return Math.ceil(threshold);
    }

    public static int getTopPercentCount(Map<String, Integer> kmerCounts, double percentile) {
        // Convert the Map entries to a List
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(kmerCounts.entrySet());

        // Sort the list by count in descending order
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Find the index corresponding to the top percentile
        int totalKmers = sortedEntries.size();
        int index = (int) Math.ceil(totalKmers * percentile) - 1;

        // Ensure the index is within bounds
        if (index < 0 || index >= totalKmers) {
            return 0;
        }

        // Return the count value at the calculated index
        return sortedEntries.get(index).getValue();
    }
    
    public static boolean overlaps(String kmer1, String kmer2, int k) {
        int overlapLength = (k + 1) / 2;
        Set<String> kmer1Fragments = new HashSet<>();
        for (int i = 0; i <= kmer1.length() - overlapLength; i++) {
            kmer1Fragments.add(kmer1.substring(i, i + overlapLength));
        }
        for (int i = 0; i <= kmer2.length() - overlapLength; i++) {
            if (kmer1Fragments.contains(kmer2.substring(i, i + overlapLength))) {
                return true;
            }
        }
        return false;
    }
}
