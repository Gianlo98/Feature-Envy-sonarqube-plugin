class FeatureEnvyCheck2 { // Compliant
    List<Animal> explodingKitties = new ArrayList<>();

    static giveTreats(Animal cat, int treatNumber) {
        int count = 0;
        while (count < treatNumber) {
            cat.pet();
            cat.giveTreat();
            count++;
        }
    }

    public void addKitten(Animal cat, int countdown) {
        explodingKitties.add(cat);
        double seconds = Math.random() * countdown;
        cat.explodeIn(seconds);

    }

    public void removeKitten(Animal cat) {
        cat.pet();
        explodingKitties.remove(cat);
    }

    public boolean containsKitten(Animal cat) {
        return explodingKitties.contains(cat);
    }

}