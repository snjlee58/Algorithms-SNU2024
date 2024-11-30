import java.util.*;

public class LCS {
    // Method to compute LCS
    public static List<LCSKmer> findLCS(List<Kmer> genome1, List<Kmer> genome2) {
        int m = genome1.size();
        int n = genome2.size();
        System.out.println("LCS array size=" + (long) m * n);
        
        int[][] dp = new int[m + 1][n + 1];

        // Fill DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (genome1.get(i - 1).value.equals(genome2.get(j - 1).value)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        // Backtrack to find LCS
        List<LCSKmer> lcs = new ArrayList<>();
        // List<Integer> genome1LCSPositions = new ArrayList<>();
        // List<Integer> genome2LCSPositions = new ArrayList<>();

        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (genome1.get(i - 1).value.equals(genome2.get(j - 1).value)) {
                // lcs.add(genome1.get(i - 1).value); // Add to the front
                
                // Add the k-mer and its positions in both genomes to the result
                lcs.add(new LCSKmer(
                        genome1.get(i - 1).value,
                        genome1.get(i - 1).position,
                        genome2.get(j - 1).position));

                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        // Reverse the list because we added elements in reverse order during backtracking
        Collections.reverse(lcs);

        return lcs;
    }

    // public static List<Kmer> findLCS(List<Kmer> genome1, List<Kmer> genome2) {
    //     int m = genome1.size();
    //     int n = genome2.size();

    //     // Only store two rows of the DP table
    //     int[] prev = new int[n + 1];
    //     int[] curr = new int[n + 1];

    //     // Fill DP table using rolling arrays
    //     for (int i = 1; i <= m; i++) {
    //         for (int j = 1; j <= n; j++) {
    //             if (genome1.get(i - 1).value.equals(genome2.get(j - 1).value)) {
    //                 curr[j] = prev[j - 1] + 1;
    //             } else {
    //                 curr[j] = Math.max(prev[j], curr[j - 1]);
    //             }
    //         }
    //         // Swap rows: current becomes previous
    //         int[] temp = prev;
    //         prev = curr;
    //         curr = temp;
    //     }

    //     // Backtracking without full table reconstruction
    //     List<Kmer> lcs = new ArrayList<>();
    //     int i = m, j = n;

    //     while (i > 0 && j > 0) {
    //         // Recalculate only the relevant part of the current row
    //         if (genome1.get(i - 1).value.equals(genome2.get(j - 1).value)) {
    //             // Move diagonally: match found
    //             lcs.add(0, genome1.get(i - 1)); // Add the matching Kmer to the front of the result
    //             i--;
    //             j--;
    //         } else {
    //             // Move based on the larger value between prev[j] and curr[j - 1]
    //             if (prev[j] >= curr[j - 1]) {
    //                 i--; // Move up
    //             } else {
    //                 j--; // Move left
    //             }
    //         }

    //         // Swap arrays to "move up" in the rolling rows
    //         int[] temp = curr;
    //         curr = prev;
    //         prev = temp;

    //         // Recompute `curr` row values for the current `i` (backtracking context)
    //         for (int k = 1; k <= n; k++) {
    //             if (i > 0 && genome1.get(i - 1).value.equals(genome2.get(k - 1).value)) {
    //                 curr[k] = prev[k - 1] + 1;
    //             } else if (i > 0) {
    //                 curr[k] = Math.max(prev[k], curr[k - 1]);
    //             }
    //         }
    //     }

    //     return lcs;
    // }

}