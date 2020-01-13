package com.actor;

public class CounterActor extends Actor {
    public final int INC_COUNTER = 1;
    public int counter = 0;

    public CounterActor() {
        name = "counter";
    }

    @Override
    protected void onMessage(Message msg) {
        switch (msg.id) {
            case INC_COUNTER:
            synchronized(this) {
                counter ++;
                System.out.println("Counter incremented to " + counter);
            }
        }
    }

    public synchronized int getCounter() {
        return counter;
    }
}