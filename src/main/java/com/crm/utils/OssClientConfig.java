package com.crm.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssClientConfig {
    @Value("${oss.endpoint}")
    String endpoint;

    @Value("${oss.accessKeyId}")
    String accessKeyId;

    @Value("${oss.accessKeySecret}")
    String accessKeySecret;

    @Bean
    public OSSClient createOssClient(){
        return (OSSClient) new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
