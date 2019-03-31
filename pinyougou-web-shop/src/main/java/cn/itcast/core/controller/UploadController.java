package cn.itcast.core.controller;

import cn.itcast.common.utils.FastDFSClient;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传图片管理
 */
@RestController
@RequestMapping("/upload")
public class UploadController {


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
}
