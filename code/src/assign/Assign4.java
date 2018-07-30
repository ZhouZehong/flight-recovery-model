package assign;

import entities.Aircraft;
import entities.Flight;
import entities.FlightLine;
import util.CommonState;

import java.util.*;

public class Assign4 {
    private static Map<Integer, Integer> departureMap;
    private static Map<Integer, Integer> landingMap;

    private final static long OVS_DELAY_TIME = 1461358800;

    /**
     * 根据机尾号来找具体的飞机对象
     * @param num 机尾号
     * @return
     */
    public static Aircraft getAirCraftByNum(String num)
    {
        for(int i = 0; i< CommonState.GlobalAircraftsList.size(); i++)
        {
            if(CommonState.GlobalAircraftsList.get(i).aircraftID.equals(num))
                return CommonState.GlobalAircraftsList.get(i);
        }
        return null;
    }

    /**
     * 指派方法
     * @param airport 需要进行指派的机场
     * @param assignTime 开始指派（延误开始）的时间
     */
    public static boolean assign3(String airport, long assignTime)
    {

        if (airport.equals("OVS"))
        {
            // 用于记录各个时刻OVS机场的跑道占用情况的Map,从9点开始，每五分钟为一个时间片段 <第几个时间片段，跑道占用情况>
            departureMap = new HashMap<>();
            landingMap = new HashMap<>();
        }



        // Step1、2:找飞机，找航班串
        List<Aircraft> aircraftOnTheGroundList = new ArrayList<>(); // 机场可用飞机的数组
        List<Aircraft> aircraftOnTheAir = new ArrayList<>(); // 还未抵达机场的飞机的数组
        List<Aircraft> allAircraftList = new ArrayList<>(); // 所有飞机的数组
        List<FlightLine> allFlightLines = new ArrayList<>(); // 所有航班串的数组

        // 根据航班线来确定可用飞机的当前位置以及当前经停该机场的航班串
        for (FlightLine flightLine : CommonState.GlobalFlightLinesList) {
            for (Flight flight : flightLine.flightLine) {
                // 先匹配机型
//                if (flight.actualAircraftType.equals("9")){
                // 一种特殊情况，飞机没有航班任务了，一直停留在这个机场
                Flight finFlight = flightLine.flightLine.get(flightLine.flightLine.size() - 1);
                if ((finFlight.actualLandingTime < (assignTime - 45*60)) && (finFlight.landingAirport.equals(airport))){
                    if (!finFlight.actualAircraftNum.equals("cancel")){
                        Aircraft aircraft = getAirCraftByNum(finFlight.actualAircraftNum);
                        aircraft.plannedTime = assignTime;
                        if (!aircraftOnTheGroundList.contains(aircraft)) {
                            aircraftOnTheGroundList.add(aircraft);
                        }
                    }
                    break;
                }
                // 起飞时间晚于延误时间且起飞机场为延误机场，则地上
                if ((flight.actualDepartureTime > assignTime) && (flight.departureAirport.equals(airport))){
                    if (!flight.actualAircraftNum.equals("cancel")){
                        Aircraft aircraft = getAirCraftByNum(flight.actualAircraftNum);
                        aircraft.plannedTime = flight.actualDepartureTime;
                        if (!aircraftOnTheGroundList.contains(aircraft)) {
                            aircraftOnTheGroundList.add(aircraft);
                        }
                    }
                    FlightLine nFlightLine = new FlightLine();
                    nFlightLine.flightLine.add(flight);
                    for (Flight tmpFlight: flightLine.flightLine) {
                        if (tmpFlight.actualDepartureTime > flight.actualDepartureTime){
                            nFlightLine.flightLine.add(tmpFlight);
                        }
                    }
                    // 先把新提取出来的航班串删掉，后边会在添加上新的航班串
//                        flightLine.flightLine.removeAll(nFlightLine.flightLine);
                    boolean judgeInsert = true;
                    for (FlightLine flightLine1: allFlightLines){
                        Flight tmpFlight = flightLine1.flightLine.get(0);
                        if (tmpFlight.flightNum == nFlightLine.flightLine.get(0).flightNum){
                            judgeInsert = false;
                            break;
                        }
                    }
                    if (judgeInsert) {
                        allFlightLines.add(nFlightLine);
                    }
                    break;
                }
                // 天上
                if ((flight.actualLandingTime > assignTime) && flight.landingAirport.equals(airport)){
                    if (!flight.actualAircraftNum.equals("cancel")){
                        Aircraft aircraft = getAirCraftByNum(flight.actualAircraftNum);
                        aircraft.plannedTime = flight.actualLandingTime;
                        aircraft.flight = flight;
                        if (!aircraftOnTheAir.contains(aircraft)) {
                            aircraftOnTheAir.add(aircraft);
                        }
                    }
                    FlightLine nFlightLine = new FlightLine();
                    for (Flight tmpFlight: flightLine.flightLine){
                        if (tmpFlight.actualLandingTime > flight.actualLandingTime)
                            nFlightLine.flightLine.add(tmpFlight);
                    }
                    // 先把新提取出来的航班串删掉，后边会在添加上新的航班串
//                        flightLine.flightLine.removeAll(nFlightLine.flightLine);
                    if (nFlightLine.flightLine.size() > 0){
                        boolean judgeInsert = true;
                        for (FlightLine flightLine1: allFlightLines){
                            Flight tmpFlight = flightLine1.flightLine.get(0);
                            if (tmpFlight.flightNum == nFlightLine.flightLine.get(0).flightNum){
                                judgeInsert = false;
                                break;
                            }
                        }
                        if (judgeInsert) {
                            allFlightLines.add(nFlightLine);
                        }
                    }
                    break;
                }
//                }
//                else {
//                    break;
//                }
            }
        }
        // 根据起飞时间，对机场可用飞机进行排序
        Collections.sort(aircraftOnTheGroundList);
        // 根据抵达时间，对未抵达机场进行排序
        Collections.sort(aircraftOnTheAir);


        // 遍历可用飞机集合，为可用飞机配置就绪时间
        int count = 0; // 计数器，需为0，因为第五架飞机和第一架飞机的起飞时间是一样的
        for (Aircraft aircraft : aircraftOnTheGroundList) {
            // OVS机场很特殊，需要单独考虑
            if (airport.equals("OVS")) {
                if (assignTime < OVS_DELAY_TIME) {
                    aircraft.readyTime = (long) (OVS_DELAY_TIME + Math.floor(count/5)*300);
                    landingMap.put(count/5, (count+1)%5);
                    count++;
                }
                else {
                    int tempIndex = (int) ((aircraft.plannedTime - OVS_DELAY_TIME)/300);
                    // 直至找到一个可以起飞的时间段
                    while (true) {
                        if (!landingMap.containsKey(tempIndex)) {
                            aircraft.readyTime = OVS_DELAY_TIME + tempIndex*300;
                            landingMap.put(tempIndex, 1);
                            break;
                        }
                        if (landingMap.get(tempIndex) < 5) {
                            aircraft.readyTime = OVS_DELAY_TIME + tempIndex*300;
                            landingMap.put(tempIndex, landingMap.get(tempIndex)+1);
                            break;
                        }
                        tempIndex++;
                    }
                }
            }
            else {
                if (aircraft.readyTime == -1) {
                    aircraft.readyTime = aircraft.plannedTime; // 因为不知道是否已经满足45分钟的条件，就将原计划的起飞安排作为就绪时间
                }
            }
        }

        // 遍历未抵达飞机集合，为未抵达飞机配置就绪时间
        count = 0;
        for (Aircraft aircraft : aircraftOnTheAir) {
            // OVS机场特殊考虑
            if (airport.equals("OVS")) {
                // 那些被滞留在空中的可怜的机儿
                if (aircraft.plannedTime < OVS_DELAY_TIME) {
                    aircraft.readyTime = (long) (OVS_DELAY_TIME + Math.floor(count/5)*300 + 45*60); // 别忘了45分钟的间隔
                    // 由于OVS机场出现的问题导致延误降落的航班
                    long actualLandingTime = (long) (OVS_DELAY_TIME + Math.floor(count/5)*300);
                    aircraft.flight.actualLandingTime = actualLandingTime;
                    aircraft.flight.delayTime = actualLandingTime - aircraft.flight.plannedLandingTime;
                    //aircraft.flight = null;
                    departureMap.put(count/5, (count+1)%5);
                    count++;
                }
                // 非滞留的飞机，但也有可能因为跑道限制而延误降落
                else {
                    int tempIndex = (int) ((aircraft.plannedTime - OVS_DELAY_TIME)/300);
                    // 如果当前时间的跑道没有被占满，则允许现在降落，如果已被占满，则往后推，直至找到未被占满的时段才允许降落
                    while (true) {
                        if (!departureMap.containsKey(tempIndex)) {
                            aircraft.readyTime = OVS_DELAY_TIME +tempIndex*300 + 45*60;
                            // 由于OVS机场出现的问题导致延误降落的航班
                            long actualLandingTime = (long) (OVS_DELAY_TIME + tempIndex*300);
                            aircraft.flight.actualLandingTime = actualLandingTime;
                            aircraft.flight.delayTime = actualLandingTime - aircraft.flight.plannedLandingTime;
                            //aircraft.flight = null;
                            departureMap.put(tempIndex, 1);
                            break;
                        }
                        if (departureMap.get(tempIndex) < 5) {
                            aircraft.readyTime = OVS_DELAY_TIME +tempIndex*300 + 45*60;
                            // 由于OVS机场出现的问题导致延误降落的航班
                            long actualLandingTime = (long) (OVS_DELAY_TIME + tempIndex*300);
                            aircraft.flight.actualLandingTime = actualLandingTime;
                            aircraft.flight.delayTime = actualLandingTime - aircraft.flight.plannedLandingTime;
//                            aircraft.flight = null;
                            departureMap.put(tempIndex, departureMap.get(tempIndex)+1);
                            break;
                        }
                        tempIndex++;
                    }
                }
            }
            else {
                if (aircraft.readyTime == -1) {
                    aircraft.readyTime = aircraft.plannedTime + 45*60;
                }
            }
        }

        allAircraftList.addAll(aircraftOnTheGroundList);
        allAircraftList.addAll(aircraftOnTheAir);

        //Step3:构造最优化指派矩阵
        long[][] delayMatrix = new long[allAircraftList.size()][allFlightLines.size()];
        for(int i = 0; i<allAircraftList.size(); i++)
        {
            Aircraft aircraft = allAircraftList.get(i); // 飞机
//            System.out.println(i + ": " + aircraft.aircraftID);
            for(int j = 0; j<allFlightLines.size(); j++)
            {
                FlightLine flightLine = allFlightLines.get(j); // 航班线
                Flight firstFlight = flightLine.flightLine.get(0); // 航班线上的首航班
                Aircraft planedAircraft = getAirCraftByNum(firstFlight.plannedAircraftNum);
//                System.out.println(j + ": " + firstFlight.flightNum);
                // 约束条件，无限大代表了无法被指派
                long delayTime = 0;
                if ((aircraft.earliestUsableTime > firstFlight.plannedDepartureTime)
                        || (aircraft.latestUsableTime < firstFlight.plannedDepartureTime)){
                    delayTime = 0x7fffffff;
                }
                else {
                    //需要判断该机型是否能装满所有旅客
                    long couldGetInFlightVisitorNum = 0;
                    for(int getVistoCount=0; getVistoCount<CommonState.GlobalVisitorList.size(); getVistoCount++)
                    {
                        if(CommonState.GlobalVisitorList.get(getVistoCount).flightID==firstFlight.flightNum)
                        {
                            if(couldGetInFlightVisitorNum<aircraft.seatNum)
                            {
                                couldGetInFlightVisitorNum+=CommonState.GlobalVisitorList.get(getVistoCount).visitorNum;
                            }
                            else
                            {
                                delayTime = delayTime+ CommonState.GlobalVisitorList.get(getVistoCount).visitorNum*24*60*60;
                            }
                        }
                    }
                    aircraft.inSeatNum = (int) couldGetInFlightVisitorNum;
                    long lastFlightLastLandingTime = 0;
                    for(int visitorCount=0; visitorCount<CommonState.GlobalVisitorList.size(); visitorCount++)
                    {

                        if(CommonState.GlobalVisitorList.get(visitorCount).lastFlight!=null
                                &&CommonState.GlobalVisitorList.get(visitorCount).lastFlight.actualLandingTime>lastFlightLastLandingTime)
                        {
                            lastFlightLastLandingTime = CommonState.GlobalVisitorList.get(visitorCount).lastFlight.actualLandingTime;
                        }
                    }
                    if(aircraft.readyTime<lastFlightLastLandingTime)
                    {
                        delayTime = delayTime + lastFlightLastLandingTime+45*60 - firstFlight.plannedDepartureTime;
                    }
                    else
                    {
                        delayTime = delayTime + aircraft.readyTime - firstFlight.plannedDepartureTime;
                    }
                    if (delayTime < 0) {
                        delayTime = 0;
                    }

                    if (!aircraft.aircraftType.equals(firstFlight.planedAircraftType)){
                        //先找到当前航班的上一躺航班

                        delayTime += 30*60;
                        // 旅客总体延误
                        delayTime = delayTime * aircraft.seatNum;
                        if (aircraft.seatNum < planedAircraft.seatNum){
                            delayTime += (planedAircraft.seatNum - aircraft.seatNum)*2*60*60;
                        }
                    }
//                    if (delayTime > 5*60*60) {
//                        delayTime = 0x7fffffff;
//                    }
                }
                // 航班串的总延时
                delayMatrix[i][j] = -1 * delayTime * flightLine.flightLine.size();
//                System.out.print(delayTime + ", ");
            }
//            System.out.println();
        }

        // 匈牙利算法进行最优化指派
        KM myKm = new KM(Math.max(allAircraftList.size(), allFlightLines.size()));
        // 如果已经没有了航班，则不再进行指派
        if (allFlightLines.size() > 0){
            int[][] matchResult = myKm.getMaxBipartie(delayMatrix);
            // 根据新的指派矩阵更新信息，包括航班信息（时刻表）、飞机信息（就绪时间等）、航班串信息
            for (int i = 0; i < matchResult.length; i++) {

                int aircraftfIndex = matchResult[i][0];
                int flightLineIndex = matchResult[i][1];
                Aircraft aircraft = allAircraftList.get(aircraftfIndex);
                FlightLine flightLine = allFlightLines.get(flightLineIndex);
                Flight firstFlight = flightLine.flightLine.get(0);

                System.out.println(aircraft.aircraftID + ", " + firstFlight.flightNum);



                // 更新航班信息（时刻表）（包括航班串后的）和飞机信息
                long delayTime;
                // 航班取消的情况，但发生的可能性很低
                if ((aircraft.earliestUsableTime > firstFlight.plannedDepartureTime)
                        || (aircraft.latestUsableTime < firstFlight.plannedDepartureTime)){
//                    aircraft.flight = null;
                    for (Flight flight: flightLine.flightLine) {
//                    flight.actualDepartureTime = -1;
//                    flight.actualLandingTime = -1;
                        flight.actualAircraftType = "cancel";
                        flight.actualAircraftNum = "cancel";
                        flight.delayTime = -1;
                    }
                }
                else {
                    delayTime = aircraft.readyTime - firstFlight.plannedDepartureTime;
                    // 该条航班串准点
                    if (delayTime <= 0) {
                        delayTime = 0;
//                        if (!aircraft.aircraftType.equals(firstFlight.planedAircraftType)){
//                            delayTime = 30*60;
//                        }
                        aircraft.readyTime = firstFlight.plannedLandingTime + 45*60;
//                        aircraft.flight = firstFlight;
                        for (Flight flight: flightLine.flightLine) {
                            flight.actualDepartureTime = flight.plannedDepartureTime;
                            flight.actualLandingTime = flight.plannedLandingTime;
                            flight.actualAircraftType = aircraft.aircraftType;
                            flight.actualAircraftNum = aircraft.aircraftID;
                            flight.delayTime = delayTime;
                        }
                        // 将不再延误的航班串从延误表里剔除
                        FlightLine deleteLine = new FlightLine();
                        boolean whetherStop = false;
                        for (FlightLine flightLine1: CommonState.DelayFlighLinesList) {
                            for (Flight flight: flightLine1.flightLine) {
                                if (flight.flightNum == firstFlight.flightNum){
                                    deleteLine = flightLine1;
                                    whetherStop = true;
                                    break;
                                }
                            }
                            if (whetherStop){
                                break;
                            }
                        }
                        if (deleteLine.flightLine.size() > 0) {
                            CommonState.DelayFlighLinesList.remove(deleteLine);
                        }
                    }
                    else {
                        // 首延误的航班取消了
                        if (delayTime > 5*60*60) {
//                            aircraft.flight = null;
//                            for (Flight flight: flightLine.flightLine) {
//                            flight.actualDepartureTime = -1;
//                            flight.actualLandingTime = -1;
                            firstFlight.actualAircraftType = "cancel";
                            firstFlight.actualAircraftNum = "cancel";
                            firstFlight.delayTime = -1;
//                            }
                        }
                        else {
                            long nDelayTime = delayTime;
//                            if (aircraft.aircraftType.equals(firstFlight.planedAircraftType)){
//                                nDelayTime = delayTime + 30*60;
//                            }
                            aircraft.readyTime = delayTime + firstFlight.plannedLandingTime + 45*60;
//                            aircraft.flight = firstFlight;
                            for (Flight flight: flightLine.flightLine) {
                                flight.actualDepartureTime = flight.plannedDepartureTime + delayTime;
                                flight.actualLandingTime = flight.plannedLandingTime + delayTime;
                                flight.actualAircraftType = aircraft.aircraftType;
                                flight.actualAircraftNum = aircraft.aircraftID;
                                flight.delayTime = nDelayTime;
                            }
                        }

                        // 判断延误表中有无此航班线，有则剔除延误首航班，无则添加新的延误路径
                        FlightLine deleteLine = new FlightLine();
                        FlightLine replaceLine = new FlightLine();
                        if (assignTime != flightLine.flightLine.get(0).plannedDepartureTime){
                            replaceLine.flightLine = new ArrayList<>(flightLine.flightLine);
                            // 剔除首延误航班
                            replaceLine.flightLine.remove(0);
                        }

                        boolean whetherStop = false;

                        for (FlightLine flightLine1: CommonState.DelayFlighLinesList) {
                            for (Flight flight: flightLine1.flightLine) {
                                if ((flightLine.flightLine.size() > 1)){
                                    if (flight.flightNum == flightLine.flightLine.get(1).flightNum){
                                        deleteLine = flightLine1;
                                        whetherStop = true;
                                        break;
                                    }
                                }
                                if (flight.flightNum == firstFlight.flightNum){
                                    deleteLine = flightLine1;
                                    whetherStop = true;
                                    break;
                                }
                            }
                            if (whetherStop){
                                break;
                            }
                        }

                        if (deleteLine.flightLine.size() > 0) {
                            CommonState.DelayFlighLinesList.remove(deleteLine);
                        }
                        if ((replaceLine.flightLine.size() > 0) && (allFlightLines.size() > 1)){
                            CommonState.DelayFlighLinesList.add(replaceLine);
                        }
                    }
                }

                // 更新全局航班串
                for (FlightLine flightLine1: CommonState.GlobalFlightLinesList) {
                    if (flightLine1.flightLine.size() > 0){
                        Flight tmpFlight = flightLine1.flightLine.get(0);
                        // 匹配到具体飞机的航班串再开始进行修改工作
                        if (tmpFlight.actualAircraftNum.equals(aircraft.aircraftID)){
                            for (Flight flight: flightLine1.flightLine) {
                                // 要注意，这里的航班时间是已经调整过的了
                                Flight finFlight = flightLine1.flightLine.get(flightLine1.flightLine.size() - 1);
                                if ((finFlight.actualLandingTime < (assignTime - 45*60)) && (finFlight.landingAirport.equals(airport))){
                                    if (firstFlight.delayTime != -1){
                                        flightLine1.flightLine.addAll(flightLine.flightLine);
                                    }
                                    break;
                                }
                                if ((flight.actualDepartureTime > assignTime)
                                        && (flight.departureAirport.equals(airport))){
                                    FlightLine nFlightLine = new FlightLine();
                                    nFlightLine.flightLine.add(flight);
                                    for (Flight flight1 : flightLine1.flightLine){
                                        if (flight1.actualDepartureTime > flight.actualDepartureTime){
                                            nFlightLine.flightLine.add(flight1);
                                        }
                                    }
                                    flightLine1.flightLine.removeAll(nFlightLine.flightLine);
                                    // 说明航班没取消，如果航班取消了，则这架飞机暂时没有了飞行任务
                                    // 在接到下一个任务之前，其航班串才补上
                                    if (firstFlight.delayTime != -1){
                                        flightLine1.flightLine.addAll(flightLine.flightLine);
                                    }
                                    break;
                                }
                                if ((flight.actualLandingTime > assignTime)
                                        && flight.landingAirport.equals(airport)){
                                    FlightLine nFlightLine = new FlightLine();
                                    for (Flight flight1 : flightLine1.flightLine){
                                        if (flight1.actualLandingTime > flight.actualLandingTime){
                                            nFlightLine.flightLine.add(flight1);
                                        }
                                    }
                                    flightLine1.flightLine.removeAll(nFlightLine.flightLine);
                                    // 说明航班没取消，如果航班取消了，则这架飞机暂时没有了飞行任务
                                    // 在接到下一个任务之前，其航班串才补上
                                    if (firstFlight.delayTime != -1){
                                        flightLine1.flightLine.addAll(flightLine.flightLine);
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                if (firstFlight.delayTime == -1){
                    FlightLine nFlightLine = new FlightLine();
                    nFlightLine.flightLine = new ArrayList<>(flightLine.flightLine);
                    // 删除取消航班
                    nFlightLine.flightLine.remove(0);
                    CommonState.GlobalFlightLinesList.add(nFlightLine);
                }
            }
            System.out.println();
            return true;
        }
        else {
            return false;
        }
    }
}
