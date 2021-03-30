package com.ikeyit.media.controller;

import com.ikeyit.media.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class UploadController {

    @Autowired
    StorageService storageService;

    public UploadController() {

    }

    @GetMapping("/prepare_upload/image")
    public HashMap<String, Object> prepareUploadImage() {
        return storageService.prepareUploadImage();
    }
}
