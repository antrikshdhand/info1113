package cargo;

import java.util.ArrayList;

public class Train {
    
    private String name;
    private Itinerary itinerary;
    private ArrayList<Deliverable> deliverables;
    private int stationsTravelled;
    private Station currentStation;

    public Train(String name, Itinerary itinerary, ArrayList<Deliverable> deliverables) {
        this.name = name;
        this.itinerary = itinerary;
        this.deliverables = deliverables;
        this.stationsTravelled = 0;
        this.currentStation = itinerary.getStation(stationsTravelled);
    }

    public String getTrainName() {
        return this.name;
    }

    public Station getCurrentStation() {
        return this.currentStation;
    }

    public Deliverable getNextDeliverable() {
        return deliverables.get(stationsTravelled);
    }

    public int getJourneyLength() {
        return this.itinerary.getItinerarySize();
    }

    public int getStationsTravelled() {
        return this.stationsTravelled;
    }

    public void travel() {
        stationsTravelled++;
        this.currentStation = itinerary.getStation(stationsTravelled);
    }
    
    public void unload() {
        if (this.stationsTravelled == 0) return;

        Deliverable currentDeliverable = deliverables.get(this.stationsTravelled - 1);
        Cargo deliverableCargo = currentDeliverable.getItem();
        this.currentStation.addCargo(deliverableCargo);

    }


}
