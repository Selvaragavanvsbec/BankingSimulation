package com.banking;

import com.banking.database.DatabaseConnection;
import com.banking.exception.*;
import com.banking.service.*;

import java.util.Scanner;

public class BankingSimulator {
    private static AccountManager accountManager;
    private static TransactionProcessor transactionProcessor;
    private static ReportingHub reportingHub;
    private static BalanceAlertTracker alertTracker;
    private static EmailService emailService;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  BANKING TRANSACTION SIMULATOR");
        System.out.println("========================================\n");

        DatabaseConnection.initializeDatabase();

        accountManager = new AccountManager();
        emailService = new EmailService();
        alertTracker = new BalanceAlertTracker(accountManager, emailService);
        transactionProcessor = new TransactionProcessor(accountManager, alertTracker);
        reportingHub = new ReportingHub(accountManager);

        runMenu();
    }

    private static void runMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            displayMenu();
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> createAccount(scanner);
                    case 2 -> deposit(scanner);
                    case 3 -> withdraw(scanner);
                    case 4 -> transfer(scanner);
                    case 5 -> checkBalance(scanner);
                    case 6 -> viewAllAccounts();
                    case 7 -> generateAccountSummary();
                    case 8 -> generateTransactionHistory(scanner);
                    case 9 -> generateDailyReport();
                    case 10 -> checkAllBalanceAlerts();
                    case 11 -> runDemoScenario();
                    case 0 -> {
                        running = false;
                        System.out.println("\nThank you for using Banking Simulator!");
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine();
            }

            System.out.println();
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1.  Create Account");
        System.out.println("2.  Deposit");
        System.out.println("3.  Withdraw");
        System.out.println("4.  Transfer");
        System.out.println("5.  Check Balance");
        System.out.println("6.  View All Accounts");
        System.out.println("7.  Generate Account Summary Report");
        System.out.println("8.  Generate Transaction History Report");
        System.out.println("9.  Generate Daily Transaction Report");
        System.out.println("10. Check All Balance Alerts");
        System.out.println("11. Run Demo Scenario");
        System.out.println("0.  Exit");
        System.out.println("================================");
    }

    private static void createAccount(Scanner scanner) {
        try {
            System.out.print("Enter Account ID: ");
            String accountId = scanner.nextLine();

            System.out.print("Enter Account Holder Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Email: ");
            String email = scanner.nextLine();

            System.out.print("Enter Initial Balance: ");
            double balance = scanner.nextDouble();

            System.out.print("Enter Minimum Balance Threshold: ");
            double threshold = scanner.nextDouble();
            scanner.nextLine();

            accountManager.createAccount(accountId, name, email, balance, threshold);
            System.out.println("✓ Account created successfully!");

        } catch (DatabaseException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private static void deposit(Scanner scanner) {
        try {
            System.out.print("Enter Account ID: ");
            String accountId = scanner.nextLine();

            System.out.print("Enter Deposit Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            transactionProcessor.deposit(accountId, amount);
            System.out.println("✓ Deposit successful!");

        } catch (AccountNotFoundException | InvalidAmountException | DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void withdraw(Scanner scanner) {
        try {
            System.out.print("Enter Account ID: ");
            String accountId = scanner.nextLine();

            System.out.print("Enter Withdrawal Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            transactionProcessor.withdraw(accountId, amount);
            System.out.println("✓ Withdrawal successful!");

        } catch (AccountNotFoundException | InvalidAmountException |
                 InsufficientBalanceException | DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void transfer(Scanner scanner) {
        try {
            System.out.print("Enter From Account ID: ");
            String fromAccountId = scanner.nextLine();

            System.out.print("Enter To Account ID: ");
            String toAccountId = scanner.nextLine();

            System.out.print("Enter Transfer Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            transactionProcessor.transfer(fromAccountId, toAccountId, amount);
            System.out.println("✓ Transfer successful!");

        } catch (AccountNotFoundException | InvalidAmountException |
                 InsufficientBalanceException | DatabaseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void checkBalance(Scanner scanner) {
        try {
            System.out.print("Enter Account ID: ");
            String accountId = scanner.nextLine();

            double balance = accountManager.getBalance(accountId);
            System.out.printf("Current Balance: %.2f%n", balance);

        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllAccounts() {
        accountManager.displayAllAccounts();
    }

    private static void generateAccountSummary() {
        reportingHub.generateAccountSummaryReport();
    }

    private static void generateTransactionHistory(Scanner scanner) {
        System.out.print("Enter Account ID: ");
        String accountId = scanner.nextLine();
        reportingHub.generateTransactionHistoryReport(accountId);
    }

    private static void generateDailyReport() {
        reportingHub.generateDailyTransactionReport();
    }

    private static void checkAllBalanceAlerts() {
        alertTracker.checkAllAccounts();
    }

    private static void runDemoScenario() {
        System.out.println("\n========== Running Demo Scenario ==========");

        try {
            System.out.println("\n1. Creating sample accounts...");
            accountManager.createAccount("ACC001", "John Doe", "john@email.com", 5000.00, 1000.00);
            accountManager.createAccount("ACC002", "Jane Smith", "jane@email.com", 3000.00, 500.00);
            accountManager.createAccount("ACC003", "Bob Johnson", "bob@email.com", 10000.00, 2000.00);

            Thread.sleep(1000);

            System.out.println("\n2. Performing deposits...");
            transactionProcessor.deposit("ACC001", 2000.00);
            transactionProcessor.deposit("ACC002", 1500.00);

            Thread.sleep(1000);

            System.out.println("\n3. Performing withdrawals...");
            transactionProcessor.withdraw("ACC001", 500.00);
            transactionProcessor.withdraw("ACC003", 8500.00);

            Thread.sleep(1000);

            System.out.println("\n4. Performing transfers...");
            transactionProcessor.transfer("ACC002", "ACC001", 1000.00);
            transactionProcessor.transfer("ACC001", "ACC003", 2000.00);

            Thread.sleep(1000);

            System.out.println("\n5. Testing error handling...");
            try {
                transactionProcessor.withdraw("ACC002", 10000.00);
            } catch (InsufficientBalanceException e) {
                System.out.println("✓ Caught expected error: " + e.getMessage());
            }

            try {
                transactionProcessor.deposit("ACC999", 100.00);
            } catch (AccountNotFoundException e) {
                System.out.println("✓ Caught expected error: " + e.getMessage());
            }

            Thread.sleep(1000);

            System.out.println("\n6. Generating reports...");
            reportingHub.generateAccountSummaryReport();
            reportingHub.generateTransactionHistoryReport("ACC001");
            reportingHub.generateDailyTransactionReport();

            Thread.sleep(1000);

            System.out.println("\n7. Checking balance alerts...");
            alertTracker.checkAllAccounts();

            System.out.println("\n========== Demo Scenario Completed ==========");

        } catch (Exception e) {
            System.out.println("Error in demo scenario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
