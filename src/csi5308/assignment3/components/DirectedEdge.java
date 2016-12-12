package csi5308.assignment3.components;

public class DirectedEdge extends Edge {

    public DirectedEdge(Node from, Node to) {
        super(from, to);
    }

    public Node getDeparture() {
        return this.getA();
    }

    public Node getArrival() {
        return this.getB();
    }

    public DirectedEdge reverse() {
        return new DirectedEdge(this.getArrival(), this.getDeparture());
    }

    public DirectedEdge copy() {
        return new DirectedEdge(this.getDeparture(), this.getArrival());
    }

    @Override
    public boolean canReceive(Node n) {
        return n.equals(this.getArrival());
    }

    @Override
    public boolean canSend(Node n) {
        return n.equals(this.getDeparture());
    }

    @Override
    public int hashCode() {
        return (String.valueOf(this.getDeparture()) + String.valueOf(this.getArrival())).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof DirectedEdge && ((DirectedEdge) other).getArrival().equals(this.getArrival())
                && ((DirectedEdge) other).getDeparture().equals(this.getDeparture());
    }

    @Override
    public String toString() {
        return this.getDeparture() + "->" + this.getArrival();
    }

}
