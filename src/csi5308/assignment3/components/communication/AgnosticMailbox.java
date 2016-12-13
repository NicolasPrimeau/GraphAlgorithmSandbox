package csi5308.assignment3.components.communication;

import java.util.*;

public class AgnosticMailbox implements Mailbox {

    private Set<Message> allMessages;
    private Set<Message> outbox;

    public AgnosticMailbox() {
        this.allMessages = new HashSet<>();
        this.outbox = new HashSet<>();
    }

    @Override
    public Collection<Message> removeMessages(Collection<Message> messages) {
        messages.forEach(this::removeMessage);
        return messages;
    }

    @Override
    public Message removeMessage(Message m) {
        this.allMessages.remove(m);
        return m;
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(this.allMessages);
    }

    @Override
    public void deleteAllMessages() {
        this.allMessages.clear();
        this.outbox.clear();
    }

    @Override
    public void depositMail(Message m) {
        this.allMessages.add(m);
    }

    @Override
    public void sendMessage(Message m) {
        this.outbox.add(m);
    }

    @Override
    public Collection<Message> getOutgoingMail() {
        Set<Message> bundle = new HashSet<>(outbox);
        outbox.clear();
        return bundle;
    }

    @Override
    public String toString() {
        return "All: " + this.allMessages.stream()
                .map(Message::toString)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("") +
                "\nOutgoing: " + this.outbox.stream()
                .map(Message::toString)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");
    }
}
