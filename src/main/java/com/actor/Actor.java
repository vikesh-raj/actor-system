package com.actor;

import java.util.LinkedList;

public abstract class Actor {

    protected String name = "<default>";
    private String id;
    private int maxMessages;
    private boolean shutdownInProgress = false;
    private boolean alive = false;
    private LinkedList<Message> queue;
    protected abstract void onMessage(Message msg);

    synchronized boolean isAlive() {
        return alive;
    }

    synchronized boolean hasMessages() {
        return !queue.isEmpty();
    }

    public String getID() {
        return id;
    }

    void Init(String id, int maxMessages) {
        this.id = id;
        this.maxMessages = maxMessages;
        this.alive = true;
        queue = new LinkedList<>();
    }

    synchronized void SendMessage(Message msg) throws ActorNotAliveException, MailboxFullException, ShutdownInProgressException{
        if (!alive) {
            throw new ActorNotAliveException(name, id);
        }
        if (shutdownInProgress) {
            throw new ShutdownInProgressException(name, id);
        }
        if (queue.size() == maxMessages) {
            throw new MailboxFullException(name, id);
        }
        queue.addLast(msg);
    }

    // Will be called by the worker.
    boolean ProcessMessage() {
        Message msg;
        synchronized(this) {
            if (queue.isEmpty()) {
                return false;
            }
            msg = queue.removeFirst();
        }
        try {
            System.out.println("OnMessage : msg id " + msg.id + ", actor name " + name + ", id = " + id);
            onMessage(msg);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    void Shutdown() {
        synchronized(this) {
            shutdownInProgress = true;
        }

        boolean hasMessages = true;
        while(hasMessages) {
            hasMessages = ProcessMessage();
        }
        synchronized(this) {
            alive = false;
        }
    }
}