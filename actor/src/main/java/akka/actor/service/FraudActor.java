package akka.actor.service;

import akka.actor.dto.RiskDecision;
import akka.actor.dto.Transaction;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class FraudActor extends AbstractBehavior<Transaction> {

    public static Behavior<Transaction> create() {
        return Behaviors.setup(FraudActor::new);
    }

    private FraudActor(ActorContext<Transaction> context) {
        super(context);
    }

    @Override
    public Receive<Transaction> createReceive() {
        return newReceiveBuilder()
                .onMessage(Transaction.class, this::onTransaction)
                .build();
    }

    private Behavior<Transaction> onTransaction(Transaction txn) {
        getContext().getLog().info("Processing transaction: {}", txn.transactionId);

        RiskDecision decision = evaluate(txn);

        getContext().getLog().info("Decision for txn {}: {}", txn.transactionId, decision);

        return this;
    }

    private RiskDecision evaluate(Transaction txn) {
        if (txn.amount > 10000) {
            return RiskDecision.FLAG;
        } else if (txn.amount > 50000) {
            return RiskDecision.REJECT;
        }
        return RiskDecision.APPROVE;
    }
}
