
package com.x.controller.common;
import com.x.utils.AliOssUtil;
import com.x.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@CrossOrigin
@Slf4j
public class UploadController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/user/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
        try {
            String originalFilename = file.getOriginalFilename();//原始文件名
            //截取原始文件名的的后缀
            String extention = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString()+extention;
            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        }catch(IOException e){
            log.error("文件上传失败：{}",e);
        }
        return null;
    }
}