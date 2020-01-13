package com.actor;

public class ActorNotAliveException extends BaseException {
    /**
     *
     */
    private static final long serialVersionUID = 5049087654504981033L;

    public ActorNotAliveException(String actorName, String id) {
        super(actorName, id);
    }

    public String toString() {
        return "Actor not avivel. actor " + name + " , id " + id;
    }
}