
package com.banking.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailService {
    private static final String EMAIL_LOG_FILE = "reports/email_log.txt";

    public void sendEmail(String to, String subject, String body) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EMAIL_LOG_FILE, true))) {
            writer.write("\n========================================\n");
            writer.write("EMAIL SENT\n");
            writer.write("========================================\n");
            writer.write("Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("To: " + to + "\n");
            writer.write("Subject: " + subject + "\n");
            writer.write("Body:\n" + body + "\n");
            writer.write("========================================\n\n");

            System.out.println("📧 Email sent to: " + to);

        } catch (IOException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}