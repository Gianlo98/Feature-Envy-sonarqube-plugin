class FeatureEnvyCheck2 { // Noncompliant
    static List<Animal> explodingKitties = new ArrayList<>();

    static giveTreats(Animal cat, int treatNumber) {
        int count = 0;
        while (count < treatNumber) {
            cat.pet();
            cat.giveTreat();
            count++;
        }
    }

    static void addKitten(Animal cat, int countdown) {
        explodingKitties.add(cat);
        double seconds = Math.random() * countdown;
        cat.explodeIn(seconds);

    }

    public void removeKitten(Animal cat) {
        cat.stopCountdown();
        cat.pet();
        explodingKitties.remove(cat);
    }

}