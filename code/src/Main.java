import assign.Assign1;
import assign.Assign2;
import assign.Assign3;
import assign.Assign4;
import data.DataReader;
import data.DataWriter;
import entities.Flight;
import entities.FlightLine;
import javafx.collections.transformation.FilteredList;
import util.CommonState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 主函数
 */
public class Main {
    public static void main(String[] args) throws IOException {
//        KM myKm = new KM(3);
//        long[][] myDelay = new long[][] {{3, -0x7fffffff, 4}, {2, 1, 3}, {-0x7fffffff, -0x7fffffff, 5}};
//        int[][] matchResult = myKm.getMaxBipartie(myDelay);
//        for (int i = 0; i < matchResult.length; i++) {
//            System.out.println(matchResult[i][0] + " , " + matchResult[i][1]);
//        }
        List<Flight> compareList = new ArrayList<>();
        DataReader.readExcel();
//        // 先将所有航线都放入延误航线表中，再进行剔除
//        CommonState.DelayFlighLinesList = new ArrayList<>(CommonState.GlobalFlightLinesList);
        boolean whetherContinue = Assign4.assign3("OVS", 1461348000);
        Collections.sort(CommonState.DelayFlighLinesList);

        while (CommonState.DelayFlighLinesList.size() != 29){
            String airport = "null";
            long assignTime = -1;
            List<FlightLine> tmpFlightLineList = new ArrayList<>();
            for (FlightLine flightLine : CommonState.DelayFlighLinesList){
                Flight firstFlight = flightLine.flightLine.get(0);
                if (!compareList.contains(firstFlight)){
                    airport = firstFlight.departureAirport;
                    assignTime = firstFlight.plannedDepartureTime;
                    break;
                }
                else {
                    flightLine.flightLine.remove(0);
                    if (flightLine.flightLine.size() < 0){
                        tmpFlightLineList.add(flightLine);
                    }
                }
            }
            CommonState.DelayFlighLinesList.removeAll(tmpFlightLineList);
            if (!airport.equals("null")){
                whetherContinue = Assign4.assign3(airport, assignTime);
                Collections.sort(CommonState.DelayFlighLinesList);
            }
            // 完全剔除DelayList中重复的航班串
//            if ((!whetherContinue) && (CommonState.DelayFlighLinesList.size() > 0)){
//                airport = CommonState.DelayFlighLinesList.get(1).flightLine.get(0).departureAirport;
//                assignTime = CommonState.DelayFlighLinesList.get().flightLine.get(0).plannedDepartureTime;
//                whetherContinue = Assign2.assign2(airport, assignTime);
//            }
        }

        // 遍历航班串，导出新的时刻表
        DataWriter.writeExcel();
    }
}
