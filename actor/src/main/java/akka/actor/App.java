package akka.actor;

import akka.actor.dto.Transaction;
import akka.actor.service.FraudActor;
import akka.actor.typed.ActorSystem;
/**
 * Entry point
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        ActorSystem<Transaction> system = ActorSystem.create(FraudActor.create(), "FraudSystem");

        system.tell(new Transaction("txn-001", 2000.0, "user123"));
        system.tell(new Transaction("txn-002", 15000.0, "user456"));
        system.tell(new Transaction("txn-003", 60000.0, "user789"));
    }
}
