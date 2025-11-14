package com.banking.model;

import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private String accountId;
    private String transactionType;
    private double amount;
    private String relatedAccountId;
    private LocalDateTime timestamp;
    private String status;
    private String remarks;

    public Transaction(String accountId, String transactionType, double amount,
                       String relatedAccountId, String status, String remarks) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.relatedAccountId = relatedAccountId;
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.remarks = remarks;
    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    public String getAccountId() { return accountId; }
    public String getTransactionType() { return transactionType; }
    public double getAmount() { return amount; }
    public String getRelatedAccountId() { return relatedAccountId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public String getRemarks() { return remarks; }
}
