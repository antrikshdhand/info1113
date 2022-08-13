package cargo;

public class Deliverable {
    public Cargo item;
    public String destination;

    public Deliverable(Cargo item, String destination) {
        this.item = item;
        this.destination = destination;
    }

    public Cargo getItem() {
        return this.item;
    }
    
    public String getDestination() {
        return this.destination;
    }
}
