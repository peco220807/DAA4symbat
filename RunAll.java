package app;
import graph.core.Graph;
import graph.scc.TarjanSCC;
import graph.scc.CondensationGraph;
import graph.topo.KahnTopo;
import graph.dagsp.DagShortestPaths;
import graph.dagsp.DagLongestPath;
import io.CsvWriter;
import io.JsonGraphIO;
import metrics.Metrics;
import metrics.SimpleMetrics;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
public class RunAll {
    public static void main(String[] args) throws Exception {
        Path outDir = Path.of("output");
        if (!Files.exists(outDir)) Files.createDirectories(outDir);

        CsvWriter cw = new CsvWriter();
        List<String[]> sccRows = new ArrayList<>();
        List<String[]> topoRows = new ArrayList<>();
        List<String[]> spRows = new ArrayList<>();
        List<String[]> lpRows = new ArrayList<>();
        sccRows.add(new String[]{"dataset","n","edges","components","avg_comp_size","dfsVisits","edgeTraversals","stackOps","time_ms"});
        topoRows.add(new String[]{"dataset","dag_nodes","dag_edges","pushes","pops","indegUpdates","time_ms"});
        spRows.add(new String[]{"dataset","source_comp","target_comp","distance","relaxations","time_ms"});
        lpRows.add(new String[]{"dataset","end_comp","longest_value","relaxations","time_ms"});

        File data = new File("data");
        String[] names = data.list((dir,name) -> name.endsWith(".json"));
        if (names == null) {
            System.out.println("No datasets in /data. Run app.GenerateDatasets first.");
            return;
        }
        Arrays.sort(names);

        int idx = 0;
        while (idx < names.length) {
            String file = names[idx];
            String dataset = file.replace(".json","");
            Graph g = new JsonGraphIO().read("data/" + file);

            Metrics mScc = new SimpleMetrics();
            mScc.reset();
            mScc.startTimer();
            List<List<Integer>> comps = new TarjanSCC(g, mScc).run();
            CondensationGraph.Result cr = new CondensationGraph().build(g, comps);
            mScc.stopTimer();

            int totalEdgesDag = 0;
            int x = 0;
            while (x < cr.dag.size()) {
                totalEdgesDag += cr.dag.getAdj().get(x).size();
                x = x + 1;
            }

            int n = g.size();
            int e = 0;
            int u = 0;
            while (u < n) { e += g.getAdj().get(u).size(); u = u + 1; }

            double avgSize = (double) n / comps.size();
            sccRows.add(new String[]{
                    dataset, String.valueOf(n), String.valueOf(e),
                    String.valueOf(comps.size()),
                    String.format(java.util.Locale.US,"%.2f", avgSize),
                    String.valueOf(mScc.getA()), String.valueOf(mScc.getB()),
                    String.valueOf(mScc.getC()),
                    String.format(java.util.Locale.US,"%.3f", mScc.getElapsedNanos()/1_000_000.0)
            });

            Metrics mTopo = new SimpleMetrics();
            mTopo.reset();
            mTopo.startTimer();
            List<Integer> order = new KahnTopo(cr.dag, mTopo).order();
            mTopo.stopTimer();
            topoRows.add(new String[]{
                    dataset, String.valueOf(cr.dag.size()),
                    String.valueOf(totalEdgesDag),
                    String.valueOf(mTopo.getA()), String.valueOf(mTopo.getB()),
                    String.valueOf(mTopo.getC()),
                    String.format(java.util.Locale.US,"%.3f", mTopo.getElapsedNanos()/1_000_000.0)
            });

            int source = 0;
            boolean[] hasIn = new boolean[cr.dag.size()];
            int q = 0;
            while (q < cr.dag.size()) {
                for (graph.core.Edge ed : cr.dag.getAdj().get(q)) {
                    hasIn[ed.getV()] = true;
                }
                q = q + 1;
            }
            int s = 0;
            while (s < cr.dag.size()) {
                if (!hasIn[s]) { source = s; break; }
                s = s + 1;
            }
            Metrics mSp = new SimpleMetrics();
            mSp.reset();
            mSp.startTimer();
            DagShortestPaths.Result spr = new DagShortestPaths(cr.dag, mSp).run(order, source);
            mSp.stopTimer();

            int bestT = source;
            long bestD = Long.MIN_VALUE/4;
            int t2 = 0;
            while (t2 < spr.dist.length) {
                if (spr.dist[t2] < Long.MAX_VALUE/4 && spr.dist[t2] > bestD) {
                    bestD = spr.dist[t2];
                    bestT = t2;
                }
                t2 = t2 + 1;
            }
            spRows.add(new String[]{
                    dataset, String.valueOf(source),
                    String.valueOf(bestT),
                    String.valueOf(bestD),
                    String.valueOf(mSp.getA()),
                    String.format(java.util.Locale.US,"%.3f", mSp.getElapsedNanos()/1_000_000.0)
            });

            Metrics mLp = new SimpleMetrics();
            mLp.reset();
            mLp.startTimer();
            DagLongestPath.Result lpr = new DagLongestPath(cr.dag, mLp).run(order);
            mLp.stopTimer();
            long bestVal = Long.MIN_VALUE/4;
            int end = 0;
            int z = 0;
            while (z < lpr.val.length) {
                if (lpr.val[z] > bestVal) { bestVal = lpr.val[z]; end = z; }
                z = z + 1;
            }
            lpRows.add(new String[]{
                    dataset, String.valueOf(end), String.valueOf(bestVal),
                    String.valueOf(mLp.getA()),
                    String.format(java.util.Locale.US,"%.3f", mLp.getElapsedNanos()/1_000_000.0)
            });
            System.out.println("Processed " + dataset);
            idx = idx + 1;
        }
        cw.write("output/scc.csv", sccRows);
        cw.write("output/topo.csv", topoRows);
        cw.write("output/shortest.csv", spRows);
        cw.write("output/longest.csv", lpRows);
        System.out.println("CSV files written to /output");
    }
}
