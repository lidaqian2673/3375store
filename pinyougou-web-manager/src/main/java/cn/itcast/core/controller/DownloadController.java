package cn.itcast.core.controller;


import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.DownloadExcelService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


@RestController
@RequestMapping("/download")
public class DownloadController {

    @Reference
    private DownloadExcelService downloadExcelService;

    @RequestMapping("/exportExcel")
    public void exportExcel(String tableName,HttpServletResponse response) {
        try {
            List<Object> dataList = (List<Object>) downloadExcelService.downloadExcelByTableName(tableName);
            //读取查询到的数据
            if (null == dataList) {
                throw new Exception();
            }
            //读取从数据库表中获得的数据
            if ("tb_brand".equals(tableName)) {
                exportBrandExcel(dataList,response);
            }else if ("tb_specification".equals(tableName)) {
                exportSpecificationExcel(dataList,response);
            }else if ("tb_type_template".equals(tableName)) {
                exportTypeTemplateExcel(dataList,response);
            } else if ("tb_item_cat".equals(tableName)) {
                exportItemCatExcel(dataList,response);
            }

//            return new Result(true,"导出成功");
        } catch (Exception e) {
            e.printStackTrace();
//            return new Result(false,"导出失败");
        }
    }



    private void exportBrandExcel(List<Object> dataList,HttpServletResponse response) throws IOException {
        Workbook wb = new HSSFWorkbook();
        String headers[] = { "id", "名字", "首字母" };
        int rowIndex = 0;
        Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(rowIndex++);
        for (int i = 0; i < headers.length; i++) { // 先写表头
            row.createCell(i).setCellValue(headers[i]);
        }

        for (Object o : dataList) {
            Brand brand = (Brand) o;
            row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(brand.getId());
            row.createCell(1).setCellValue(brand.getName());
            row.createCell(2).setCellValue(brand.getFirstChar());
        }
        response.setHeader("Content-Disposition",
                "attachment;filename=" + new String("手动导出excel.xls".getBytes("utf-8"), "iso8859-1"));
        response.setContentType("application/ynd.ms-excel;charset=UTF-8");
        OutputStream out = response.getOutputStream();
//        String name = String.valueOf(System.currentTimeMillis());  这种是导入到服务器的D盘了
//        FileOutputStream fout = new FileOutputStream("D://" + name + "brand.xls");
        wb.write(out);
        out.flush();
        out.close();

    }

    private void exportSpecificationExcel(List<Object> dataList, HttpServletResponse response) {

    }

    private void exportTypeTemplateExcel(List<Object> dataList, HttpServletResponse response) {

    }

    private void exportItemCatExcel(List<Object> dataList, HttpServletResponse response) {
    }



}
