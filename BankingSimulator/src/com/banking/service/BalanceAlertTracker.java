package com.banking.service;

import com.banking.exception.AccountNotFoundException;
import com.banking.model.Account;

public class BalanceAlertTracker {
    private AccountManager accountManager;
    private EmailService emailService;

    public BalanceAlertTracker(AccountManager accountManager, EmailService emailService) {
        this.accountManager = accountManager;
        this.emailService = emailService;
    }

    public void checkAndAlert(String accountId) {
        try {
            Account account = accountManager.getAccount(accountId);

            if (account.getBalance() < account.getMinBalanceThreshold()) {
                sendLowBalanceAlert(account);
            }

        } catch (AccountNotFoundException e) {
            System.err.println("Account not found for alert check: " + accountId);
        }
    }

    private void sendLowBalanceAlert(Account account) {
        String subject = "Low Balance Alert - Account " + account.getAccountId();
        String body = String.format("""
            Dear %s,
            
            This is an automated alert to inform you that your account balance has fallen below the minimum threshold.
            
            Account Details:
            - Account ID: %s
            - Current Balance: %.2f
            - Minimum Threshold: %.2f
            
            Please ensure adequate funds are maintained in your account.
            
            Thank you,
            Banking System
            """,
                account.getAccountHolderName(),
                account.getAccountId(),
                account.getBalance(),
                account.getMinBalanceThreshold());

        emailService.sendEmail(account.getEmail(), subject, body);

        System.out.println("⚠️  Low balance alert sent to " + account.getEmail());
    }

    public void checkAllAccounts() {
        System.out.println("\n========== Checking All Accounts for Low Balance ==========");

        for (Account account : accountManager.getAllAccounts().values()) {
            if (account.getBalance() < account.getMinBalanceThreshold()) {
                System.out.printf("⚠️  Account %s is below threshold: Balance = %.2f, Threshold = %.2f%n",
                        account.getAccountId(),
                        account.getBalance(),
                        account.getMinBalanceThreshold());
                sendLowBalanceAlert(account);
            }
        }

        System.out.println("===========================================================\n");
    }
}
