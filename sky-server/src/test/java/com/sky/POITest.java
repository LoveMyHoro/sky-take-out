package com.sky;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * @ClassName POITest
 * @Author iove
 * @Date 2025/1/13 下午5:40
 * @Version 1.0
 * @Description TODO
 **/

public class POITest {
	public static void write() throws IOException {
		//1.在内存中创建一个excel文件
		XSSFWorkbook excel = new XSSFWorkbook();
		//2.创建一个新页
		XSSFSheet sheet = excel.createSheet("info");
		//3.创建单元行
		XSSFRow row = sheet.createRow(0);
		//4.创建单元列
		row.createCell(0).setCellValue("姓名");
		row.createCell(1).setCellValue("年龄");
		//5.创建输出流
		FileOutputStream out = new FileOutputStream(new File("E:\\info.xlsx"));
		//6.导出excel表格
		excel.write(out);
		//7.关闭资源
		out.close();
		excel.close();
	}
	public static void read() throws IOException {
		FileInputStream inputStream = new FileInputStream(new File("E:\\info.xlsx"));
		XSSFWorkbook excel = new XSSFWorkbook(inputStream);

		XSSFSheet sheet = excel.getSheetAt(0);
		int lastRowNum=sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = 0; i <= lastRowNum; i++) {
			XSSFRow row=sheet.getRow(i);
			String cellValue1 =row.getCell(0).getStringCellValue();
			String celleValue2=row.getCell(1).getStringCellValue();
			System.out.println("cellValue1="+ cellValue1);
			System.out.println("cellValue2="+celleValue2);
		}
		inputStream.close();
		excel.close();


	}
	public static void main(String args[]) throws IOException {
		//write();
		read();
	}
}
