package entities;

import java.util.ArrayList;
import java.util.List;

public class Visitor {
    public long visitorID;
    public long flightID;
    public long visitorNum;
    public List<Flight> flightWay = new ArrayList<>();
    public Flight currentFlight;
    public Flight lastFlight;
    public boolean isBandoned;
}
