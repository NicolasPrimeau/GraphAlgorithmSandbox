package csi5308.assignment3.components.communication;

import csi5308.assignment3.components.DirectedEdge;

public interface Message {
    MessageType getType();
    String getMessage();
    DirectedEdge getDirection();
    @Override
    String toString();
}
