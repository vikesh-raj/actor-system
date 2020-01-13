package com.actor;

public class ShutdownInProgressException extends BaseException {
    /**
     *
     */
    private static final long serialVersionUID = 3375047606990589172L;

    public ShutdownInProgressException(String actorName, String id) {
        super(actorName, id);
    }

    public String toString() {
        return "No new messages accepted during shutdown of actor " + name + " , id " + id;
    }
}