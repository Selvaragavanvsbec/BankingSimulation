# Java-Based Simple Banking Transaction Simulator

A comprehensive banking transaction simulation platform built with core Java, designed to demonstrate real-world banking operations including deposits, withdrawals, transfers, and automated alerts.

## 📋 Project Overview

This project simulates a basic banking system that handles account management, transaction processing, and automated notifications. It leverages core Java features including exception handling, collections, and JDBC for data persistence, providing a practical understanding of banking operations without complex financial libraries.

## ✨ Key Features

- **Account Management**: Create and manage multiple bank accounts with balance tracking
- **Transaction Processing**: Handle deposits, withdrawals, and inter-account transfers
- **Error Handling**: Robust validation for overdrafts and invalid operations
- **Transaction Logging**: JDBC-based persistence for all transactions
- **Automated Alerts**: Email notifications for low balance thresholds
- **Report Generation**: Export transaction history and account summaries to text files
- **Real-time Monitoring**: Balance threshold tracking with configurable alerts

## 🏗️ System Architecture

### Core Modules

#### 1. Account Management Engine
- Account creation and deletion
- Balance inquiry and updates
- In-memory storage using Java Collections (HashMap, ArrayList)
- Account validation and uniqueness checks

#### 2. Transaction Processing System
- Deposit operations with validation
- Withdrawal with overdraft protection
- Inter-account transfers
- Transaction rollback on failures
- Exception handling for invalid operations

#### 3. Reporting and Text File Integration Hub
- Transaction history reports
- Account summary generation
- CSV/Text file export functionality
- Email API integration for notifications
- Scheduled report generation

#### 4. Balance Alert Tracker
- Configurable balance thresholds
- Real-time account monitoring
- Automated email alerts
- Alert history logging

## 🛠️ Technology Stack

- **Language**: Java (JDK 8 or higher)
- **Database**: JDBC-compatible database (MySQL/PostgreSQL/H2)
- **Storage**: Collections Framework for in-memory data
- **File I/O**: Text file handling for reports
- **Email**: JavaMail API for notifications
- **Build Tool**: Maven/Gradle (optional)

## 📦 Prerequisites

- JDK 8 or higher installed
- JDBC-compatible database (MySQL/PostgreSQL recommended)
- Email SMTP credentials (for alert functionality)
- IDE (Eclipse/IntelliJ IDEA/VS Code recommended)

## 🚀 Getting Started

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/banking-simulator.git
cd banking-simulator
```

2. **Configure Database**
```sql
CREATE DATABASE banking_simulator;
CREATE TABLE accounts (
    account_id VARCHAR(20) PRIMARY KEY,
    holder_name VARCHAR(100),
    balance DECIMAL(15,2),
    created_at TIMESTAMP
);
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(20),
    transaction_type VARCHAR(20),
    amount DECIMAL(15,2),
    timestamp TIMESTAMP,
    description VARCHAR(255)
);
```

3. **Update Configuration**

Edit `config.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/banking_simulator
db.username=your_username
db.password=your_password
email.smtp.host=smtp.gmail.com
email.smtp.port=587
email.username=your_email@gmail.com
email.password=your_app_password
alert.threshold=1000.00
```

4. **Compile and Run**
```bash
javac -d bin src/**/*.java
java -cp bin com.banking.Main
```

## 📖 Usage Examples

### Creating an Account
```java
AccountManager accountManager = new AccountManager();
Account account = accountManager.createAccount("ACC001", "John Doe", 5000.00);
```

### Processing Transactions
```java
TransactionProcessor processor = new TransactionProcessor();
processor.deposit("ACC001", 1000.00);
processor.withdraw("ACC001", 500.00);
processor.transfer("ACC001", "ACC002", 250.00);
```

### Generating Reports
```java
ReportGenerator reportGen = new ReportGenerator();
reportGen.generateTransactionReport("ACC001", "reports/transactions.txt");
reportGen.generateAccountSummary("reports/summary.txt");
```

### Setting Up Alerts
```java
BalanceAlertTracker alertTracker = new BalanceAlertTracker();
alertTracker.setThreshold("ACC001", 1000.00);
alertTracker.startMonitoring();
```

## 📁 Project Structure

```
banking-simulator/
├── src/
│   ├── com/banking/
│   │   ├── account/
│   │   │   ├── Account.java
│   │   │   ├── AccountManager.java
│   │   │   └── AccountValidator.java
│   │   ├── transaction/
│   │   │   ├── Transaction.java
│   │   │   ├── TransactionProcessor.java
│   │   │   └── TransactionType.java
│   │   ├── reporting/
│   │   │   ├── ReportGenerator.java
│   │   │   └── FileExporter.java
│   │   ├── alert/
│   │   │   ├── BalanceAlertTracker.java
│   │   │   └── EmailNotifier.java
│   │   ├── database/
│   │   │   └── DatabaseManager.java
│   │   ├── exception/
│   │   │   ├── InsufficientFundsException.java
│   │   │   ├── InvalidAccountException.java
│   │   │   └── TransactionFailedException.java
│   │   └── Main.java
├── reports/
├── logs/
├── config.properties
└── README.md
```

## 🧪 Testing

Run comprehensive tests covering:
- Account creation and validation
- Transaction processing scenarios
- Exception handling for edge cases
- Alert triggering conditions
- Report generation accuracy

```bash
java -cp bin:junit.jar org.junit.runner.JUnitCore com.banking.tests.TestSuite
```

## 🗓️ Development Milestones

- **✅ Milestone 1 (Weeks 1-2)**: Environment setup and initial training
- **✅ Milestone 2 (Weeks 3-4)**: Account Management Engine
- **✅ Milestone 3 (Weeks 5-6)**: Transaction Processing & Reporting Hub
- **✅ Milestone 4 (Weeks 7-8)**: Balance Alert Tracker & Deployment

## 🔍 Error Handling

The system handles various exception scenarios:
- `InsufficientFundsException`: Overdraft attempts
- `InvalidAccountException`: Non-existent accounts
- `TransactionFailedException`: Database or processing errors
- `InvalidAmountException`: Negative or zero amounts

## 📊 Sample Output

**Transaction Report:**
```
=====================================
    TRANSACTION HISTORY REPORT
=====================================
Account: ACC001 | Holder: John Doe
Current Balance: $4,750.00

Date/Time            | Type      | Amount    | Description
------------------------------------------------------------------
2025-11-14 10:30:00 | DEPOSIT   | $1,000.00 | Initial deposit
2025-11-14 11:15:00 | WITHDRAW  |   $500.00 | ATM withdrawal
2025-11-14 14:45:00 | TRANSFER  |   $250.00 | Transfer to ACC002
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/NewFeature`)
3. Commit changes (`git commit -m 'Add NewFeature'`)
4. Push to branch (`git push origin feature/NewFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- Your Name - selvaragavan s
