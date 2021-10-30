class FeatureEnvyCheck2 {
    static void giveTreats(Animal cat, int treatNumber) {
        int count = 0;
        while (count < treatNumber) {
            cat.pet();
            cat.giveTreat();
            count++;
        }
    }
    static void deposit(Account account, int amount) {
        account.setBalance(account.balance() + amount);
    }
}