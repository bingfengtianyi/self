package com.itheima.pyg.controller.image;

import org.apache.poi.hssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class ImageController {
    private static String outputFile = "D:\\test.xls";

    public static void main(String[] args) {
        try {
            // 创建新的Excel 工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 在Excel工作簿中建一工作表，其名为缺省值
            // 如要新建一名为"效益指标"的工作表，其语句为：
            // HSSFSheet sheet = workbook.createSheet("效益指标");
            HSSFSheet sheet = workbook.createSheet("用户表");
            sheet.setDefaultRowHeight((short) (2 * 256));//设置行高
            sheet.setColumnWidth(0, 4000);//设置列宽
            sheet.setColumnWidth(1,5500);
            sheet.setColumnWidth(2,5500);
            sheet.setColumnWidth(3,5500);
            sheet.setColumnWidth(11,3000);
            sheet.setColumnWidth(12,3000);
            sheet.setColumnWidth(13,3000);
            HSSFFont font = workbook.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 16);


            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell(0);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);//已过时
            cell.setCellValue("用户名");
            cell = row.createCell(1);
            cell.setCellValue("注册手机号");
            cell = row.createCell(2);
            cell.setCellValue("注册邮箱");
            cell = row.createCell(3);
            cell.setCellValue("创建时间");
            cell = row.createCell(4);
            cell.setCellValue("更新时间");
            cell = row.createCell(5);
            cell.setCellValue("会员来源");
            cell = row.createCell(6);
            cell.setCellValue("昵称");
            cell = row.createCell(7);
            cell.setCellValue("真实姓名");
            cell = row.createCell(8);
            cell.setCellValue("使用状态");
            cell = row.createCell(9);
            cell.setCellValue("QQ号码");
            cell = row.createCell(10);
            cell.setCellValue("账户余额");
            cell = row.createCell(11);
            cell.setCellValue("手机是否验证");
            cell = row.createCell(12);
            cell.setCellValue("邮箱是否验证");
            cell = row.createCell(13);
            cell.setCellValue("性别");
            cell = row.createCell(14);
            cell.setCellValue("会员等级");
            cell = row.createCell(15);
            cell.setCellValue("积分");
            cell = row.createCell(16);
            cell.setCellValue("经验值");
            cell = row.createCell(17);
            cell.setCellValue("生日");
            cell = row.createCell(18);
            cell.setCellValue("最后登录时间");

            // 新建一输出文件流
            FileOutputStream fOut = new FileOutputStream(outputFile);
            // 把相应的Excel 工作簿存盘
            workbook.write(fOut);
            fOut.flush();
            // 操作结束，关闭文件
            fOut.close();
            System.out.println("文件生成...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
