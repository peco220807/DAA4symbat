package graph.core;
import java.util.ArrayList;
import java.util.List;
public class Graph {
    private boolean directed;
    private int n;
    private List<List<Edge>> adj;
    public Graph(boolean directed, int n) {
        this.directed = directed;
        this.n = n;
        this.adj = new ArrayList<>();
        int i = 0;
        while (i < n) {
            this.adj.add(new ArrayList<>());
            i = i + 1;
        }
    }
    public void addEdge(int u, int v, int w) {
        Edge e = new Edge(u, v, w);
        this.adj.get(u).add(e);
        if (!directed) {
            Edge rev = new Edge(v, u, w);
            this.adj.get(v).add(rev);
        }
    }
    public int size() { return n; }
    public boolean isDirected() { return directed; }
    public List<List<Edge>> getAdj() { return adj; }
}
