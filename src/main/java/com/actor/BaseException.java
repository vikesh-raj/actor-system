package com.actor;

public class BaseException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -6425510955360308678L;
    protected String name;
    protected String id;
    public BaseException(String actorName, String id) {
        this.name = actorName;
        this.id = id;
    }

    public String toString() {
        return "Base Exception for actor " + this.name + " , id " + this.id;
    }
}