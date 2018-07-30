package data;

import assign.Assign3;
import entities.Aircraft;
import entities.Flight;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import util.CommonState;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class DataWriter {
    @SuppressWarnings("resource")
    public static void writeExcel() throws IOException{

        Workbook workbook = new XSSFWorkbook();
//        String[] title = {"航班唯一编号", "起飞时间", "新起飞时间", "到达时间",
//                "新到达时间", "起飞机场", "到达机场", "飞机型号", "新飞机型号",
//                "飞机尾号", "新飞机尾号", "延误时间(min)"};
        String[] title = {"航班唯一编号", "起飞时间", "新起飞时间", "到达时间",
                "新到达时间", "起飞机场", "到达机场", "飞机型号", "新飞机型号",
                "飞机尾号", "新飞机尾号", "原载客量", "新载客量", "延误时间(hour)"};

        // 创建Sheet对象
        Sheet sheet = workbook.createSheet("sheet1");

        // 样式对象
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直
        style.setAlignment(CellStyle.ALIGN_CENTER);// 水平
        style.setWrapText(true);// 指定当单元格内容显示不下时自动换行

        Font font = workbook.createFont();
        font.setColor((short)18);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setFontHeight((short) 220);
        style.setFont(font);

        CellStyle differentStyle = workbook.createCellStyle();
        differentStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直
        differentStyle.setAlignment(CellStyle.ALIGN_CENTER);// 水平
        differentStyle.setWrapText(true);// 指定当单元格内容显示不下时自动换行

        Font diffFont = workbook.createFont();
        diffFont.setFontName("宋体");
        diffFont.setFontHeight((short) 220);
        differentStyle.setFont(diffFont);

        CellStyle highlightStyle = workbook.createCellStyle();
        highlightStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直
        highlightStyle.setAlignment(CellStyle.ALIGN_CENTER);// 水平
        highlightStyle.setWrapText(true);// 指定当单元格内容显示不下时自动换行

        Font highlightFont = workbook.createFont();
        highlightFont.setColor((short)4);
        highlightFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        highlightFont.setFontName("宋体");
        highlightFont.setFontHeight((short) 220);
        highlightStyle.setFont(highlightFont);


//        // 单元格合并
//        // 四个参数分别是：起始行，起始列，结束行，结束列
//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
//        sheet.autoSizeColumn(5200);

        Row row = sheet.createRow(0);    //创建第1行
        Cell cell;
        for(int i = 0;i < title.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style); // 样式，居中
            sheet.setColumnWidth(i, 20 * 180);
        }
        row.setHeight((short) 600);

        int rowNum = 1;
        //遍历航班串，写入行数据

        for (Flight flight : CommonState.GlobalFlightsList) {
//            if (flight.actualAircraftType.equals("9")){
            row = sheet.createRow(rowNum);
            row.setHeight((short) 600);
            rowNum++;

            Aircraft aAircraft = Assign3.getAirCraftByNum(flight.actualAircraftNum);
            Aircraft pAircraft = Assign3.getAirCraftByNum(flight.plannedAircraftNum);

            Cell flightNumCell = row.createCell(0);
            Cell plannedDTCell = row.createCell(1);
            Cell actualDTCell = row.createCell(2);
            Cell plannedLTCell = row.createCell(3);
            Cell actualLTCell = row.createCell(4);
            Cell dACell = row.createCell(5);
            Cell lACell = row.createCell(6);
            Cell pACell = row.createCell(7);
            Cell aACell = row.createCell(8);
            Cell pNCell = row.createCell(9);
            Cell aNCell = row.createCell(10);
            Cell pSeatCell = row.createCell(11);
            Cell aSeatCell = row.createCell(12);
//            Cell pInSeatCell = row.createCell(13);
//            Cell aInSeatCell = row.createCell(14);
            Cell delayTimeCell = row.createCell(15);

            if ((flight.actualAircraftType.equals("cancel")) || (aAircraft == null)){
                flightNumCell.setCellValue(flight.flightNum);
                flightNumCell.setCellStyle(differentStyle);

                String plannedDepartureTime = new java.text.SimpleDateFormat
                        ("yyyy-MM-dd HH:mm").format(new Date(flight.plannedDepartureTime * 1000 - 1000*60*60*8L));
                plannedDTCell.setCellStyle(differentStyle);
                plannedDTCell.setCellValue(plannedDepartureTime);

                actualDTCell.setCellValue("航班取消");
                actualDTCell.setCellStyle(highlightStyle);

                String plannedLandingTime = new java.text.SimpleDateFormat
                        ("yyyy-MM-dd HH:mm").format(new Date(flight.plannedLandingTime * 1000 - 1000*60*60*8L));
                plannedLTCell.setCellStyle(differentStyle);
                plannedLTCell.setCellValue(plannedLandingTime );

                actualLTCell.setCellValue("航班取消");
                actualLTCell.setCellStyle(highlightStyle);

                dACell.setCellStyle(differentStyle);
                dACell.setCellValue(flight.departureAirport);

                lACell.setCellStyle(differentStyle);
                lACell.setCellValue(flight.landingAirport);

                pACell.setCellStyle(differentStyle);
                pACell.setCellValue(flight.planedAircraftType);

                aACell.setCellValue("航班取消");
                aACell.setCellStyle(highlightStyle);

                pNCell.setCellStyle(differentStyle);
                pNCell.setCellValue(flight.plannedAircraftNum);

                aNCell.setCellValue("航班取消");
                aNCell.setCellStyle(highlightStyle);

                pSeatCell.setCellStyle(differentStyle);
                pSeatCell.setCellValue(pAircraft.seatNum);

                aSeatCell.setCellValue("航班取消");
                aSeatCell.setCellStyle(highlightStyle);

//                pInSeatCell.setCellValue("航班取消");
//                pInSeatCell.setCellStyle(highlightStyle);
//
//                aInSeatCell.setCellValue("航班取消");
//                aInSeatCell.setCellStyle(highlightStyle);

                delayTimeCell.setCellValue("航班取消");
                delayTimeCell.setCellStyle(highlightStyle);
                continue;
            }
            long delayTime = flight.delayTime * aAircraft.seatNum;

            flightNumCell.setCellValue(flight.flightNum);
            flightNumCell.setCellStyle(differentStyle);

            String plannedDepartureTime = new java.text.SimpleDateFormat
                    ("yyyy-MM-dd HH:mm").format(new Date(flight.plannedDepartureTime * 1000 - 1000*60*60*8L));
            plannedDTCell.setCellStyle(differentStyle);
            plannedDTCell.setCellValue(plannedDepartureTime);

            String actualDepartureTime = new java.text.SimpleDateFormat
                    ("yyyy-MM-dd HH:mm").format(new Date(flight.actualDepartureTime * 1000- 1000*60*60*8L));
            if (flight.actualDepartureTime == flight.plannedDepartureTime) {
                actualDTCell.setCellStyle(differentStyle);
            }
            else {
                actualDTCell.setCellStyle(highlightStyle);
            }
            actualDTCell.setCellValue(actualDepartureTime);

            String plannedLandingTime = new java.text.SimpleDateFormat
                    ("yyyy-MM-dd HH:mm").format(new Date(flight.plannedLandingTime * 1000 - 1000*60*60*8L));
            plannedLTCell.setCellStyle(differentStyle);
            plannedLTCell.setCellValue(plannedLandingTime );

            String actualLandingTime = new java.text.SimpleDateFormat
                    ("yyyy-MM-dd HH:mm").format(new Date(flight.actualLandingTime * 1000- 1000*60*60*8L));
            if (flight.actualLandingTime == flight.plannedLandingTime) {
                actualLTCell.setCellStyle(differentStyle);
            }
            else {
                actualLTCell.setCellStyle(highlightStyle);
            }
            actualLTCell.setCellValue(actualLandingTime);

            dACell.setCellStyle(differentStyle);
            dACell.setCellValue(flight.departureAirport);

            lACell.setCellStyle(differentStyle);
            lACell.setCellValue(flight.landingAirport);

            pACell.setCellStyle(differentStyle);
            pACell.setCellValue(flight.planedAircraftType);

            if (flight.actualAircraftType.equals(flight.planedAircraftType)) {
                aACell.setCellStyle(differentStyle);
            }
            else {
                if (aAircraft.seatNum < pAircraft.seatNum){
                    delayTime += (pAircraft.seatNum - aAircraft.seatNum)*2*60*60;
                }
                aACell.setCellStyle(highlightStyle);
            }
            aACell.setCellValue(flight.actualAircraftType);

            pNCell.setCellStyle(differentStyle);
            pNCell.setCellValue(flight.plannedAircraftNum);

            aNCell.setCellStyle(differentStyle);
            aNCell.setCellValue(flight.actualAircraftNum);

            pSeatCell.setCellStyle(differentStyle);
            pSeatCell.setCellValue(pAircraft.seatNum);

            if (pAircraft.seatNum == aAircraft.seatNum) {
                aSeatCell.setCellStyle(differentStyle);
            }
            else {
                aSeatCell.setCellStyle(highlightStyle);
            }
            aSeatCell.setCellValue(aAircraft.seatNum);

//            pInSeatCell.setCellValue(flight.planeInSeatNum);
//            if(aAircraft.inSeatNum<flight.planeInSeatNum)
//            {
//                aInSeatCell.setCellValue(aAircraft.inSeatNum);
//                delayTime+=(flight.planeInSeatNum-aAircraft.inSeatNum)*24*60*60;
//            }
//            else
//            {
//                aInSeatCell.setCellValue(flight.planeInSeatNum);
//            }


            delayTime = delayTime/(60*60);
            if (delayTime == 0) {
                delayTimeCell.setCellStyle(differentStyle);
            }
            else {
                delayTimeCell.setCellStyle(highlightStyle);
            }
            if (delayTime == -1){
                delayTimeCell.setCellValue("航班取消");
            }
            else {
                delayTimeCell.setCellValue(delayTime);
            }
//            }
        }


        //创建文件流
        OutputStream stream = new FileOutputStream("C:/Users/Bean/Documents/NewShcedules.xlsx");
        //写入数据
        workbook.write(stream);
        //关闭文件流
        stream.close();
    }
}
