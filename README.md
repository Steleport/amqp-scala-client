# Simple Scala AMQP client

[![Build Status](https://travis-ci.org/Spacelift/amqp-scala-client.svg?branch=develop)](https://travis-ci.org/Spacelift/amqp-scala-client)

Note: This project is undergoing heavy development as it pulls away from the fork of sstone/amqp-client. Unlike the corresponding break of akka-mq-proxies, we will retain its status as a direct fork until further notice.

Also, this README will change to reflect the new repo shortly.

## Why The Break?

The original amqp-client is pretty stable, but also hasn't seen updates in almost a year now. I will attempt to address issues from the original project's tracker where applicable. The project name has also been slightly adjusted to accomodate for the fact that the RabbitMQ AMQP client library has the same output name, when project and Scala version are omitted. Given that this project (for the time being, this may change) uses said library, that only further aids the confusion.

I also am rearranging the project somewhat, moving out sample code from the actual library, and moving entire classes out of this library and into the Akka-MQ-Proxies fork, where reasonable. This is due to the need for further abstraction as the Akka-MQ-Proxies fork will support more than just AMQP, and certain classes emerge from that change more relevant to the MQ proxies library. Understandably, these changes are divergent enough from the original intent of the projects this and the sister project are based on, such that it warranted the major version increment, plus renames.

## What Specifically Will Change?

* All RPC code (RPCServer/RPCClient/IProcessor) will move to Akka-MQ-Proxies (in progress, but already removed from this project)

## What Won't Change?

* License, and original copyright attribution, as Fabrice Drouin's (sstone) hard work will still be at the heart of this project to a healthy degree. Were this a complete rewrite, this wouldn't have been a fork in the first place.
* Expected behavior of the Consumer, ChannelOwner and ConnectionOwner classes, and their supporting case classes in the Amqp object.

## Overview

This client provides a simple API for

* publishing and consuming messages over AMQP
* automatic reconnection

It is based on the [Akka](http://akka.io/) 2.4 framework.

## Limitations and compatibility issues

* This client is compatible with AMQP 0.9.1, not AMQP 1.0.
* This client is most probably not easily usable from Java

## Configuring maven/sbt

* releases and milestones are pushed to maven central
* snapshots are pushed to the sonatype snapshot repository

```xml
<repositories>
    <repository>
        <id>sonatype snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>space.spacelift</groupId>
    <artifactId>amqp-scala-client_SCALA-VERSION</artifactId>
    <version>2.0.0</version>
  </dependency>
  <dependency>
    <groupId>com.typesafe.akka</groupId>
    <artifactId>akka-actor_SCALA-VERSION</artifactId>
    <version>AKKA-VERSION</version>
  </dependency>
</dependencies>
```

Please note that the Akka dependency is now in the "provided" scope which means that you'll have to define it explicitly in your
maven/sbt projects. 

The latest snapshot (development) version is 2.0.0-SNAPSHOT, the latest released version is 2.0.0

* amqp-client 2.0-SNAPSHOT is compatible with Scala 2.12 and Akka 2.4.16

## Library design

This is a thin wrapper over the RabbitMQ java client, which tries to take advantage of the nice actor model provided
by the Akka library. There is no effort to "hide/encapsulate" the RabbitMQ library (and I don't really see the point anyway
since AMQP is a binary protocol spec, not an API spec).
So to make the most of this library you should first check the documentation for the RabbitMQ client, and learn a bit
about AMQP 0.9.1. There are very nice tutorial on the [RabbitMQ](http://www.rabbitmq.com/) website, and
also [there](http://www.zeromq.org/whitepapers:amqp-analysis), and probably many other...

### Connection and channel management

* AMQP connections are equivalent to "physical" connections. They are managed by ConnectionOwner objects. Each ConnectionOwner
 object manages a single connection and will try and reconnect when the connection is lost.
* AMQP channels are multiplexed over AMQP connections. You use channels to publish and consume messages. Channels are managed
by ChannelOwner objects.

ConnectionOwner and ChannelOwner are implemened as Akka actors:
* channel owners are created by connection owners
* when a connection is lost, the connection owner will create a new connection and provide each of its children with a
new channel
* connection owners and channel owners are implemented as Finite State Machines, with 2 possible states: Connected and Disconnected
* For a connection owner, "connected" means that it owns a valid connection to the AMQP broker
* For a channel owner, "connected" means that it owns a valid AMQP channel

YMMV, but using few connections (one per JVM) and many channels per connection (one per thread) is a common practice.

### Wrapping the RabbitMQ client

As explained above, this is an actor-based wrapper around the RabbitMQ client, with 2 main classes: ConnectionOwner and
ChannelOwner. Instead of calling the RabbitMQ [Channel](http://www.rabbitmq.com/releases/rabbitmq-java-client/v3.1.1/rabbitmq-java-client-javadoc-3.1.1/com/rabbitmq/client/Channel.html)
interface, you send a message to a ChannelOwner actor, which replies with whatever the java client returned wrapped in an Amqp.Ok()
message if the call was successful, or an Amqp.Error if it failed.

For example, to declare a queue you could write:

``` scala

  val connFactory = new ConnectionFactory()
  connFactory.setUri("amqp://guest:guest@localhost/%2F")
  val conn = system.actorOf(ConnectionOwner.props(connFactory, 1 second))
  val channel = ConnectionOwner.createChildActor(conn, ChannelOwner.props())

  channel ! DeclareQueue(QueueParameters("my_queue", passive = false, durable = false, exclusive = false, autodelete = true))

```

Or, if you want to check the number of messages in a queue:

``` scala

  val connFactory = new ConnectionFactory()
  connFactory.setUri("amqp://guest:guest@localhost/%2F")
  val conn = system.actorOf(ConnectionOwner.props(connFactory, 1 second))
  val channel = ConnectionOwner.createChildActor(conn, ChannelOwner.props())

  val Amqp.Ok(_, Some(result: Queue.DeclareOk)) = Await.result(
    (channel ? DeclareQueue(QueueParameters(name = "my_queue", passive = true))).mapTo[Amqp.Ok],
    5 seconds
  )
  println("there are %d messages in the queue named %s".format(result.getMessageCount, result.getQueue))

```

### Initialization and failure handling

If the connection to the broker is lost, ConnectionOwner actors will try and reconnect, and once they are connected
again they will send a new AMQP channel to each of their ChannelOwner children.

Likewise, if the channel owned by a ChannelOwner is shut down because of an error it will request a new one from its parent.

In this case you might want to "replay" some of the messages that were sent to the ChannelOwner actor before it lost
its channel, like queue declarations and bindings.

For this, you have 2 options:
* initialize the ChannelOwner with a list of requests
* wrap requests inside a Record message

Here, queues and bindings will be gone if the connection is lost and restored:

``` scala

  val connFactory = new ConnectionFactory()
  connFactory.setUri("amqp://guest:guest@localhost/%2F")
  val conn = system.actorOf(ConnectionOwner.props(connFactory, 1 second))

  // create an actor that will receive AMQP deliveries
  val listener = system.actorOf(Props(new Actor {
    def receive = {
      case Delivery(consumerTag, envelope, properties, body) => {
        println("got a message: " + new String(body))
        sender ! Ack(envelope.getDeliveryTag)
      }
    }
  }))

  // create a consumer that will route incoming AMQP messages to our listener
  // it starts with an empty list of queues to consume from
  val consumer = ConnectionOwner.createChildActor(conn, Consumer.props(listener, channelParams = None, autoack = false))

  // wait till everyone is actually connected to the broker
  Amqp.waitForConnection(system, consumer).await()

  // create a queue, bind it to a routing key and consume from it
  // here we don't wrap our requests inside a Record message, so they won't replayed when if the connection to
  // the broker is lost: queue and binding will be gone

  // create a queue
  val queueParams = QueueParameters("my_queue", passive = false, durable = false, exclusive = false, autodelete = true)
  consumer ! DeclareQueue(queueParams)
  // bind it
  consumer ! QueueBind(queue = "my_queue", exchange = "amq.direct", routing_key = "my_key")
  // tell our consumer to consume from it
  consumer ! AddQueue(QueueParameters(name = "my_queue", passive = false))

```

We can initialize our consumer with a list of messages that will be replayed each time its receives a new channel:

``` scala

 val consumer = ConnectionOwner.createChildActor(conn, Consumer.props(
    listener = Some(listener),
    init = List(AddBinding(Binding(StandardExchanges.amqDirect, queueParams, "my_key")))
  ), name = Some("consumer"))

```

Or can can wrap our initialization messages with Record to make sure they will be replayed each time its receives a new channel:

``` scala

  consumer ! Record(AddBinding(Binding(StandardExchanges.amqDirect, QueueParameters("my_queue", passive = false, durable = false, exclusive = false, autodelete = true), "my_key")))

```

If you have a reason to add a heartbeat (for instance, to keep your load balancer from dropping the connection), you can easily do so:

``` scala
  val connFactory = new ConnectionFactory()
  connFactory.setRequestedHeartbeat(5) // seconds
```

## Samples

You can check either samples [src/samples/scala](https://github.com/spacelift/amqp-scala-client/tree/develop/src/samples/scala/) or spec tests [src/test/scala/space/spacelift/amqp](https://github.com/spacelift/amqp-scala-client/tree/develop/src/test/scala/space/spacelift/amqp) for examples of how to use the library.





