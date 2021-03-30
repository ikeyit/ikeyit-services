package com.ikeyit.media.controller;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.media.domain.MediaFile;
import com.ikeyit.media.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MediaController {

    @Autowired
    MediaService mediaService;

    @GetMapping("/seller/medias")
    public Page<MediaFile> getMediaFiles(@RequestParam(required = false) Integer fileType,
                                     @RequestParam(required = false) Long folderId,
                                     @RequestParam(required = false, defaultValue = "1") int page,
                                     @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return mediaService.getMediaFiles(fileType, folderId, new PageParam(page, pageSize));
    }

    @PostMapping("/seller/media")
    public MediaFile createMediaFile(@RequestBody MediaFile mediaFile) {
        return mediaService.createMediaFile(mediaFile);
    }

}
