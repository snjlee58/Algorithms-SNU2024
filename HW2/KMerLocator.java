import java.util.*;

public class KMerLocator {
    public static Map<String, List<Integer>> findKMerPositions(String genome, List<String> kMers) {
        Map<String, List<Integer>> kMerPositions = new HashMap<>();
        for (String kmer : kMers) {
            List<Integer> positions = new ArrayList<>();
            int index = genome.indexOf(kmer);
            while (index >= 0) {
                positions.add(index);
                index = genome.indexOf(kmer, index + 1);
            }
            kMerPositions.put(kmer, positions);
            System.out.println(kmer + "(" + positions + ")"); // DEBUG
        }
        return kMerPositions;
    }
}
