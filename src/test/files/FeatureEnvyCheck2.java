class FeatureEnvyCheck2 { // Noncompliant
    static List<Animal> explodingKitties = new ArrayList<>();
    int aPrimitiva = 9;

    /**
     * I am a good comment
     * @param cat
     * @param treatNumber
     */
    static giveTreats(Animal cat, int treatNumber) {
        int count = 0;
        //Blehhh

        while (count < treatNumber) {
            cat.pet();

            Animal a = new Animal();

            //S.ad comment :D
            cat.giveTreat();
            count++;
        }
    }

    static void addKitten(Animal cat, int countdown) {
        double seconds;
        explodingKitties.add(cat);
        seconds = Math.random() * Math.random() * 100 * countdown;

        cat.explodeIn(seconds);

    }

    public void removeKitten(Animal cat) {
        cat.stopCountdown();
        cat.pet();
        explodingKitties.remove(cat);
    }

}