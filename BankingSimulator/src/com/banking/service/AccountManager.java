package com.banking.service;

import com.banking.database.DatabaseConnection;
import com.banking.exception.AccountNotFoundException;
import com.banking.exception.DatabaseException;
import com.banking.model.Account;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AccountManager {
    private Map<String, Account> accountCache;

    public AccountManager() {
        this.accountCache = new HashMap<>();
        loadAccountsFromDatabase();
    }

    private void loadAccountsFromDatabase() {
        String query = "SELECT * FROM accounts";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Account account = new Account(
                        rs.getString("account_id"),
                        rs.getString("account_holder_name"),
                        rs.getString("email"),
                        rs.getDouble("balance"),
                        rs.getDouble("min_balance_threshold")
                );
                accountCache.put(account.getAccountId(), account);
            }
            System.out.println("Loaded " + accountCache.size() + " accounts from database.");
        } catch (SQLException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    public void createAccount(String accountId, String holderName, String email,
                              double initialBalance, double minThreshold) throws DatabaseException {
        if (accountCache.containsKey(accountId)) {
            throw new DatabaseException("Account already exists with ID: " + accountId, null);
        }

        Account account = new Account(accountId, holderName, email, initialBalance, minThreshold);

        String insertQuery = "INSERT INTO accounts (account_id, account_holder_name, email, balance, min_balance_threshold) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, accountId);
            pstmt.setString(2, holderName);
            pstmt.setString(3, email);
            pstmt.setDouble(4, initialBalance);
            pstmt.setDouble(5, minThreshold);

            pstmt.executeUpdate();
            accountCache.put(accountId, account);
            System.out.println("Account created successfully: " + accountId);

        } catch (SQLException e) {
            throw new DatabaseException("Error creating account", e);
        }
    }

    public Account getAccount(String accountId) throws AccountNotFoundException {
        Account account = accountCache.get(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountId);
        }
        return account;
    }

    public void updateBalance(String accountId, double newBalance) throws AccountNotFoundException, DatabaseException {
        Account account = getAccount(accountId);

        String updateQuery = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountId);
            pstmt.executeUpdate();

            account.setBalance(newBalance);

        } catch (SQLException e) {
            throw new DatabaseException("Error updating balance", e);
        }
    }

    public Map<String, Account> getAllAccounts() {
        return new HashMap<>(accountCache);
    }

    public double getBalance(String accountId) throws AccountNotFoundException {
        return getAccount(accountId).getBalance();
    }

    public void displayAllAccounts() {
        System.out.println("\n========== All Accounts ==========");
        for (Account account : accountCache.values()) {
            System.out.println(account);
        }
        System.out.println("==================================\n");
    }
}
