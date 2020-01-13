package com.actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorSystem {

    private int lastActorID;
    private boolean isAlive;
    private int maxMailboxSize;
    private Map<String, Actor> actors;
    private ArrayList<Thread> threadPool;

    public ActorSystem(int maxThreads, int maxMailboxSize) {
        this.maxMailboxSize = maxMailboxSize;
        actors = new HashMap<>();
        threadPool = new ArrayList<>(maxThreads);
        isAlive = true;
        for (int i = 0; i < maxThreads; i++) {
            Thread thread = new WorkerThread("thread" + i);
            threadPool.add(thread);
            thread.start();
        }
    }

    private String makeID(int id) {
        return "act" + id;
    }

    public synchronized void addActor(Actor actor) {
        String id = makeID(lastActorID);
        lastActorID++;
        actor.Init(id, maxMailboxSize);
        actors.put(id, actor);
    }

    public synchronized void removeActor(String id) throws ActorNotFound {
        Actor actor = actors.get(id);
        if (actor == null) {
            throw new ActorNotFound(id);
        }
        actor.Shutdown();
        System.out.println("Actor removed " + id);
    }

    public synchronized void sendMessage(String id, Message msg)
            throws ActorNotAliveException, MailboxFullException, ShutdownInProgressException, ActorNotFound {
        Actor actor = actors.get(id);
        if (actor == null) {
            throw new ActorNotFound(id);
        }
        actor.SendMessage(msg);
        notify();
    }

    public void Shutdown() {
        synchronized(this) {
            isAlive = false;
            notifyAll();
        }
        for (Thread t : threadPool) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Shutdown complete");
    }

    private Actor getRunnableActor() {
        // TODO: Randomize the order in which actor's are traversed, to avoid starvation.
        for(Actor actor: actors.values()) {
            if (actor.hasMessages()) {
                return actor;
            }
        }
        return null;
    }

    private class WorkerThread extends Thread {
        public WorkerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Actor actor = null;
                    synchronized (ActorSystem.this) {
                        while (isAlive) {
                            actor = getRunnableActor();
                            if (actor != null) {
                                break;
                            }
                            ActorSystem.this.wait();
                        }

                        if (!isAlive) {
                            return;
                        }
                    }

                    actor.ProcessMessage();
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}