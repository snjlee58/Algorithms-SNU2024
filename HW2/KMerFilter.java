import java.util.*;

public class KMerFilter {
    public static List<String> filterKMers(Map<String, Integer> kmerCounts, int k, int genomeSize) {
        List<String> filteredKMers = new ArrayList<>();
        Set<String> selectedKMers = new HashSet<>();
        
        // int alpha = 5;
        // double threshold = alpha * genomeSize / Math.pow(4, k);
        double tooFrequentThreshold = tooFrequentThreshold(k, genomeSize);
        System.out.println("threshold" + tooFrequentThreshold);

        double lowerPercentile = 0.1;
        // double upperPercentile = 0.4;

        // Determine the range of indices for the top-k percentiles
        int totalKmers = kmerCounts.size();
        int lowerIndex = (int) Math.ceil(totalKmers * lowerPercentile);
        // int upperIndex = (int) Math.floor(totalKmers * upperPercentile);

        // Iterate through the k-mers and filter based on the percentile range
        int currentIndex = 0;

        for (Map.Entry<String, Integer> entry : kmerCounts.entrySet()) {
            String kmer = entry.getKey();
            // int count = entry.getValue();

            // Exclude if kmer count is in top 10%
            if (currentIndex <= lowerIndex) {
                currentIndex++; 
                continue;
            }
            
            // Exclude too frequent kmers
            // if (count > tooFrequentThreshold) {
            //     continue; 
            // }

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
    
    public static double tooFrequentThreshold(int k, int genomeSize) {
        // Calculate equation2: (k // 2) * (N / Math.pow(4, k))
        int halfK = k / 2; // Integer division (k // 2)
        double expectedFrequency = genomeSize / Math.pow(4, k); // N / 4^k
        double equation2 = halfK * expectedFrequency;

        // Return the ceiling of the result
        return Math.ceil(equation2);
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
                    // System.out.println(kmer1.substring(i, i+overlapLength));
                    return true;
                }
            }
        }

        return false; // No overlap found
    }
    
            //  equation2 = (k // 2) * (N / math.pow(4, k))
            // return math.ceil(equation2)\
}
