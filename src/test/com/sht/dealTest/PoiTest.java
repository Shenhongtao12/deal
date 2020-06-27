package com.sht.dealTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PoiTest {

    @Test
    public void createExcel1() throws Exception {
        //1.创建workbook工作簿
        Workbook wb = new XSSFWorkbook();
        //2.创建表单Sheet        
        Sheet sheet = wb.createSheet("test");
        //创建行对象,从0开始
        Row row = sheet.createRow(3);
        //创建单元格,从0开始
        Cell cell = row.createCell(0);
        //单元格写入数据
        cell.setCellValue("树根互联");  //在第四行的第一格输入数据
        //文件流        
        FileOutputStream fos = new FileOutputStream("D:\\A_Users\\Desktop\\test.xlsx");
        //写入文件        
        wb.write(fos);
        //关闭流
        fos.close();
    }

    @Test
    public void createExcel2() throws Exception {
        //1.创建workbook工作簿
        Workbook wb = new XSSFWorkbook();
        //2.创建表单Sheet        
        Sheet sheet = wb.createSheet("test");
        //创建行对象,从0开始
        Row row = sheet.createRow(0);
        //创建单元格,从0开始
        Cell cell = row.createCell(0);
        //单元格写入数据
        cell.setCellValue("树根互联");  //在第四行的第一格输入数据
        //创建单元格样式对象        
        CellStyle cellStyle = wb.createCellStyle();
        //设置边框        
        cellStyle.setBorderBottom(BorderStyle.DASH_DOT);//下边框      
        cellStyle.setBorderTop(BorderStyle.HAIR);//上边框
        //设置字体        
        Font font = wb.createFont();//创建字体对象        
        font.setFontName("华文行楷");//设置字体        
        font.setFontHeightInPoints((short)28);
        //设置字号        
        cellStyle.setFont(font);
        //设置宽高        
        sheet.setColumnWidth(0, 31 * 256);//设置第一列的宽度是31个字符宽度 
        row.setHeightInPoints(50);//设置行的高度是50个点
        //设置居中显示        
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中       cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        //设置单元格样式        
        cell.setCellStyle(cellStyle);
        //合并单元格        
        CellRangeAddress region =new CellRangeAddress(0, 3, 0, 2);
        sheet.addMergedRegion(region);
        //文件流        
        FileOutputStream fos = new FileOutputStream("D:\\A_Users\\Desktop\\test.xlsx");
        //写入文件        
        wb.write(fos);
        fos.close();
    }

    @Test
    public void createExcelImage() throws Exception {
        //1.创建workbook工作簿
        Workbook wb = new XSSFWorkbook();
        //2.创建表单Sheet        
        Sheet sheet = wb.createSheet("test");
        //读取图片流        
        FileInputStream stream=new FileInputStream("C:\\Users\\hongtao.shen\\Desktop\\笔记\\images\\git.png");
        byte[] bytes= IOUtils.toByteArray(stream);
        // 读取图片到二进制数组        
        stream.read(bytes);
        // 向Excel添加一张图片,并返回该图片在Excel中的图片集合中的下标        
        int pictureIdx = wb.addPicture(bytes,Workbook.PICTURE_TYPE_JPEG);
        // 绘图工具类        
        CreationHelper helper = wb.getCreationHelper();
        //创建一个绘图对象        
        Drawing<?> patriarch = sheet.createDrawingPatriarch();
        // 创建锚点,设置图片坐标        
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(0);//从0开始        
        anchor.setRow1(0);//从0开始
        //创建图片        
        Picture picture = patriarch.createPicture(anchor, pictureIdx);
        picture.resize();

        //文件流        
        FileOutputStream fos = new FileOutputStream("D:\\A_Users\\Desktop\\test.xlsx");
        //写入文件        
        wb.write(fos);
        fos.close();
    }

    @Test
    public void readExcel() throws Exception {
        //1.读取workbook工作簿      
        Workbook wb = new XSSFWorkbook("C:\\Users\\hongtao.shen\\Desktop\\student.xlsx");
        //2.获取sheet 从0开始      
        Sheet sheet = wb.getSheetAt(0);
        Row row = null;
        Cell cell = null;

        //循环所有行        
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            row = sheet.getRow(rowNum);
            StringBuilder sb = new StringBuilder();
            //循环每行中的所有单元格
            for(int cellNum = 0; cellNum < row.getLastCellNum();cellNum++) {
                cell = row.getCell(cellNum);
                sb.append(getValue(cell)).append(" ");
            }
            System.out.println(sb.toString());
        }
    }

    //获取数据    
    private static Object getValue(Cell cell) {
        Object value = null;
        switch (cell.getCellType()) {
            case STRING: //字符串类型                
                value = cell.getStringCellValue();
                break;
            case BOOLEAN: //boolean类型
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC: //数字类型（包含日期和普通数字）                
                if(DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else{
                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA: //公式类型                
                value = cell.getCellFormula();
                break;
            default:
                break;
        }
        return value;
    }
}
