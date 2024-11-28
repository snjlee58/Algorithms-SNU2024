public class Kmer {
    // Fields to store k-mer value and position
    String value;
    int position;

    // Constructor
    public Kmer(String value, int position) {
        this.value = value;
        this.position = position;
    }

    // Getters (optional, if you need them)
    public String getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }

    // Setters (optional, if you need them)
    public void setValue(String value) {
        this.value = value;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    // Override toString for easy printing (optional)
    @Override
    public String toString() {
        return value + " (" + position + ")";
    }
}
