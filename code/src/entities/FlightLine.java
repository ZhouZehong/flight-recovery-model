package entities;

import java.util.ArrayList;
import java.util.List;

public class FlightLine implements Comparable<FlightLine>{
    // 代表了一个航班串
    public List<Flight> flightLine;

    public FlightLine(){
        flightLine = new ArrayList<>();
    }

    @Override
    public int compareTo(FlightLine o) {
        if (this.flightLine.get(0).plannedDepartureTime > o.flightLine.get(0).plannedDepartureTime){
            return 1;
        }
        if (this.flightLine.get(0).plannedDepartureTime == o.flightLine.get(0).plannedDepartureTime){
            return 0;
        }
        return -1;
    }
}