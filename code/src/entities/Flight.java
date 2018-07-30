package entities;

public class Flight {

    public long flightNum;

    public int planeInSeatNum;

    public long plannedDepartureTime;
    public long actualDepartureTime;

    public long plannedLandingTime;
    public long actualLandingTime;

    public String departureAirport;
    public String landingAirport;

    public String planedAircraftType; // 对应表里面的飞机型号
    public String actualAircraftType;

    public String plannedAircraftNum; //对应表里面的飞机尾号
    public String actualAircraftNum;

    public long delayTime; // 航班延误时间
}