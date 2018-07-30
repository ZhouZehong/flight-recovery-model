package data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import entities.Visitor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

import entities.Aircraft;
import entities.Flight;
import entities.FlightLine;
import util.CommonState;


public class DataReader {
    @SuppressWarnings("resource")
    public static Boolean readExcel()throws IOException
    {
        List<Aircraft> dataAircrafts = new ArrayList<Aircraft>();
        List<Flight> dataFlights = new ArrayList<Flight>();
        List<FlightLine> dataFlightLines = new ArrayList<FlightLine>();
        List<Visitor> dataVisitors = new ArrayList<Visitor>();

        //读取aircraft的excel
        InputStream inputAircraft = new FileInputStream("C:/Users/Bean/Documents/Aircrafts.xlsx");
        XSSFWorkbook xssfWorkBook = new XSSFWorkbook(inputAircraft);
        // Read the Sheet
        for (int i = 0; i < xssfWorkBook.getNumberOfSheets(); i++)
        {
            XSSFSheet xssfSheet = xssfWorkBook.getSheetAt(i);
            if (xssfSheet == null)
            {
                continue;
            }
            //由于有表头，所以需要从第一行开始读
            for (int j = 1; j <= xssfSheet.getLastRowNum(); j++)
            {
                XSSFRow xssfRow = xssfSheet.getRow(j);
                if (xssfRow != null)
                {
                    Aircraft tempAircraft = new Aircraft();
                    tempAircraft.aircraftID = getValue(xssfRow.getCell(0));
                    tempAircraft.aircraftType = getValue(xssfRow.getCell(1));
                    tempAircraft.earliestUsableTime = (long) xssfRow.getCell(2).getNumericCellValue();
                    tempAircraft.latestUsableTime = (long) xssfRow.getCell(3).getNumericCellValue();
                    tempAircraft.airport = getValue(xssfRow.getCell(4));
                    tempAircraft.seatNum = (int) xssfRow.getCell(5).getNumericCellValue();
                    dataAircrafts.add(tempAircraft);
                }
            }
        }

        //读取flight的excel
        inputAircraft = new FileInputStream("C:/Users/Bean/Documents/Schedules.xlsx");
        xssfWorkBook = new XSSFWorkbook(inputAircraft);
        // Read the Sheet
        for (int i = 0; i < xssfWorkBook.getNumberOfSheets(); i++)
        {
            XSSFSheet xssfSheet = xssfWorkBook.getSheetAt(i);
            if (xssfSheet == null)
            {
                continue;
            }

            FlightLine tempFlightLine = null;
            //由于有表头，所以需要从第一行开始读
            for (int j = 1; j <= xssfSheet.getLastRowNum(); j++)
            {
                XSSFRow xssfRow = xssfSheet.getRow(j);
                if (xssfRow != null)
                {
                    Flight tempFlight = new Flight();
                    tempFlight.flightNum = (long) xssfRow.getCell(0).getNumericCellValue();
                    tempFlight.plannedDepartureTime = (long) xssfRow.getCell(1).getNumericCellValue();
                    tempFlight.actualDepartureTime = tempFlight.plannedDepartureTime;
                    tempFlight.plannedLandingTime = (long) xssfRow.getCell(2).getNumericCellValue();
                    tempFlight.actualLandingTime = tempFlight.plannedLandingTime;
                    tempFlight.departureAirport = getValue(xssfRow.getCell(3));
                    tempFlight.landingAirport = getValue(xssfRow.getCell(4));
                    tempFlight.planedAircraftType = getValue(xssfRow.getCell(5));
                    tempFlight.actualAircraftType = tempFlight.planedAircraftType;
                    tempFlight.plannedAircraftNum = getValue(xssfRow.getCell(6));
                    tempFlight.actualAircraftNum = tempFlight.plannedAircraftNum;
                    tempFlight.delayTime = 0;
                    dataFlights.add(tempFlight);

                    if(j==1) {
                        tempFlightLine = new FlightLine();
                        tempFlightLine.flightLine.add(tempFlight);
                    }
                    if(j>1)
                    {
                        if(tempFlight.plannedAircraftNum.equals(dataFlights.get(j-2).plannedAircraftNum))
                        {
                            tempFlightLine.flightLine.add(tempFlight);
                        }
                        else
                        {
                            dataFlightLines.add(tempFlightLine);
                            tempFlightLine = new FlightLine();
                            tempFlightLine.flightLine.add(tempFlight);
                        }
                    }
                    if (j==xssfSheet.getLastRowNum()){
                        dataFlightLines.add(tempFlightLine); // 别忘了最后一条航班串
                    }
                }
            }
        }


        inputAircraft = new FileInputStream("C:/Users/Bean/Documents/Paxinfo.xlsx");
        xssfWorkBook = new XSSFWorkbook(inputAircraft);
        // Read the Sheet
        for (int i = 0; i < xssfWorkBook.getNumberOfSheets(); i++)
        {
            XSSFSheet xssfSheet = xssfWorkBook.getSheetAt(i);
            if (xssfSheet == null)
            {
                continue;
            }

            //由于有表头，所以需要从第一行开始读
            for (int j = 1; j <= xssfSheet.getLastRowNum(); j++)
            {
                XSSFRow xssfRow = xssfSheet.getRow(j);
                if (xssfRow != null)
                {
                    Visitor tempVisitor = new Visitor();
                    tempVisitor.visitorID = (long) xssfRow.getCell(0).getNumericCellValue();
                    tempVisitor.flightID = (long) xssfRow.getCell(1).getNumericCellValue();
                    tempVisitor.visitorNum = (long) xssfRow.getCell(2).getNumericCellValue();
                    for(int flightCount=0; flightCount<dataFlights.size();flightCount++)
                    {
                        if(dataFlights.get(flightCount).flightNum==tempVisitor.flightID)
                        {
                            tempVisitor.currentFlight = dataFlights.get(flightCount);
                            tempVisitor.lastFlight = null;
                            dataFlights.get(flightCount).planeInSeatNum += (int)tempVisitor.visitorNum;
                        }
                    }
                    tempVisitor.isBandoned = false;
                    dataVisitors.add(tempVisitor);


                }
            }
        }

        CommonState.GlobalAircraftsList = dataAircrafts;
        CommonState.GlobalFlightLinesList = dataFlightLines;
        CommonState.GlobalFlightsList = dataFlights;
        CommonState.GlobalVisitorList = dataVisitors;

        return true;
    }

    @SuppressWarnings({ "deprecation", "static-access" })
    private static String getValue(XSSFCell xssfCell)
    {
        if (xssfCell.getCellType() == xssfCell.CELL_TYPE_BOOLEAN)
        {
            return String.valueOf(xssfCell.getBooleanCellValue());
        }
        else if (xssfCell.getCellType() == xssfCell.CELL_TYPE_NUMERIC)
        {
            return String.valueOf(xssfCell.getNumericCellValue());
        }
        else
        {
            return String.valueOf(xssfCell.getStringCellValue());
        }
    }

}
