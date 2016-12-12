package csi5308.assignment3.components;

import csi5308.assignment3.components.communication.AgnosticMailbox;
import csi5308.assignment3.components.communication.Mailbox;

import java.util.*;

public class Node extends UUIDItem {

    private int id;

    private Map<Node, Edge> neighbours;
    private Mailbox mailbox;

    public Node(int id) {
        this.id = id;
        mailbox = new AgnosticMailbox();
        this.neighbours = new HashMap<>();
    }

    public Node addEdge(Edge e) {
        if (!e.canSend(this)) {
            return this;
        }
        this.neighbours.put(e.getA().equals(this)? e.getB() : e.getA(), e);
        return this;
    }

    public Node removeEdge(Edge e) {
        this.neighbours.remove(e.getA().equals(this) ? e.getB() : e.getA());
        return this;
    }

    public Collection<Edge> getAllEdges() {
        return this.neighbours.values();
    }

    public Mailbox getMailbox() {
        return this.mailbox;
    }

    public Edge getRouteToNeighbour(Node n) {
        return this.neighbours.get(n);
    }

    public Set<Node> getNeighbours() {
        return this.neighbours.keySet();
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.getId()+"";
    }
}
