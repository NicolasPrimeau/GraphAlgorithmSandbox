package csi5308.assignment3.components.communication;

import csi5308.assignment3.components.Graph;
import csi5308.assignment3.components.Node;
import csi5308.assignment3.simulation.MessageEvent;
import csi5308.assignment3.simulation.Time;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Mailman {

    private final Graph neighbourhood;
    private final Supplier<Long> messageTime;

    public Mailman(Graph neighbourhood, Supplier<Long> messageTime) {
        this.neighbourhood = neighbourhood;
        this.messageTime = messageTime;
    }

    public Collection<MessageEvent> distributeMail(Time time) {
        List<Node> nodes = new ArrayList<>(this.neighbourhood.getNodes());
        // Shuffle the other in which nodes are visited
        Collections.shuffle(nodes);
        return nodes.stream()
                .flatMap(e -> e.getMailbox().getOutgoingMail().stream())
                .map(e -> new MessageEvent(time.increment(this.messageTime.get()), e))
                .collect(Collectors.toList());
    }
}
