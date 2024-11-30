class LCSKmer {
    String value;
    int positionGenome1;
    int positionGenome2;

    public LCSKmer(String value, int positionGenome1, int positionGenome2) {
        this.value = value;
        this.positionGenome1 = positionGenome1;
        this.positionGenome2 = positionGenome2;
    }

    @Override
    public String toString() {
        return "LCSKmer{" +
                "value='" + value + '\'' +
                ", positionGenome1=" + positionGenome1 +
                ", positionGenome2=" + positionGenome2 +
                '}';
    }
}
