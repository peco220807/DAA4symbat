package smoke;

import graph.core.Graph;
import graph.scc.TarjanSCC;
import graph.scc.CondensationGraph;
import graph.topo.KahnTopo;
import metrics.SimpleMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BasicTests {

    @Test
    public void sccAndTopoSmoke() {
        Graph g = new Graph(true, 5);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 1);

        SimpleMetrics m = new SimpleMetrics();
        m.reset();
        List<java.util.List<Integer>> comps = new TarjanSCC(g, m).run();
        CondensationGraph.Result cr = new CondensationGraph().build(g, comps);
        java.util.List<Integer> ord = new KahnTopo(cr.dag, m).order();
        assertFalse(ord.isEmpty());
    }

    @Test
    public void graphEdgeAdditionTest() {
        Graph g = new Graph(true, 5); // Directed graph with 5 nodes
        g.addEdge(0, 1, 10);
        g.addEdge(1, 2, 20);
        g.addEdge(2, 3, 30);
        g.addEdge(3, 4, 40);

        assertEquals(1, g.getAdj().get(0).size()); // Node 0 has one edge to 1
        assertEquals(1, g.getAdj().get(1).size()); // Node 1 has one edge to 2
        assertEquals(1, g.getAdj().get(2).size()); // Node 2 has one edge to 3
        assertEquals(1, g.getAdj().get(3).size()); // Node 3 has one edge to 4
        assertEquals(0, g.getAdj().get(4).size()); // Node 4 has no outgoing edges
    }

}