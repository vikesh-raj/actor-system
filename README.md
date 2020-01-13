# actor-system

Simple actor system.

Main classes:

  * Actor: Each actor has mailbox to which messages can be posted.
  * ActorSystem: Schedules the actors in a thread pool.

There are two custom actors:

  * CounterActor: It increments a counter whenever an even is recieved.
  * PingPongActor: Sends pong upon recieving ping and vice-versa. It needs to be intialized with the other actor.

