package com.x.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.x.properties.AliOssProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Data
@AllArgsConstructor
@Component
@Slf4j
public class AliOssUtil {

   @Autowired
   private AliOssProperties aliOssProperties;

    /**
     * 文件上传
     *
     * @param bytes
     * @param objectName
     * @return
     */
    public String upload(byte[] bytes, String objectName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().
                build(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(),
                        aliOssProperties.getAccessKeySecret());
        // 创建PutObject请求。
        ossClient.putObject(aliOssProperties.getBucketName(), objectName,
                new ByteArrayInputStream(bytes));
        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append( aliOssProperties.getBucketName())
                .append(".")
                .append( aliOssProperties.getEndpoint())
                .append("/")
                .append(objectName);
        log.info("文件上传到:{}", stringBuilder.toString());
        return stringBuilder.toString();
    }
    public void delete(String objectName){
        OSS ossClient = new OSSClientBuilder().build(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());
        try {
            ossClient.deleteObject(aliOssProperties.getBucketName(), objectName);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
        }
        catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
        finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
            log.info("文件删除成功:{}",objectName);
        }

    }
}
