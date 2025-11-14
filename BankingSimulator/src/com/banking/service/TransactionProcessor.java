package com.banking.service;

import com.banking.database.DatabaseConnection;
import com.banking.exception.*;
import com.banking.model.Account;
import com.banking.model.Transaction;

import java.sql.*;

public class TransactionProcessor {
    private AccountManager accountManager;
    private BalanceAlertTracker alertTracker;

    public TransactionProcessor(AccountManager accountManager, BalanceAlertTracker alertTracker) {
        this.accountManager = accountManager;
        this.alertTracker = alertTracker;
    }

    public void deposit(String accountId, double amount) throws AccountNotFoundException,
            InvalidAmountException,
            DatabaseException {
        validateAmount(amount);
        Account account = accountManager.getAccount(accountId);

        double newBalance = account.getBalance() + amount;
        accountManager.updateBalance(accountId, newBalance);

        Transaction transaction = new Transaction(accountId, "DEPOSIT", amount, null, "SUCCESS",
                "Deposit successful");
        logTransaction(transaction);

        System.out.printf("Deposited %.2f to account %s. New balance: %.2f%n",
                amount, accountId, newBalance);
    }

    public void withdraw(String accountId, double amount) throws AccountNotFoundException,
            InvalidAmountException,
            InsufficientBalanceException,
            DatabaseException {
        validateAmount(amount);
        Account account = accountManager.getAccount(accountId);

        if (account.getBalance() < amount) {
            Transaction transaction = new Transaction(accountId, "WITHDRAWAL", amount, null,
                    "FAILED", "Insufficient balance");
            logTransaction(transaction);
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance. Available: %.2f, Requested: %.2f",
                            account.getBalance(), amount));
        }

        double newBalance = account.getBalance() - amount;
        accountManager.updateBalance(accountId, newBalance);

        Transaction transaction = new Transaction(accountId, "WITHDRAWAL", amount, null,
                "SUCCESS", "Withdrawal successful");
        logTransaction(transaction);

        System.out.printf("Withdrew %.2f from account %s. New balance: %.2f%n",
                amount, accountId, newBalance);

        alertTracker.checkAndAlert(accountId);
    }

    public void transfer(String fromAccountId, String toAccountId, double amount)
            throws AccountNotFoundException, InvalidAmountException,
            InsufficientBalanceException, DatabaseException {

        validateAmount(amount);

        if (fromAccountId.equals(toAccountId)) {
            throw new InvalidAmountException("Cannot transfer to the same account");
        }

        Account fromAccount = accountManager.getAccount(fromAccountId);
        Account toAccount = accountManager.getAccount(toAccountId);

        if (fromAccount.getBalance() < amount) {
            Transaction transaction = new Transaction(fromAccountId, "TRANSFER_OUT", amount,
                    toAccountId, "FAILED", "Insufficient balance");
            logTransaction(transaction);
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance for transfer. Available: %.2f, Requested: %.2f",
                            fromAccount.getBalance(), amount));
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            double newFromBalance = fromAccount.getBalance() - amount;
            accountManager.updateBalance(fromAccountId, newFromBalance);

            double newToBalance = toAccount.getBalance() + amount;
            accountManager.updateBalance(toAccountId, newToBalance);

            Transaction outTransaction = new Transaction(fromAccountId, "TRANSFER_OUT", amount,
                    toAccountId, "SUCCESS",
                    "Transfer to " + toAccountId);
            Transaction inTransaction = new Transaction(toAccountId, "TRANSFER_IN", amount,
                    fromAccountId, "SUCCESS",
                    "Transfer from " + fromAccountId);

            logTransaction(outTransaction);
            logTransaction(inTransaction);

            conn.commit();

            System.out.printf("Transferred %.2f from %s to %s%n", amount, fromAccountId, toAccountId);
            System.out.printf("%s new balance: %.2f%n", fromAccountId, newFromBalance);
            System.out.printf("%s new balance: %.2f%n", toAccountId, newToBalance);

            alertTracker.checkAndAlert(fromAccountId);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error");
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            throw new DatabaseException("Transfer failed", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
        if (amount > 1000000) {
            throw new InvalidAmountException("Amount exceeds maximum transaction limit");
        }
    }

    private void logTransaction(Transaction transaction) throws DatabaseException {
        String insertQuery = """
            INSERT INTO transactions (account_id, transaction_type, amount, related_account_id, status, remarks) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, transaction.getAccountId());
            pstmt.setString(2, transaction.getTransactionType());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setString(4, transaction.getRelatedAccountId());
            pstmt.setString(5, transaction.getStatus());
            pstmt.setString(6, transaction.getRemarks());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Error logging transaction", e);
        }
    }
}
