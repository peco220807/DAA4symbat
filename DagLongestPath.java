package graph.dagsp;
import graph.core.Edge;
import graph.core.Graph;
import metrics.Metrics;
import java.util.List;
public class DagLongestPath {
    private Graph dag;
    private Metrics m;
    public static class Result {
        public long[] val;
        public int[] parent;
        public int start;
        public int end;
    }
    public DagLongestPath(Graph dag, Metrics m) {
        this.dag = dag;
        this.m = m;
    }
    public Result run(List<Integer> topo) {
        int n = dag.size();
        long negInf = Long.MIN_VALUE / 4;
        long[] dp = new long[n];
        int[] parent = new int[n];

        for (int i = 0; i < n; i++) {
            dp[i] = negInf;
            parent[i] = -1;
        }
        boolean[] hasIn = new boolean[n];
        for (int u = 0; u < n; u++) {
            for (Edge e : dag.getAdj().get(u)) {
                hasIn[e.getV()] = true;
            }
        }
        for (int i = 0; i < n; i++) {
            if (!hasIn[i]) {
                dp[i] = 0L;
            }
        }
        for (int u : topo) {
            if (dp[u] > negInf) {
                for (Edge e : dag.getAdj().get(u)) {
                    long newDist = dp[u] + e.getW();
                    if (newDist > dp[e.getV()]) {
                        dp[e.getV()] = newDist;
                        parent[e.getV()] = u;
                        m.incA();
                    }
                }
            }
        }
        int end = 0;
        long bestVal = negInf;
        for (int i = 0; i < n; i++) {
            if (dp[i] > bestVal) {
                bestVal = dp[i];
                end = i;
            }
        }
        Result result = new Result();
        result.val = dp;
        result.parent = parent;
        result.start = -1;
        result.end = end;
        return result;
    }
}
