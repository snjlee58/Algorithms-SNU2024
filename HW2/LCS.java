import java.util.*;

public class LCS {
    // Method to compute LCS
    public static List<LCSKmer> findLCS(List<Kmer> genome1, List<Kmer> genome2) {
        int m = genome1.size();
        int n = genome2.size();
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

        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (genome1.get(i - 1).value.equals(genome2.get(j - 1).value)) {
                
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
        // System.out.println("LCS length=" + lcs.size()); // DEBUG
        return lcs;
    }
}