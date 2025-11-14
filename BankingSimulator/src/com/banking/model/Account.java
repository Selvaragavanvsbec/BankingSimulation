package com.banking.model;

import java.time.LocalDateTime;

public class Account {
    private String accountId;
    private String accountHolderName;
    private String email;
    private double balance;
    private double minBalanceThreshold;
    private LocalDateTime createdAt;

    public Account(String accountId, String accountHolderName, String email, double initialBalance, double minBalanceThreshold) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.email = email;
        this.balance = initialBalance;
        this.minBalanceThreshold = minBalanceThreshold;
        this.createdAt = LocalDateTime.now();
    }

    public String getAccountId() { return accountId; }
    public String getAccountHolderName() { return accountHolderName; }
    public String getEmail() { return email; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getMinBalanceThreshold() { return minBalanceThreshold; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("Account[ID=%s, Holder=%s, Balance=%.2f, Email=%s]",
                accountId, accountHolderName, balance, email);
    }
}
