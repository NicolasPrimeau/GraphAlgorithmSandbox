package csi5308.assignment3.simulation.algorithms.YoYo;

import csi5308.assignment3.components.DirectedEdge;
import csi5308.assignment3.components.Node;
import csi5308.assignment3.components.communication.Message;
import csi5308.assignment3.components.communication.MessageType;
import csi5308.assignment3.components.communication.SimpleMessage;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

class NodeSubController {

    private enum State {
        TICK, TOCK
    }

    private Collection<DirectedEdge> edges;
    private Node node;
    private Collection<Message> buffer;
    private NodeState state;
    private State iState;

    NodeSubController(Node n, Collection<DirectedEdge> edges) {
        this.node = n;
        this.edges = edges;
        iState = State.TICK;
        buffer = new LinkedList<>();
        this.state = this.dagStatus();
    }

    private NodeState dagStatus() {
        if (state == NodeState.SOURCE && edges.size() == 0) {
            return NodeState.ELECTED;
        } else if (edges.size() == 0 && state != NodeState.SOURCE) {
            return NodeState.STOP;
        }

        long outgoing = edges.stream().filter(edge -> edge.getDeparture().equals(node)).count();
        long incoming = edges.size() - outgoing;

        if (incoming == 0 && outgoing == edges.size()) {
            return NodeState.SOURCE;
        } else if (outgoing == 0 && incoming == edges.size()) {
            return NodeState.SINK;
        } else {
            return NodeState.INTERNAL;
        }
    }

    NodeState getState() {
        return this.state;
    }

    NodeState act() {
        switch(state) {
            case INTERNAL:
                internalNode();
                this.state = dagStatus();
                break;
            case SINK:
                sinkNode();
                this.state = dagStatus();
                break;
            case SOURCE:
                sourceNode();
                this.state = dagStatus();
                break;
            default:
                this.state = dagStatus();
                break;
        }
        return this.state;
    }

    private void sourceNode() {
        if (iState == State.TICK) {
            if (this.edges.isEmpty()) {
                return;
            }
            this.edges.stream()
                    .filter(edge -> edge.getDeparture().equals(this.node))
                    .forEach(edge -> this.node.getMailbox().sendMessage(
                            new SimpleMessage(MessageType.ID, String.valueOf(this.node.getId()), edge.copy())));

            iState = State.TOCK;

        } else {
            this.buffer.addAll(this.node.getMailbox().getAllMessages().stream()
                    .filter(m -> m.getType() == MessageType.ANSWER || m.getType() == MessageType.PRUNE)
                    .map(m -> node.getMailbox().removeMessage(m)).collect(Collectors.toList()));

            if (this.buffer.size() == this.edges.size()) {
                processAnswers();
                iState = State.TICK;
            }
        }
    }

    private void internalNode() {
        if (iState == State.TICK) {
            int inbound = (int)this.edges.stream().filter(e -> e.getArrival().equals(this.node)).count();
            this.buffer.addAll(this.node.getMailbox().getAllMessages().stream()
                    .filter(m -> m.getType() == MessageType.ID)
                    .map(m -> node.getMailbox().removeMessage(m)).collect(Collectors.toList()));
            if (this.buffer.size() == inbound) {

                int smallest = this.buffer.stream()
                        .mapToInt(m -> Integer.valueOf(m.getMessage()))
                        .min()
                        .orElseThrow(IllegalStateException::new);

                this.edges.stream()
                        .filter(edge -> edge.getDeparture().equals(this.node))
                        .map(edge -> new SimpleMessage(MessageType.ID, Long.toString(smallest), edge.copy()))
                        .forEach(m -> this.node.getMailbox().sendMessage(m));

                iState = State.TOCK;
            }
        } else {
            this.buffer.addAll(this.node.getMailbox().getAllMessages().stream()
                    .filter(m -> m.getType() == MessageType.ANSWER || m.getType() == MessageType.PRUNE)
                    .map(m -> node.getMailbox().removeMessage(m)).collect(Collectors.toList()));
            if (this.buffer.size() == this.edges.size()) {
                generateAnswers();
                processAnswers();
                this.iState = State.TICK;
            }
        }
    }

    private void sinkNode() {
        this.buffer.addAll(this.node.getMailbox().getAllMessages().stream()
                .filter(m -> m.getType() == MessageType.ID)
                .map(m -> node.getMailbox().removeMessage(m)).collect(Collectors.toList()));

        if (this.buffer.size() == this.edges.size()) {
            generateAnswers();
            buffer.clear();
            this.iState = State.TICK;
        }
    }

    private void reverseEdge(DirectedEdge edge) {
        if (!this.edges.contains(edge)) {
            throw new IllegalStateException();
        }
        this.edges.remove(edge);
        this.edges.add(edge.reverse());
    }

    private void processAnswers() {
        for (Message m : this.buffer) {
            if (m.getType() == MessageType.PRUNE) {
                edges.remove(m.getDirection().reverse());
            } else if(m.getType() == MessageType.ANSWER && !Boolean.valueOf(m.getMessage())) {
                reverseEdge(m.getDirection().reverse());
            }
        }
        this.buffer.clear();
    }

    private void generateAnswers() {
        Collection<Message> answers = this.buffer.stream()
                .filter(m -> m.getType() == MessageType.ANSWER || m.getType() == MessageType.PRUNE)
                .collect(Collectors.toList());

        Collection<Message> ids = this.buffer.stream()
                .filter(m -> m.getType() == MessageType.ID)
                .collect(Collectors.toList());

        if (!answers.isEmpty() && answers.stream().noneMatch(m -> Boolean.valueOf(m.getMessage()))) {
            ids.stream()
                    .map(m -> {
                        reverseEdge(m.getDirection());
                        return new SimpleMessage(MessageType.ANSWER, String.valueOf(false), m.getDirection().reverse());
                    }).forEach(m -> this.node.getMailbox().sendMessage(m));
        } else {
            int smallest = ids.stream()
                    .mapToInt(m -> Integer.valueOf(m.getMessage()))
                    .min()
                    .orElseThrow(IllegalStateException::new);

            boolean prune = answers.stream().filter(m -> m.getType() == MessageType.ANSWER).count() == 0
                            && ids.stream().allMatch(m -> (Integer.valueOf(m.getMessage()) == smallest));

            for (Message m : ids) {
                boolean answer = false;
                MessageType type = edges.size() == 0 ? MessageType.PRUNE : MessageType.ANSWER;

                if ((Integer.valueOf(m.getMessage()) == smallest)) {
                    type = prune ? MessageType.PRUNE : MessageType.ANSWER;
                    answer = true;
                    prune = true;
                }

                if (type == MessageType.PRUNE) {
                    this.edges.remove(m.getDirection());
                } else if (!answer) {
                    reverseEdge(m.getDirection());
                }

                this.node.getMailbox().sendMessage(new SimpleMessage(type, String.valueOf(answer), m.getDirection().reverse()));
            }
        }

    }

}
