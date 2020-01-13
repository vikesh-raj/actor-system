package com.actor;

public class PingPongActor extends Actor {

    public final int PING = 1;
    public final int PONG = 2;
    private int counter = 0;
    private int max = 0;
    private String otherActor;
    private ActorSystem actorSystem;

    public void init(ActorSystem actorSystem, String otherActorId, int max) {
        this.actorSystem = actorSystem;
        this.otherActor = otherActorId;
        this.max = max;
        counter = 0;
        name = "ping-pong";
    }

    public synchronized int getCounter() {
        return counter;
    }

    private void sendMsg(int id) {
        if (actorSystem == null || otherActor == null) {
            return;
        }
        try {
            actorSystem.sendMessage(otherActor, new Message(id, null));
        } catch (BaseException e) {
            e.printStackTrace();
        } catch (ActorNotFound e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMessage(Message msg) {
        switch(msg.id) {
            case PING:
            synchronized(this) {
                if (counter<max) {
                    System.out.println("Sending PONG to " + otherActor);
                    sendMsg(PONG);
                    counter++;
                }
            }
            break;
            case PONG:
            synchronized(this) {
                if(counter<max) {
                    System.out.println("Sending PING to " + otherActor);
                    sendMsg(PING);
                    counter++;
                }
            }
        }
    }
}