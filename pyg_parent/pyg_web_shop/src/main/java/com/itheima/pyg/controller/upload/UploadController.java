package com.itheima.pyg.controller.upload;

import com.itheima.pyg.entity.Result;
import com.itheima.pyg.util.FastDFSClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String  FILE_SERVER_URL;

    /**
     * 上传图片至fastDFS服务器
     * @param file
     * @return
     */
    @RequestMapping("uploadFile")
    public Result uploadFile(MultipartFile file){
        try {
            String conf = "classpath:fastDFS/fdfs_client.conf";
            FastDFSClient fastDFSClient = new FastDFSClient(conf);
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String path = fastDFSClient.uploadFile(file.getBytes(), extension, null);
            String url = FILE_SERVER_URL + path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
