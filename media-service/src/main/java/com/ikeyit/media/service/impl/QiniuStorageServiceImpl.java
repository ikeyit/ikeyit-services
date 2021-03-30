package com.ikeyit.media.service.impl;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.ikeyit.media.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Service
public class QiniuStorageServiceImpl implements StorageService {

    @Value("${storageService.qiniu.accessKey}")
    String accessKey;

    @Value("${storageService.qiniu.secretKey}")
    String secretKey;

    @Value("${storageService.qiniu.bucket}")
    String bucket;

    @Value("${storageService.qiniu.baseUrl}")
    String baseUrl;

    @Value("${storageService.qiniu.savePath}")
    String savePath;

    int fsizeLimit = 1048576;

    String mimeLimit = "image/*";

    StringMap putPolicy = new StringMap();

    long expireSeconds = 3600;


    @PostConstruct
    public void init() {
        putPolicy.put("saveKey", savePath + "${etag}${ext}");
        putPolicy.put("returnBody", "{" +
                "\"url\":\"" + baseUrl + "${key}\"," +
                "\"fileName\":\"$(fname)\"," +
                "\"extension\":\"$(ext)\"," +
                "\"fileType\":0," +
                "\"size\":$(fsize)," +
                "\"width\":$(imageInfo.width)," +
                "\"height\":$(imageInfo.height)" +
                "}");
        putPolicy.put("fsizeLimit", fsizeLimit);
        putPolicy.put("mimeLimit", mimeLimit);
    }

    public HashMap<String, Object> prepareUploadImage() {
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucket, null, expireSeconds, putPolicy);
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("token", token);
        response.put("expire", expireSeconds);
        return response;
    }
}
