class FeatureEnvyCheck1 { // Noncompliant
    static void withdraw(Account account, int amount) {
        int balance;
        balance = account.balance();
        account.setBalance(balance - amount);
    }
    static void deposit(Account account, int amount) {
        int balance;
        balance = account.balance();
        account.setBalance(balance + amount);
    }
}
