package graph.topo;
import graph.core.Edge;
import graph.core.Graph;
import metrics.Metrics;
import java.util.*;
public class KahnTopo {
    private Graph dag;
    private Metrics m;
    public KahnTopo(Graph dag, Metrics m) {
        this.dag = dag;
        this.m = m;
    }
    public java.util.List<Integer> order() {
        int n = dag.size();
        int[] indeg = new int[n];
        int i = 0;
        while (i < n) {
            for (Edge e : dag.getAdj().get(i)) {
                indeg[e.getV()] = indeg[e.getV()] + 1;
            }
            i = i + 1;
        }
        Deque<Integer> q = new ArrayDeque<>();
        int j = 0;
        while (j < n) {
            if (indeg[j] == 0) {
                q.add(j);
                m.incA(); // push
            }
            j = j + 1;
        }
        java.util.List<Integer> res = new java.util.ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.remove();
            m.incB(); // pop
            res.add(u);
            for (Edge e : dag.getAdj().get(u)) {
                indeg[e.getV()] = indeg[e.getV()] - 1;
                m.incC(); // in-degree update
                if (indeg[e.getV()] == 0) {
                    q.add(e.getV());
                    m.incA();
                }
            }
        }
        return res;
    }
}