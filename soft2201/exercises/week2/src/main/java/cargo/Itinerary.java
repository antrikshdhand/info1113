package cargo;

import java.util.ArrayList;

public class Itinerary {
    
    private ArrayList<Station> stations;

    public Itinerary(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public Station getStation(int stationNumber) {
        return this.stations.get(stationNumber);
    }

    public int getItinerarySize() {
        return this.stations.size();
    }

}
