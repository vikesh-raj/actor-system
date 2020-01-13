package com.actor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class ActorTest {
    /**
     * Actor tests.
     * 
     * @throws ActorNotFound
     * @throws ShutdownInProgressException
     * @throws MailboxFullException
     * @throws ActorNotAliveException
     * @throws InterruptedException
     */
    @Test
    public void testFirstMessage() throws ActorNotAliveException, MailboxFullException, ShutdownInProgressException,
            ActorNotFound, InterruptedException {

        ActorSystem actorSystem = new ActorSystem(2, 2);
        CounterActor counter = new CounterActor();
        actorSystem.addActor(counter);

        actorSystem.sendMessage(counter.getID(), new Message(counter.INC_COUNTER, null));
        actorSystem.sendMessage(counter.getID(), new Message(counter.INC_COUNTER, null));

        actorSystem.removeActor(counter.getID());
        actorSystem.Shutdown();
        assertEquals(2, counter.getCounter());
    }

    @Test
    public void testMultiSend()
            throws ActorNotAliveException, MailboxFullException, ShutdownInProgressException, ActorNotFound,
            InterruptedException {
        ActorSystem actorSystem = new ActorSystem(2, 2);
        CounterActor counter = new CounterActor();
        actorSystem.addActor(counter);

        int sendCounter = 0;
        while(sendCounter < 100) {
            try {
                actorSystem.sendMessage(counter.getID(), new Message(counter.INC_COUNTER, null));
            } catch( MailboxFullException e) {
                Thread.sleep(20);
                continue;
            }
            sendCounter++;
        }

        actorSystem.removeActor(counter.getID());
        actorSystem.Shutdown();
        assertEquals(100, counter.getCounter());
    }

    @Test
    public void testMultiActors()
            throws ActorNotAliveException, MailboxFullException, ShutdownInProgressException, ActorNotFound {
        ActorSystem actorSystem = new ActorSystem(2, 2);
        CounterActor counter1 = new CounterActor();
        CounterActor counter2 = new CounterActor();
        actorSystem.addActor(counter1);
        actorSystem.addActor(counter2);

        actorSystem.sendMessage(counter1.getID(), new Message(counter1.INC_COUNTER, null));
        actorSystem.sendMessage(counter2.getID(), new Message(counter2.INC_COUNTER, null));

        actorSystem.removeActor(counter1.getID());
        actorSystem.removeActor(counter2.getID());
        actorSystem.Shutdown();
        assertEquals(1, counter1.getCounter());
        assertEquals(1, counter2.getCounter());
    }

    @Test
    public void testPingPong()
            throws ActorNotAliveException, MailboxFullException, ShutdownInProgressException, ActorNotFound,
            InterruptedException {

        final int MAX_MESSAGES = 10;
        ActorSystem actorSystem = new ActorSystem(2, 2);
        PingPongActor actor1 = new PingPongActor();
        PingPongActor actor2 = new PingPongActor();
        actorSystem.addActor(actor1);
        actorSystem.addActor(actor2);
        actor1.init(actorSystem, actor2.getID(), MAX_MESSAGES);
        actor2.init(actorSystem, actor1.getID(), MAX_MESSAGES);

        // Prime the messages.
        actorSystem.sendMessage(actor1.getID(), new Message(actor1.PING, null));

        // Wait a little bit for messages to get ping ponged.
        Thread.sleep(30);
        actorSystem.removeActor(actor1.getID());
        actorSystem.removeActor(actor2.getID());
        actorSystem.Shutdown();
        assertEquals(MAX_MESSAGES, actor1.getCounter());
        assertEquals(MAX_MESSAGES, actor2.getCounter());
    }
}
