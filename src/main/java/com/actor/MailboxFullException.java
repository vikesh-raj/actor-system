package com.actor;

public class MailboxFullException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 6429479829997759949L;

    public MailboxFullException(String actorName, String id) {
        super(actorName, id);
    }

    public String toString() {
        return "Mailbox is full when sending message to actor " + name + " , id " + id;
    }
}