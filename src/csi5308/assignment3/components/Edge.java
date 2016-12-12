package csi5308.assignment3.components;

public class Edge extends UUIDItem {

    private final Node n1;
    private final Node n2;

    public Edge(final Node n1, final Node n2) {
        super();
        this.n1 = n1;
        this.n2 = n2;
    }

    public Node getA() {
        return this.n1;
    }

    public Node getB() {
        return this.n2;
    }

    public boolean canReceive(Node n) {
        return n.equals(n1) || n.equals(n2);
    }

    public boolean canSend(Node n) {
        return n.equals(n1) || n.equals(n2);
    }

    @Override
    public int hashCode() {
        return n1.getId() + n2.getId();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Edge &&
                ((((Edge) other).getA().equals(this.getA()) && ((Edge) other).getB().equals(this.getB())) ||
                (((Edge) other).getA().equals(this.getB()) || ((Edge) other).getB().equals(this.getA())));
    }

    @Override
    public String toString() {
        return this.getA() + "-" + this.getB();
    }
}
