
package com.banking.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // TODO: UPDATE THESE WITH YOUR MYSQL CREDENTIALS
    private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String USER = "root";
    private static final String PASSWORD = "Selva@123";  // CHANGE THIS!

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        String createAccountsTable = """
            CREATE TABLE IF NOT EXISTS accounts (
                account_id VARCHAR(20) PRIMARY KEY,
                account_holder_name VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL,
                balance DECIMAL(15, 2) NOT NULL,
                min_balance_threshold DECIMAL(15, 2) DEFAULT 1000.00,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createTransactionsTable = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id INT AUTO_INCREMENT PRIMARY KEY,
                account_id VARCHAR(20) NOT NULL,
                transaction_type VARCHAR(20) NOT NULL,
                amount DECIMAL(15, 2) NOT NULL,
                related_account_id VARCHAR(20),
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                status VARCHAR(20) NOT NULL,
                remarks TEXT,
                FOREIGN KEY (account_id) REFERENCES accounts(account_id)
            )
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createAccountsTable);
            stmt.execute(createTransactionsTable);
            System.out.println("Database tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}