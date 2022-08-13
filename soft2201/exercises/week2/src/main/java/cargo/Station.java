package cargo;

import java.util.ArrayList;

public class Station {
    
    private String name;
    private ArrayList<Cargo> cargo;

    public Station(String name, ArrayList<Cargo> cargo) {
        this.name = name;
        this.cargo = cargo;
    }

    public ArrayList<Cargo> getCargo() {
        return cargo;
    }

    public void addCargo(Cargo cargo) {
        this.cargo.add(cargo);
    }

    @Override
    public String toString() {
        return name;
    }

}
