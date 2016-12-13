package csi5308.assignment3.simulation.algorithms.YoYo;

import csi5308.assignment3.components.DirectedEdge;
import csi5308.assignment3.components.Node;
import csi5308.assignment3.components.communication.MessageType;
import csi5308.assignment3.components.communication.SimpleMessage;
import csi5308.assignment3.simulation.Time;
import csi5308.assignment3.simulation.algorithms.AlgorithmicController;
import csi5308.assignment3.simulation.SimulationEvent;

import java.util.*;

public class NodeController implements AlgorithmicController {

    private final Node node;
    private Collection<DirectedEdge> dagEdges;
    private NodeSubController subController;
    private NodeState state;

    public NodeController(Node node) {
        this.node = node;
        dagEdges = new HashSet<>();
        state = NodeState.DAG_SENDING;
    }

    @Override
    public Collection<SimulationEvent> step(Time t) {
        switch(state) {
            case DAG_SENDING: state = setupDAG(); break;
            case DAG_RECEIVING: state = setupEdges(); break;
            case INTERNAL: state = subController.act(); break;
            case SOURCE: state = subController.act(); break;
            case SINK: state = subController.act(); break;
            case ELECTED: broadcastWin(); state = NodeState.STOP; break;
            case STOP:
            default: break;
        }
        List<SimulationEvent> events = new LinkedList<>();
        events.add(new SimulationEvent(t.increment(1), this));
        return events;
    }

    private void broadcastWin() {
        this.node.getNeighbours().stream()
                .map(nei -> new DirectedEdge(this.node, nei))
                .map(e -> new SimpleMessage(MessageType.ELECTED, String.valueOf(this.node.getId()), e))
                .forEach(m -> this.node.getMailbox().sendMessage(m));
    }

    private void broadcastId() {
        this.node.getNeighbours().forEach(nei -> node.getMailbox()
                .sendMessage(new SimpleMessage(MessageType.ID, String.valueOf(node.getId()), new DirectedEdge(node, nei))));
    }

    private NodeState setupDAG() {
        broadcastId();
        return NodeState.DAG_RECEIVING;
    }

    private NodeState setupEdges () {
        node.getMailbox().getAllMessages().stream()
                .filter(m -> m.getType() == MessageType.ID)
                .forEach(m -> {
                    DirectedEdge e = (Integer.valueOf(m.getMessage()) > node.getId() ? m.getDirection().reverse() : m.getDirection());
                    if (!this.dagEdges.contains(e)) {
                        node.getMailbox().removeMessage(m);
                        this.dagEdges.add(e);
                    }
                });

        if (this.dagEdges.size() == this.node.getAllEdges().size()) {
            subController = new NodeSubController(this.node, this.dagEdges);
            return subController.getState();
        } else {
            return NodeState.DAG_RECEIVING;
        }
    }

    @Override
    public String toString() {
        return "Subject: " + this.node + "\nState: " + this.state +
                "\n" + this.node.getMailbox() + "\nDAG: " +
                this.dagEdges.stream().map(DirectedEdge::toString).reduce((s1, s2) -> s1 + ", " + s2).orElse("") + "\n";
    }

    @Override
    public String reducedString() {
        return this.node + "=" + this.state + "," +
                this.dagEdges.stream().map(DirectedEdge::toString).reduce((s1, s2) -> s1 + "," + s2).orElse("");
    }

    @Override
    public boolean done() {
        return this.state == NodeState.STOP;
    }

}
