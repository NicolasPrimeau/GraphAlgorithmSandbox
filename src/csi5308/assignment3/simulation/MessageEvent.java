package csi5308.assignment3.simulation;

import csi5308.assignment3.components.communication.Message;

public class MessageEvent {

    private final Time t;
    private final Message message;

    public MessageEvent(Time t, Message message) {
        this.t = t;
        this.message = message;
    }

    public Time getTime() {
        return this.t;
    }

    public Message getMessage() {
        return this.message;
    }
}
