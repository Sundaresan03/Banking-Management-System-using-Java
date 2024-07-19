import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Account {
    String accountNumber;
    String ownerName;
    double balance;
    List<Transaction> transactions;

    Account(String accountNumber, String ownerName) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = 0;
        this.transactions = new ArrayList<>();
    }

    void deposit(double amount) {
        balance += amount;
        transactions.add(new Transaction("Deposit", amount));
    }

    void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction("Withdraw", amount));
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    void transfer(Account toAccount, double amount) {
        if (amount <= balance) {
            balance -= amount;
            toAccount.balance += amount;
            transactions.add(new Transaction("Transfer to " + toAccount.accountNumber, amount));
            toAccount.transactions.add(new Transaction("Transfer from " + this.accountNumber, amount));
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    void viewTransactionHistory() {
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    @Override
    public String toString() {
        return "Account [Account Number: " + accountNumber + ", Owner Name: " + ownerName + ", Balance: " + balance + "]";
    }
}

class Transaction {
    String type;
    double amount;

    Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction [Type: " + type + ", Amount: " + amount + "]";
    }
}

class User {
    String name;
    String address;
    String phoneNumber;
    List<Account> accounts;

    User(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.accounts = new ArrayList<>();
    }

    void addAccount(Account account) {
        accounts.add(account);
    }

    void viewAccounts() {
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    void updatePersonalInfo(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}

class Bank {
    private List<User> users;
    private static final String USER_FILE = "bank_users.txt";

    Bank() {
        users = new ArrayList<>();
        loadUsers();
    }

    void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    User findUser(String name) {
        for (User user : users) {
            if (user.name.equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User user : users) {
                bw.write(user.name + "," + user.address + "," + user.phoneNumber);
                for (Account account : user.accounts) {
                    bw.write("," + account.accountNumber + "," + account.ownerName + "," + account.balance);
                    for (Transaction transaction : account.transactions) {
                        bw.write("," + transaction.type + "," + transaction.amount);
                    }
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                String address = parts[1];
                String phoneNumber = parts[2];
                User user = new User(name, address, phoneNumber);
                int i = 3;
                while (i < parts.length) {
                    String accountNumber = parts[i++];
                    String ownerName = parts[i++];
                    double balance = Double.parseDouble(parts[i++]);
                    Account account = new Account(accountNumber, ownerName);
                    account.balance = balance;
                    while (i < parts.length && !parts[i].matches("\\d+")) {
                        String type = parts[i++];
                        double amount = Double.parseDouble(parts[i++]);
                        account.transactions.add(new Transaction(type, amount));
                    }
                    user.addAccount(account);
                }
                users.add(user);
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
}

public class OnlineBankingSystem {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Online Banking System!");

        while (true) {
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();  // consume newline

            if (option == 3) break;

            switch (option) {
                case 1:
                    createUser(bank, scanner);
                    break;
                case 2:
                    loginUser(bank, scanner);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        System.out.println("Thank you for using the Online Banking System!");
    }

    private static void createUser(Bank bank, Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your address: ");
        String address = scanner.nextLine();
        System.out.print("Enter your phone number: ");
        String phoneNumber = scanner.nextLine();

        User user = new User(name, address, phoneNumber);
        bank.addUser(user);
        System.out.println("Account created successfully!");
    }

    private static void loginUser(Bank bank, Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        User user = bank.findUser(name);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        while (true) {
            System.out.println("1. View Accounts");
            System.out.println("2. Deposit Funds");
            System.out.println("3. Withdraw Funds");
            System.out.println("4. Transfer Funds");
            System.out.println("5. View Transaction History");
            System.out.println("6. Update Personal Information");
            System.out.println("7. Logout");
            System.out.print("Select option: ");
            int option = scanner.nextInt();
            scanner.nextLine();  // consume newline

            if (option == 7) break;

            switch (option) {
                case 1:
                    user.viewAccounts();
                    break;
                case 2:
                    depositFunds(user, scanner);
                    break;
                case 3:
                    withdrawFunds(user, scanner);
                    break;
                case 4:
                    transferFunds(user, scanner);
                    break;
                case 5:
                    viewTransactionHistory(user, scanner);
                    break;
                case 6:
                    updatePersonalInfo(user, scanner);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void depositFunds(User user, Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  // consume newline

        for (Account account : user.accounts) {
            if (account.accountNumber.equals(accountNumber)) {
                account.deposit(amount);
                System.out.println("Deposit successful!");
                return;
            }
        }
        System.out.println("Account not found.");
    }

    private static void withdrawFunds(User user, Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  // consume newline

        for (Account account : user.accounts) {
            if (account.accountNumber.equals(accountNumber)) {
                account.withdraw(amount);
                System.out.println("Withdrawal successful!");
                return;
            }
        }
        System.out.println("Account not found.");
    }

    private static void transferFunds(User user, Scanner scanner) {
        System.out.print("Enter your account number: ");
        String fromAccountNumber = scanner.nextLine();
        System.out.print("Enter recipient account number: ");
        String toAccountNumber = scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  // consume newline

        Account fromAccount = null;
        Account toAccount = null;
        for (Account account : user.accounts) {
            if (account.accountNumber.equals(fromAccountNumber)) {
                fromAccount = account;
            }
            if (account.accountNumber.equals(toAccountNumber)) {
                toAccount = account;
            }
        }

        if (fromAccount != null && toAccount != null) {
            fromAccount.transfer(toAccount, amount);
            System.out.println("Transfer successful!");
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void viewTransactionHistory(User user, Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        for (Account account : user.accounts) {
            if (account.accountNumber.equals(accountNumber)) {
                account.viewTransactionHistory();
                return;
            }
        }
        System.out.println("Account not found.");
    }

    private static void updatePersonalInfo(User user, Scanner scanner) {
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new address: ");
        String address = scanner.nextLine();
        System.out.print("Enter new phone number: ");
        String phoneNumber = scanner.nextLine();

        user.updatePersonalInfo(name, address, phoneNumber);
        System.out.println("Personal information updated successfully!");
    }
}
