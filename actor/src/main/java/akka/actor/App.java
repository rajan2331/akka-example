package akka.actor;

import akka.actor.dto.Transaction;
import akka.actor.service.FraudActor;
import akka.actor.service.KafkaConsumerActor;
import akka.actor.typed.ActorSystem;
/**
 * Entry point
 *
 */
public class App 
{
    public static void main( String[] args )
    {        ActorSystem<Transaction> fraudActor =
    ActorSystem.create(FraudActor.create(), "FraudActorSystem");

    ActorSystem<KafkaConsumerActor.Start> kafkaActor =
    		ActorSystem.create(KafkaConsumerActor.create(fraudActor), "KafkaConsumerSystem");

    kafkaActor.tell(new KafkaConsumerActor.Start());
}
}
