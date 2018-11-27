package com.itheima.pyg.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelUtils {
	public static void main(String[] args) throws Exception {
//		List<User> list = new ArrayList<User>();
//		for (int i=0;i<10;i++) {
//			User us = new User();
//			us.setName("张三"+i);
//			us.setPhone(i+"11111111111");
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			us.setCreate_time(sdf.format(new Date()));
//			us.setUser_name(i+"wawerer@123123.com");
//			list.add(us);
//		}
//		String columnNames[] = {"姓名","电话","时间","邮箱"};
//		String columns[] = {"name","phone","create_time","user_name"};
//		exportExcelByList("D:\\wang.xls", list, columnNames, columns, "用户信息");
		String[][] mm = readexcell("D:\\wang.xls",1);
		System.out.println(123);
	}
	/**
	 * 读取excel
	 * @param filepath 文件路径
	 * @param startrow 读取的开始行
	 * @Result 返回一个二维数组（第一维放的是行，第二维放的是列表）
	 * @throws Exception
	 */
	public static String[][] readexcell(String filepath,int startrow) throws Exception{
		// 判断文件是否存在
        File file = new File(filepath);
        if (!file.exists()) {
            throw new IOException("文件" + filepath + "W不存在！");
        }
		//获取sheet
        HSSFSheet sheet = getSheet(filepath);
		String[][] content = getData(startrow, sheet);
		return content;
	}
	/**
	 * 读取excel
	 * @param
	 * @param startrow 读取的开始行
	 * @Result 返回一个二维数组（第一维放的是行，第二维放的是列表）
	 * @throws Exception
	 */
	public static String[][] readexcellByInput(InputStream is, String fileName, int startrow) throws Exception{
		//文件后缀
	    String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf("."));
		//获取sheet
        HSSFSheet sheet = null;
		if (".xls".equals(extension)) {//2003
			//获取工作薄
			POIFSFileSystem fs = new POIFSFileSystem(is);
			sheet =  new HSSFWorkbook(fs).getSheetAt(0);
		} else if (".xlsx".equals(extension) || ".xlsm".equals(extension)) {
			sheet =  new HSSFWorkbook(is).getSheetAt(0);
		} else {
			throw new IOException("文件（" + fileName + "）,无法识别！");
		}
		//获取表单数据
		String[][] content = getData(startrow,sheet);
		return content;
	}
	/**
	 * 获取表单数据
	 * wangyue
	 * @param startrow
	 * @param sheet
	 * @return
	 * 2018年4月26日下午2:25:43
	 */
	private static String[][] getData(int startrow, HSSFSheet sheet) {
		// 得到总行数
		int rowNum = sheet.getLastRowNum()+1;
		// 根据第一行获取列数
		Row row = sheet.getRow(0);
		//获取总列数
		int colNum = row.getPhysicalNumberOfCells();
		//根据行列创建二维数组
		String[][] content = new String[rowNum-startrow][colNum];
		String[] cols = null;
		//通过循环，给二维数组赋值
		for (int i = startrow; i < rowNum; i++) {
			row = sheet.getRow(i);
			cols = new String[colNum];
			for (int j = 0; j < colNum; j++) {
				//获取每个单元格的值
				cols[j] = getCellValue(row.getCell(j));
				//把单元格的值存入二维数组
				content[i - startrow][j] =cols[j];
			}
		}
		return content;
	}
	
 
 
	/**
	 * 根据表名获取第一个sheet
	 * @param
	 * @return 2003-HSSFWorkbook  2007-XSSFWorkbook
	 * @throws Exception 
	 */
	public static HSSFSheet getSheet(String file) throws Exception {
		//文件后缀
		String extension = file.lastIndexOf(".") == -1 ? "" : file.substring(file.lastIndexOf("."));
		//创建输入流
		InputStream is = new FileInputStream(file);
		if (".xls".equals(extension)) {//2003
			//获取工作薄
			POIFSFileSystem fs = new POIFSFileSystem(is);
			return new HSSFWorkbook(fs).getSheetAt(0);
		} else if (".xlsx".equals(extension) || ".xlsm".equals(extension)) {
			return new HSSFWorkbook(is).getSheetAt(0);
		} else {
			throw new IOException("文件（" + file + "）,无法识别！");
		}
	}
	  /**
     * 功能:获取单元格的值
     */
    private static String getCellValue(Cell cell) {
        Object result = "";
        if (cell != null) {
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                result = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
             // 在excel里,日期也是数字,在此要进行判断 
				if(HSSFDateUtil.isCellDateFormatted(cell)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					 Date date = cell.getDateCellValue();
					 result =  sdf.format(date); 
				}else{
					DecimalFormat df=new DecimalFormat("#");
					result=df.format(cell.getNumericCellValue());
				}
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                result = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                result = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_ERROR:
                result = cell.getErrorCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                break;
            default:
                break;
            }
        }
        return result.toString();
    }
    /**
     * 导出  ---到固定文件目录
     * 根据传入List数据集合导出Excel表格 生成本地excel
     * @param file （输出流路径）d:\\123.xml
     * @param list 任何对象类型的list（数据库直接查询出的）User（id，name，age，sex)
     * @param columnNames（表头名称）(姓名、性别、年龄)
     * @param columns （表头对应的列名）（name,sex,age）注意顺序
     * @param sheetName（sheet名称）
     */
 	@SuppressWarnings("rawtypes")
 	public static void exportExcelByList(String file, List list, String[] columnNames, String[] columns, String sheetName) {
 		OutputStream fos  =null;
 		try {
			//获取输出流
 			fos= new FileOutputStream(file);
			//创建工作薄HSSFWorkbook
 			HSSFWorkbook wb = new HSSFWorkbook();
			//创建表单sheet
 			HSSFSheet sheet = wb.createSheet(sheetName);
			//创建样式对象
 			HSSFCellStyle style = wb.createCellStyle(); // 样式对象
 			style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
 			style.setAlignment(HorizontalAlignment.CENTER);// 水平
 
 
			//创建行--表头
 			HSSFRow row = sheet.createRow(0);
 			for (int i = 0; i < columnNames.length; i++) {
				//创建列、单元格
 				HSSFCell cell = row.createCell(i);
 				cell.setCellValue(columnNames[i]);
 				cell.setCellStyle(style);
 			}
			//创建数据列
 			for (int i = 0; i < list.size(); i++) {
 				Object o = list.get(i);
				//创建行--数据
 				HSSFRow listRow = sheet.createRow(i + 1);
				//循环列字段数组
 				for (int j = 0; j < columns.length; j++) {
					//创建列
 					HSSFCell listCell = listRow.createCell(j);
 					//根据反射调用方法
 					Method m = o.getClass().getMethod("get" + upperStr(columns[j]));
 					String value = (String) m.invoke(o);
 					if (value != null) {
 						listCell.setCellValue(value);
 						listCell.setCellStyle(style);
 					} else {
 						listCell.setCellValue("");
 						listCell.setCellStyle(style);
 					}
 					sheet.autoSizeColumn(j+1, true);//自适应，从1开始
 				}
 			}
			//把工作薄写入到输出流
 			wb.write(fos);
 			System.out.println("生成excel成功："+file);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}finally {
 			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 	}
 
 
 	/**
 	 * 根据传入List数据集合导出Excel表格 返回页面选择保存路径的excel
 	 * @param response （响应页面）
 	 * @param list 数据列表
 	 * @param columnNames 表头
 	 * @param columns 对应列名
 	 * @param sheetName 
 	 * @param filename
 	 */
 	@SuppressWarnings("rawtypes")
 	public static void exportExcel(HttpServletResponse response, List list, String[] columnNames, String[] columns, String sheetName, String filename) {
 		OutputStream fos = null;
 		String value = null;
 		try {
			//响应输出流，让用户自己选择保存路径
 			response.setCharacterEncoding("UTF-8");
 			response.reset();//清除缓存
 			response.setContentType("octets/stream");
 			response.addHeader("Content-Disposition", "attachment;filename="+ new String((filename).getBytes("UTF-8"), "iso8859-1")+ ".xls");
 			fos = response.getOutputStream();
 
 
 			HSSFWorkbook wb = new HSSFWorkbook();
 			HSSFSheet sheet = wb.createSheet(sheetName);
 			HSSFCellStyle style = wb.createCellStyle(); // 样式对象
 			style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
 			style.setAlignment(HorizontalAlignment.CENTER);// 水平
 
 
 			HSSFRow row = sheet.createRow(0);
 			for (int i = 0; i < columnNames.length; i++) {
 				HSSFCell cell = row.createCell(i);
 				cell.setCellValue(columnNames[i]);
 				cell.setCellStyle(style);
 			}
 			for (int i = 0; i < list.size(); i++) {
 				HSSFRow listRow = sheet.createRow(i + 1);
 				Object o = list.get(i);
 				for (int j = 0; j < columns.length; j++) {
 					HSSFCell listCell = listRow.createCell(j);
 					Method m = o.getClass().getMethod("get" + upperStr(columns[j]));
 					if (m.invoke(o) instanceof Date){
 						Date date = (Date) m.invoke(o);
 						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
						value = dateFormat.format(date);
					}else if (m.invoke(o) instanceof Long){
						Long l = (Long)m.invoke(o);
						value = l.toString();
					}else if (m.invoke(o) instanceof Integer){
 						Integer integer = (Integer)m.invoke(o);
						value = integer.toString();
					}else if (m.invoke(o) instanceof BigDecimal){
						BigDecimal bd = (BigDecimal)m.invoke(o);
						value = bd.toString();
					}else {
						value = (String) m.invoke(o);
					}
					if (value != null) {
						listCell.setCellValue(value + "");
						listCell.setCellStyle(style);
					} else {
						listCell.setCellValue("");
						listCell.setCellStyle(style);
					}
 					sheet.autoSizeColumn(j+1, true);//自适应，从1开始
 				}
 			}
 			wb.write(fos);
 		} catch (Exception e) {
 			e.printStackTrace();
 		} finally {
 			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 	}
 	/**
 	 * 把输入字符串的首字母改成大写
 	 * 
 	 * @param str
 	 * @return
 	 */
 	private static String upperStr(String str) {
 		char[] ch = str.toCharArray();
 		if (ch[0] >= 'a' && ch[0] <= 'z') {
 			ch[0] = (char) (ch[0] - 32);
 		}
 		return new String(ch);
 	}
 
}
