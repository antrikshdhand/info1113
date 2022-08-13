package cargo;

public class Cargo {
    
    private String item;

    public Cargo(String item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return this.item;
    }

}
