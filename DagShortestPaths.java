package graph.dagsp;
import graph.core.Edge;
import graph.core.Graph;
import metrics.Metrics;
import java.util.List;
public class DagShortestPaths {
    private Graph dag;
    private Metrics m;
    public DagShortestPaths(Graph dag, Metrics m) {
        this.dag = dag;
        this.m = m;
    }
    public static class Result {
        public long[] dist;
        public int[] parent;
        public int source;
    }
    public Result run(List<Integer> topo, int source) {
        int n = dag.size();
        long inf = Long.MAX_VALUE;
        long[] dist = new long[n];
        int[] parent = new int[n];

        for (int i = 0; i < n; i++) {
            dist[i] = inf;
            parent[i] = -1;
        }
        dist[source] = 0;
        for (int u : topo) {
            if (dist[u] < inf) {
                for (Edge e : dag.getAdj().get(u)) {
                    int v = e.getV();
                    long newDist = dist[u] + e.getW();
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        parent[v] = u;
                        m.incA();
                    }
                }
            }
        }
        Result r = new Result();
        r.dist = dist;
        r.parent = parent;
        r.source = source;
        return r;
    }
}
