package com.banking.service;

import com.banking.database.DatabaseConnection;
import com.banking.model.Account;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ReportingHub {
    private AccountManager accountManager;
    private static final String REPORT_DIR = "reports/";

    public ReportingHub(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public void generateAccountSummaryReport() {
        String filename = REPORT_DIR + "account_summary_" + getTimestamp() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("========================================\n");
            writer.write("      ACCOUNT SUMMARY REPORT\n");
            writer.write("========================================\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");

            Map<String, Account> accounts = accountManager.getAllAccounts();
            double totalBalance = 0;

            writer.write(String.format("%-15s %-25s %-20s %15s\n",
                    "Account ID", "Holder Name", "Email", "Balance"));
            writer.write("-------------------------------------------------------------------------\n");

            for (Account account : accounts.values()) {
                writer.write(String.format("%-15s %-25s %-20s %,15.2f\n",
                        account.getAccountId(),
                        account.getAccountHolderName(),
                        account.getEmail(),
                        account.getBalance()));
                totalBalance += account.getBalance();
            }

            writer.write("-------------------------------------------------------------------------\n");
            writer.write(String.format("Total Accounts: %d\n", accounts.size()));
            writer.write(String.format("Total Balance: %,.2f\n", totalBalance));
            writer.write("========================================\n");

            System.out.println("Account summary report generated: " + filename);

        } catch (IOException e) {
            System.err.println("Error generating account summary report: " + e.getMessage());
        }
    }

    public void generateTransactionHistoryReport(String accountId) {
        String filename = REPORT_DIR + "transaction_history_" + accountId + "_" + getTimestamp() + ".txt";

        String query = """
            SELECT transaction_id, transaction_type, amount, related_account_id, 
                   timestamp, status, remarks 
            FROM transactions 
            WHERE account_id = ? 
            ORDER BY timestamp DESC
        """;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
             Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();

            Account account = accountManager.getAccount(accountId);

            writer.write("========================================\n");
            writer.write("   TRANSACTION HISTORY REPORT\n");
            writer.write("========================================\n");
            writer.write("Account ID: " + accountId + "\n");
            writer.write("Account Holder: " + account.getAccountHolderName() + "\n");
            writer.write("Current Balance: " + String.format("%,.2f", account.getBalance()) + "\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");

            writer.write(String.format("%-8s %-20s %-18s %12s %-10s %-20s\n",
                    "TXN ID", "Timestamp", "Type", "Amount", "Status", "Remarks"));
            writer.write("-------------------------------------------------------------------------------------------\n");

            int count = 0;
            while (rs.next()) {
                writer.write(String.format("%-8d %-20s %-18s %,12.2f %-10s %-20s\n",
                        rs.getInt("transaction_id"),
                        rs.getTimestamp("timestamp").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getString("remarks")));
                count++;
            }

            writer.write("-------------------------------------------------------------------------------------------\n");
            writer.write("Total Transactions: " + count + "\n");
            writer.write("========================================\n");

            System.out.println("Transaction history report generated: " + filename);

        } catch (Exception e) {
            System.err.println("Error generating transaction history report: " + e.getMessage());
        }
    }

    public void generateDailyTransactionReport() {
        String filename = REPORT_DIR + "daily_transactions_" + getTimestamp() + ".txt";

        String query = """
            SELECT t.transaction_id, t.account_id, a.account_holder_name, 
                   t.transaction_type, t.amount, t.timestamp, t.status, t.remarks
            FROM transactions t
            JOIN accounts a ON t.account_id = a.account_id
            WHERE DATE(t.timestamp) = CURDATE()
            ORDER BY t.timestamp DESC
        """;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
             Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            writer.write("========================================\n");
            writer.write("     DAILY TRANSACTION REPORT\n");
            writer.write("========================================\n");
            writer.write("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");

            writer.write(String.format("%-8s %-12s %-20s %-18s %12s %-10s\n",
                    "TXN ID", "Account", "Holder", "Type", "Amount", "Status"));
            writer.write("-------------------------------------------------------------------------------------------\n");

            int count = 0;
            double totalAmount = 0;

            while (rs.next()) {
                double amount = rs.getDouble("amount");
                writer.write(String.format("%-8d %-12s %-20s %-18s %,12.2f %-10s\n",
                        rs.getInt("transaction_id"),
                        rs.getString("account_id"),
                        rs.getString("account_holder_name"),
                        rs.getString("transaction_type"),
                        amount,
                        rs.getString("status")));

                if ("SUCCESS".equals(rs.getString("status"))) {
                    totalAmount += amount;
                }
                count++;
            }

            writer.write("-------------------------------------------------------------------------------------------\n");
            writer.write(String.format("Total Transactions: %d\n", count));
            writer.write(String.format("Total Transaction Volume: %,.2f\n", totalAmount));
            writer.write("========================================\n");

            System.out.println("Daily transaction report generated: " + filename);

        } catch (Exception e) {
            System.err.println("Error generating daily transaction report: " + e.getMessage());
        }
    }

    private String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
}
