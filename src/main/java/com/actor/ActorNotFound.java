package com.actor;

public class ActorNotFound extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -4646806074546648300L;
    private String id;
    public ActorNotFound(String id) {
        this.id = id;
    }
    public String toString() {
        return "No such actor " + id;
    }
}