package csi5308.assignment3.components;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class Graph {

    private final Set<Edge> edges;
    private final Set<Node> nodes;

    public Graph() {
        this.edges = new HashSet<>();
        this.nodes = new HashSet<>();
    }

    public Graph addEdge(Edge e) {
        if (this.edges.contains(e)) {
            return this;
        }
        this.nodes.add(e.getA());
        this.nodes.add(e.getB());
        e.getA().addEdge(e);
        e.getB().addEdge(e);
        edges.add(e);
        return this;
    }

    public Graph addNode(Node n) {
        this.nodes.add(n);
        n.getNeighbours().stream()
                .map(n::getRouteToNeighbour)
                .forEach(this::addEdge);
        return this;
    }

    public void removeNode(Node n) {
        this.nodes.remove(n);
        this.edges.removeIf(e -> e.getB().equals(n) || e.getA().equals(n));
    }

    public Set<Node> getNodes() {
        return this.nodes;
    }

    public Set<Edge> getEdges() {
        return this.edges;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        StringJoiner joiner = new StringJoiner(", ");
        builder.append("Nodes: ");
        nodes.stream().map(Node::toString).forEach(joiner::add);
        builder.append(joiner.toString());
        builder.append("\nEdges: ");
        joiner = new StringJoiner(", ");
        edges.stream().map(Edge::toString).forEach(joiner::add);
        builder.append(joiner.toString());
        builder.append("\n");
        return builder.toString();
    }

}
