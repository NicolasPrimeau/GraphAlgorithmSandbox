package csi5308.assignment3.components.communication;

import java.util.Collection;
import java.util.List;

public interface Mailbox {
    Collection<Message> removeMessages(Collection<Message> m);
    Message removeMessage(Message m);
    List<Message> getAllMessages();
    void deleteAllMessages();
    void depositMail(Message m);

    void sendMessage(Message m);
    Collection<Message> getOutgoingMail();
}
