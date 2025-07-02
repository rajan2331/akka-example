package akka.actor.dto;

public class Transaction {
    public final String transactionId;
    public final double amount;
    public final String userId;

    public Transaction(String transactionId, double amount, String userId) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.userId = userId;
    }
}

