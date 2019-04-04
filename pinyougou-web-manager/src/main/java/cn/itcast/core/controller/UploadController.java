package cn.itcast.core.controller;

import cn.itcast.common.utils.FastDFSClient;
import cn.itcast.core.service.UploadExcelService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传图片管理
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/upload")
public class UploadController {

    //专门接受Excel表格并且往数据库表批量添加数据的实现类

    @Value("${FILE_SERVER_URL}")
    private String fsu;

    //上传图片
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){



        try {
            System.out.println(file.getOriginalFilename());


            //上传图片到分布式文件系统上了 FastDFS 是什么语言C写的  FastDFS的Client客户端 连接 FastDFS服务器
            //1:服务端   （原理） 分布式文件系统有什么好处啊 为什么选择使用？
            //2:客户端  （Java 版客户端 ） 必须掌握的
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");

            //扩展名
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());

            //上传图片
            String path = fastDFSClient.uploadFile(file.getBytes(), ext, null);

            return new Result(true, fsu + path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }


    @Reference
    private UploadExcelService uploadExcelService;

    @RequestMapping("/uploadExcel")
    public Result uploadExcel(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("文件是空的");
            }

            System.out.println(file.getOriginalFilename());
            String name = (file.getOriginalFilename().split("\\."))[0];
            addExcelToTable(name,file);

            return new Result(true, "导入成功");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "导入表格失败");
        }

    }



    /**
     * 根据传递的文件,组装数据,传递到service层
     * @param file
     * @param tableName
     * @param rowSize
     * @throws IOException
     */
    public void addExcelToTable( String name ,MultipartFile file) throws IOException {
        //指定页,行,格
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        List<List<Object>> list = new ArrayList<>();

        //使用输入流读取文件的输入流
        InputStream inputStream = file.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(bufferedInputStream);

        //创建工作表
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);

        //获取工作簿的每一页,为空的继续
        for (int i = 0; i < hssfWorkbook.getNumberOfSheets(); i++) {
            sheet = hssfWorkbook.getSheetAt(i);
            if (sheet == null) {
                continue;//如果该页码是空,跳过,直到遍历完所有的页
            }
            //遍历页sheet中所有的行,第一行是标题,第二行是字段名,第三行开始是数据
            if (sheet.getLastRowNum() > 2) {
                for (int j = 2; j <= sheet.getLastRowNum(); j++) {

                    //获取一行的数据
                    row = sheet.getRow(j);

                    List<Object> li = new ArrayList<>();

                    if (row != null) {
                        for (int k = row.getFirstCellNum(); k < row.getLastCellNum() ; k++) {
                            cell = row.getCell(k);
                            if (null==cell||cell.getStringCellValue().trim() == "") {
                                break;
                            }
                            //将每个单元格的数据添加到li集合中
                            li.add(cell.getStringCellValue());
                        }
                    }
                    //当一行读取完成后,将一行的数据放入List集合中
                    if (li.size() > 0) {
                        list.add(li);
                    }
                }
            }

        }

        if (list.size() == 0) {
            //从excel第三行读取不到数据了
            throw new RuntimeException("没有数据");
        }
        uploadExcelService.addExcelToTable(name,list);
    }

}
