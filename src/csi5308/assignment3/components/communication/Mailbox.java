package csi5308.assignment3.components.communication;

import java.util.Collection;
import java.util.List;

public interface Mailbox {
    boolean hasUnreadMessages();
    void removeMessages(Collection<Message> m);
    void removeMessage(Message m);
    List<Message> getUnreadMessages();
    List<Message> getUnreadMessages(Collection<MessageType> filters);
    List<Message> getUnreadMessages(MessageType... filters);
    List<Message> getAllMessages();
    void deleteAllMessages();
    void depositMail(Message m);

    void sendMessage(Message m);
    Collection<Message> getOutgoingMail();
}
