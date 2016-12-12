package csi5308.assignment3.components.communication;

import java.util.*;
import java.util.stream.Collectors;

public class AgnosticMailbox implements Mailbox {

    private Queue<Message> unreadMessages;
    private Set<Message> allMessages;
    private Set<Message> outbox;

    public AgnosticMailbox() {
        this.unreadMessages = new LinkedList<>();
        this.allMessages = new HashSet<>();
        this.outbox = new HashSet<>();
    }

    @Override
    public boolean hasUnreadMessages() {
        return unreadMessages.isEmpty();
    }

    @Override
    public void removeMessages(Collection<Message> messages) {
        messages.forEach(this::removeMessage);
    }

    @Override
    public void removeMessage(Message m) {
        this.allMessages.remove(m);
        this.unreadMessages.remove(m);
    }

    @Override
    public List<Message> getUnreadMessages() {
        List<Message> messages = new ArrayList<>(this.unreadMessages);
        this.unreadMessages.clear();
        return messages;
    }

    @Override
    public List<Message> getUnreadMessages(Collection<MessageType> filters) {
        List<Message> wanted = this.unreadMessages.stream()
                .filter(m -> filters.contains(m.getType()))
                .collect(Collectors.toList());
        this.unreadMessages.removeAll(wanted);
        return wanted;
    }

    @Override
    public List<Message> getUnreadMessages(MessageType... filters) {
        return this.getUnreadMessages(new ArrayList<>(Arrays.asList(filters)));
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(this.allMessages);
    }

    @Override
    public void deleteAllMessages() {
        this.allMessages.clear();
        this.unreadMessages.clear();
        this.outbox.clear();
    }

    @Override
    public void depositMail(Message m) {
        this.unreadMessages.add(m);
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
        return "Unread: " + this.unreadMessages.stream()
                .map(Message::toString)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("") +
                "\nAll: " + this.allMessages.stream()
                .map(Message::toString)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("") +
                "\nOutgoing: " + this.outbox.stream()
                .map(Message::toString)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");
    }
}
