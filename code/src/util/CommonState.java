package util;

import entities.Aircraft;
import entities.Flight;
import entities.FlightLine;
import entities.Visitor;

import java.util.ArrayList;
import java.util.List;

public class CommonState {
    /** 所有飞机的组合 */
    public static List<Aircraft> GlobalAircraftsList = new ArrayList<>();
    /** 所有航班的组合 */
    public static List<Flight> GlobalFlightsList = new ArrayList<>();
    /** 所有航线的组合 */
    public static List<FlightLine> GlobalFlightLinesList = new ArrayList<>();
    /** 延误航线表 */
    public static List<FlightLine> DelayFlighLinesList = new ArrayList<>();
    /** 旅客表 */
    public static  List<Visitor> GlobalVisitorList = new ArrayList<>();
}
