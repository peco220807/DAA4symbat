package app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.JsonGraphIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
public class GenerateDatasets {
    public static void main(String[] args) throws Exception {
        Path dataDir = Path.of("data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        ObjectMapper om = new ObjectMapper();

        makeMixed(om, "data/small_1.json", 8, 10, 0.25, true);
        makeDAG(om,   "data/small_2.json", 9,  12, 0.35);
        makeOneCycle(om, "data/small_3.json", 7, 9, 3);

        makeMixed(om, "data/medium_1.json", 12, 20, 0.25, true);
        makeMixed(om, "data/medium_2.json", 16, 28, 0.35, true);
        makeDAG(om,   "data/medium_3.json", 18, 32, 0.30);

        makeMixed(om, "data/large_1.json", 24, 50, 0.20, true);
        makeDAG(om,   "data/large_2.json", 32, 64, 0.22);
        makeMixed(om, "data/large_3.json", 40, 120, 0.35, true);

        System.out.println("Generated 9 datasets in /data");
    }
    private static void makeDAG(ObjectMapper om, String path, int n, int m, double density) throws Exception {
        ObjectNode root = om.createObjectNode();
        root.put("directed", true);
        root.put("n", n);
        root.put("source", 0);
        root.put("weight_model", "edge");
        ArrayNode edges = om.createArrayNode();
        Random rnd = new Random(n * 31L + m);
        int u = 0;
        while (u < n) {
            int v = u + 1;
            while (v < n) {
                if (rnd.nextDouble() < density && edges.size() < m) {
                    ObjectNode e = om.createObjectNode();
                    e.put("u", u);
                    e.put("v", v);
                    e.put("w", 1 + rnd.nextInt(9));
                    edges.add(e);
                }
                v = v + 1;
            }
            u = u + 1;
        }
        root.set("edges", edges);
        new JsonGraphIO().write(path, root);
    }
    private static void makeMixed(ObjectMapper om, String path, int n, int m, double dagDensity, boolean addCycles) throws Exception {
        ObjectNode root = om.createObjectNode();
        root.put("directed", true);
        root.put("n", n);
        root.put("source", 0);
        root.put("weight_model", "edge");

        ArrayNode edges = om.createArrayNode();
        Random rnd = new Random(n * 17L + m);

        int u = 0;
        while (u < n) {
            int v = u + 1;
            while (v < n) {
                if (rnd.nextDouble() < dagDensity) {
                    ObjectNode e = om.createObjectNode();
                    e.put("u", u);
                    e.put("v", v);
                    e.put("w", 1 + rnd.nextInt(9));
                    edges.add(e);
                }
                v = v + 1;
            }
            u = u + 1;
        }
        if (addCycles) {
            int cycles = Math.max(2, n / 6);
            int i = 0;
            while (i < cycles) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                if (a != b) {
                    ObjectNode e1 = om.createObjectNode();
                    e1.put("u", a); e1.put("v", b); e1.put("w", 1 + rnd.nextInt(9));
                    ObjectNode e2 = om.createObjectNode();
                    e2.put("u", b); e2.put("v", a); e2.put("w", 1 + rnd.nextInt(9));
                    edges.add(e1); edges.add(e2);
                }
                i = i + 1;
            }
        }
        while (edges.size() > m) {
            edges.remove(edges.size() - 1);
        }
        root.set("edges", edges);
        new JsonGraphIO().write(path, root);
    }
    private static void makeOneCycle(ObjectMapper om, String path, int n, int m, int cycleLen) throws Exception {
        ObjectNode root = om.createObjectNode();
        root.put("directed", true);
        root.put("n", n);
        root.put("source", 0);
        root.put("weight_model", "edge");

        ArrayNode edges = om.createArrayNode();
        Random rnd = new Random(n * 13L + m);

        int i = 0;
        while (i < n - 1) {
            ObjectNode e = om.createObjectNode();
            e.put("u", i);
            e.put("v", i + 1);
            e.put("w", 1 + rnd.nextInt(9));
            edges.add(e);
            i = i + 1;
        }
        int k = 0;
        while (k < cycleLen) {
            int a = k % n;
            int b = (k + 1) % n;
            ObjectNode e = om.createObjectNode();
            e.put("u", b);
            e.put("v", a);
            e.put("w", 1 + rnd.nextInt(9));
            edges.add(e);
            k = k + 1;
        }
        root.set("edges", edges);
        new JsonGraphIO().write(path, root);
    }
}
