// TrainLink has decided to develop a cargo freighting service between regional centres of Australia. A cargo train will deliver cargo from each station on its itinerary. An itinerary contains a list of stations to visit, with the train starting at the first station and ending at the last.

package cargo;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ArrayList<Station> stations = new ArrayList<>(Arrays.asList(
                new Station("Moree", new ArrayList<Cargo>(
                        Arrays.asList(
                                new Cargo("Barley"),
                                new Cargo("Avocados"),
                                new Cargo("Truck Engine"),
                                new Cargo("Drone")
                        )
                )),
                new Station("Gunnedah", new ArrayList<Cargo>(
                        Arrays.asList(
                                new Cargo("DVDs"),
                                new Cargo("Textbooks"),
                                new Cargo("Soybean")
                        )
                )),
                new Station("Murrundai", new ArrayList<Cargo>()),
                new Station("Scone", new ArrayList<Cargo>(
                        Arrays.asList(
                                new Cargo("Oats"),
                                new Cargo("Barley")
                        )
                ))
        ));

        Itinerary itinerary = new Itinerary(stations);

        Train train = new Train("Big Blue", itinerary, new ArrayList<Deliverable>(
                Arrays.asList(
                        new Deliverable(new Cargo("Avocados"), "Gunnedah"),
                        new Deliverable(new Cargo("DVDs"), "Murrundai"),
                        new Deliverable(new Cargo("Drone"), "Scone")

                )
        ));

        
        // Please add your code here to print the required information
        String trainName = train.getTrainName();
        int numberOfStops = train.getJourneyLength();

        while (train.getStationsTravelled() < numberOfStops) {
                Station currentStation = train.getCurrentStation();
                ArrayList<Cargo> currentStationCargo = currentStation.getCargo();
                
                
                System.out.println("The train " + trainName + " is currently at station " + currentStation);

                train.unload();

                System.out.println("There are " + currentStationCargo.size() + " cargoes at the station. They are as follows:");
                for (Cargo c : currentStationCargo) {
                        System.out.println(c);
                }

                if (train.getStationsTravelled() == numberOfStops - 1) return;

                Deliverable nextDeliverable = train.getNextDeliverable();
                System.out.println("The train is going to deliver " + nextDeliverable.getItem() + " to Station " + nextDeliverable.getDestination());
                
                train.travel();
        }
        

}
}