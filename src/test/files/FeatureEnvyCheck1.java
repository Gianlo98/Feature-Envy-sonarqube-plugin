class FeatureEnvyCheck1 {
    static void withdraw(Account account, int amount) {
        account.setBalance(account.balance() - amount);
    }
    static void deposit(Account account, int amount) {
        account.setBalance(account.balance() + amount);
    }
}