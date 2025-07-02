package akka.actor.service;

import akka.actor.dto.Transaction;
import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import akka.kafka.ConsumerSettings;
import akka.kafka.javadsl.Consumer;
import akka.kafka.Subscriptions;
import akka.stream.javadsl.Sink;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.UUID;

public class KafkaConsumerActor extends AbstractBehavior<KafkaConsumerActor.Start> {

    public static class Start {}

    private final ActorRef<Transaction> fraudActor;
    private final ObjectMapper mapper = new ObjectMapper();

    public static Behavior<Start> create(ActorRef<Transaction> fraudActor) {
        return Behaviors.setup(ctx -> new KafkaConsumerActor(ctx, fraudActor));
    }

    private KafkaConsumerActor(ActorContext<Start> context, ActorRef<Transaction> fraudActor) {
        super(context);
        this.fraudActor = fraudActor;
    }

    @Override
    public Receive<Start> createReceive() {
        return newReceiveBuilder()
                .onMessage(Start.class, this::onStart)
                .build();
    }

    private Behavior<Start> onStart(Start msg) {
        var system = getContext().getSystem();

        var consumerSettings = ConsumerSettings
                .create(system, new StringDeserializer(), new StringDeserializer())
                .withBootstrapServers("localhost:9092")
                .withGroupId("fraud-group-" + UUID.randomUUID())
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer.plainSource(consumerSettings, Subscriptions.topics("transactions"))
                .map(consumerRecord -> {
                    String json = consumerRecord.value();
                    return mapper.readValue(json, Transaction.class);
                })
                .to(Sink.foreach(fraudActor::tell))
                .run(system);

        return this;
    }
}
