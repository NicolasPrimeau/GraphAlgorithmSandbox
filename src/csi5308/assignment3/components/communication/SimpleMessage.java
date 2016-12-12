package csi5308.assignment3.components.communication;

import csi5308.assignment3.components.DirectedEdge;
import csi5308.assignment3.components.UUIDItem;

public class SimpleMessage extends UUIDItem implements Message {

    private MessageType type;
    private String message;
    private DirectedEdge direction;

    public SimpleMessage(MessageType type, String message, DirectedEdge direction) {
        super();
        this.type = type;
        this.message = message;
        this.direction = direction;
    }

    @Override
    public MessageType getType() {
        return this.type;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public DirectedEdge getDirection() {
        return this.direction;
    }

    @Override
    public String toString() {
        return "{" + type + ", " + message + ", " + direction + "}";
    }

}
