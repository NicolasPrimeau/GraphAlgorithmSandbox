package csi5308.assignment3.components.communication;

import csi5308.assignment3.components.Graph;
import csi5308.assignment3.components.Node;

import java.util.*;

public class Mailman {

    private Graph neighbourhood;

    public Mailman(Graph neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public long distributeMail() {
        List<Node> nodes = new ArrayList<>(this.neighbourhood.getNodes());
        // Shuffle the other in which nodes are visited
        Collections.shuffle(nodes);
        return nodes.stream()
                .flatMap(e -> e.getMailbox().getOutgoingMail().stream())
                .mapToInt(m -> {m.getDirection().getArrival().getMailbox().depositMail(m); return 1;})
                .count();
    }
}
