import java.util.*;

public class LCS {
    // Method to compute LCS
    public static List<Kmer> findLCS(List<Kmer> genome1, List<Kmer> genome2) {
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
        List<Kmer> lcs = new ArrayList<>();
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (genome1.get(i - 1).value.equals(genome2.get(j - 1).value)) {
                lcs.add(0, genome1.get(i - 1)); // Add to the front
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return lcs;
    }
}
