package io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.core.Graph;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class JsonGraphIO {
    private ObjectMapper mapper = new ObjectMapper();

    public Graph read(String path) throws IOException {
        JsonNode root = mapper.readTree(new File(path));
        boolean directed = root.get("directed").asBoolean();
        int n = root.get("n").asInt();
        Graph g = new Graph(directed, n);

        JsonNode edges = root.get("edges");
        Iterator<JsonNode> it = edges.elements();
        while (it.hasNext()) {
            JsonNode e = it.next();
            int u = e.get("u").asInt();
            int v = e.get("v").asInt();
            int w = e.get("w").asInt();
            g.addEdge(u, v, w);
        }
        return g;
    }

    public void write(String path, JsonNode node) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), node);
    }
}
