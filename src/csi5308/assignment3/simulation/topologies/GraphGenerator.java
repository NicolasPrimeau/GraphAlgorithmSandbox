package csi5308.assignment3.simulation.topologies;

import csi5308.assignment3.components.Edge;
import csi5308.assignment3.components.Graph;
import csi5308.assignment3.components.Node;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphGenerator {

    public static Graph generateRandomGraph(int numNodes, Supplier<Boolean> criteria) {
        Graph g = new Graph();
        List<Node> nodes = IntStream.range(0, numNodes).mapToObj(Node::new).collect(Collectors.toList());
        Collections.shuffle(nodes);
        nodes.forEach(g::addNode);
        Random r = new Random();
        List<Node> backbone = new ArrayList<>(nodes.subList(0, r.nextInt(nodes.size())));

        if (backbone.size() == 0) {
            backbone.add(nodes.get(r.nextInt(nodes.size())));
        }

        for (int i=1; i<(backbone.size()-1); i+=1) {
            g.addEdge(new Edge(backbone.get(i), backbone.get(i == 0 ? backbone.size() - 1 : i-1)));
            g.addEdge(new Edge(backbone.get(i), backbone.get(i == backbone.size() - 1 ? 0 : i+1)));
        }

        for (int i=0; i < numNodes; i+=1) {
            for (int j = 0; j < numNodes && j != i; j += 1) {
                if (criteria.get()) {
                    g.addEdge(new Edge(nodes.get(i), nodes.get(j)));
                    if (!backbone.contains(nodes.get(i)) && backbone.contains(nodes.get(j))
                            || backbone.contains(nodes.get(i)) && !backbone.contains(nodes.get(j))) {
                        backbone.add(backbone.contains(nodes.get(i)) ? nodes.get(j) : nodes.get(i));
                    }
                }
            }
        }

        nodes.stream().filter(node -> !backbone.contains(node)).forEach(node -> {
            g.addEdge(new Edge(node, backbone.get(r.nextInt(backbone.size()))));
            backbone.add(node);
        });

        if (backbone.size() != nodes.size()) {
            throw new IllegalStateException("Generic Graph Creation failed!");
        }
        return g;
    }

    public static Graph generateMesh(int dimension) {
        Graph g = new Graph();
        int numNodes = dimension * dimension;
        List<Node> nodes = IntStream.range(0, numNodes).mapToObj(Node::new).collect(Collectors.toList());
        Collections.shuffle(nodes);

        for (int i=0; i<numNodes; i+=1) {
            if (i%dimension == 0) {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+1)));
            } else if ((i+1) % dimension == 0) {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i-1)));
            } else {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+1)));
                g.addEdge(new Edge(nodes.get(i), nodes.get(i-1)));
            }

            if (i/dimension == 0) {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+dimension)));
            } else if (i/dimension == dimension-1) {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i-dimension)));
            } else {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+dimension)));
                g.addEdge(new Edge(nodes.get(i), nodes.get(i-dimension)));
            }
        }
        return g;
    }

    public static Graph generateHyperCube(int dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Dimension must be greater or equal to 0");
        }
        Graph g = new Graph();
        List<Node> nodes = IntStream.range(0, (int)Math.pow(2, dimension)).mapToObj(Node::new).collect(Collectors.toList());
        Collections.shuffle(nodes);
        connect(nodes.subList(0, nodes.size()/2), nodes.subList(nodes.size()/2, nodes.size())).forEach(g::addEdge);
        return g;
    }

    private static List<Edge> connect(List<Node> a, List<Node> b) {
        List<Edge> retList = new LinkedList<>();
        if (a.size() > 1) {
            retList.addAll(connect(a.subList(0, a.size()/2), a.subList(a.size()/2, a.size())));
            retList.addAll(connect(b.subList(0, b.size()/2), b.subList(b.size()/2, b.size())));
        }
        IntStream.range(0, a.size()).mapToObj(i -> new Edge(a.get(i), b.get(i))).forEach(retList::add);
        return retList;

    }

    public static Graph generateTorus(int dimension) {
        Graph g = new Graph();
        int numNodes = dimension * dimension;
        List<Node> nodes = IntStream.range(0, numNodes).mapToObj(Node::new).collect(Collectors.toList());
        Collections.shuffle(nodes);

        for (int i=0; i<numNodes; i+=1) {
            if (i%dimension == 0) {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+1)));
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+dimension-1)));
            } else if ((i+1) % dimension == 0) {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i-1)));
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+1-dimension)));
            } else {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+1)));
                g.addEdge(new Edge(nodes.get(i), nodes.get(i-1)));
            }

            if (i/dimension == 0) {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+dimension)));
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+(dimension-1)*dimension)));
            } else if (i/dimension == dimension-1) {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i-dimension)));
                g.addEdge(new Edge(nodes.get(i), nodes.get(i%dimension)));
            } else {
                g.addEdge(new Edge(nodes.get(i), nodes.get(i+dimension)));
                g.addEdge(new Edge(nodes.get(i), nodes.get(i-dimension)));
            }
        }
        return g;
    }

    public static Graph generateCompleteGraph(int numNodes) {
        Graph g = new Graph();
        List<Node> nodes = IntStream.range(0, numNodes).mapToObj(Node::new).collect(Collectors.toList());
        Collections.shuffle(nodes);
        for (int i=0; i<numNodes; i+=1) {
            for (int j=0; j<numNodes && j !=i; j+=1) {
                g.addEdge(new Edge(nodes.get(i), nodes.get(j)));
            }
        }
        return g;
    }

    public static Graph generateRingGraph(int numNodes) {
        Graph g = new Graph();
        List<Node> nodes = IntStream.range(0, numNodes).mapToObj(Node::new).collect(Collectors.toList());
        Collections.shuffle(nodes);
        for (int i=0; i<numNodes; i+=1) {
            g.addEdge(new Edge(nodes.get(i), nodes.get(i == 0? numNodes-1 : i-1)));
            g.addEdge(new Edge(nodes.get(i), nodes.get(i == numNodes-1? 0 : i+1)));
        }
        return g;
    }
}
