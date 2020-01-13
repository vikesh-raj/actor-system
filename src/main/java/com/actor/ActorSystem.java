package com.actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

public class ActorSystem {

    private int lastActorID;
    private boolean isAlive;
    private int maxMailboxSize;
    private Map<String, Actor> actors;
    private ArrayList<Thread> threadPool;
    private LinkedList<String> threadQueue;

    public ActorSystem(int maxThreads, int maxMailboxSize) {
        this.maxMailboxSize = maxMailboxSize;
        threadQueue = new LinkedList<>();
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
    }

    public synchronized void sendMessage(String id, Message msg)
            throws ActorNotAliveException, MailboxFullException, ShutdownInProgressException, ActorNotFound {
        Actor actor = actors.get(id);
        if (actor == null) {
            throw new ActorNotFound(id);
        }
        actor.SendMessage(msg);
        threadQueue.addLast(id);
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
    }

    private class WorkerThread extends Thread {
        public WorkerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String id;
                    synchronized (ActorSystem.this) {
                        while (threadQueue.isEmpty() && isAlive) {
                            ActorSystem.this.wait();
                        }

                        if (!isAlive) {
                            return;
                        }

                        try {
                            id = threadQueue.removeFirst();
                        } catch(NoSuchElementException e) {
                            continue;
                        }
                    }

                    Actor actor = actors.get(id);
                    if (actor == null) {
                        System.out.println("No actor for id " + id);
                        continue;
                    }

                    actor.ProcessMessage();
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}