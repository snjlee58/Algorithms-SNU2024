import java.util.*;

public class KMerLocator {
    public static List<Kmer> findKMerPositions(String genome, List<String> kMers, int k) {
        List<Kmer> kmerPositions = new ArrayList<>();
        Set<String> kmerSet = new HashSet<>(kMers); // Use a HashSet for faster lookups

        // Slide through the genome with a window of size k
        for (int i = 0; i <= genome.length() - k; i++) {
            String fragment = genome.substring(i, i + k);

            // Check if the fragment is in the kMers list
            if (kmerSet.contains(fragment)) {
                kmerPositions.add(new Kmer(fragment, i));
            }
        }

        return kmerPositions; // Already in sorted order based on position
    }
}
