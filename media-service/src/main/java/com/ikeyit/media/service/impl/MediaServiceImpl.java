package com.ikeyit.media.service.impl;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.media.domain.MediaFile;
import com.ikeyit.media.repository.MediaFileRepository;
import com.ikeyit.media.service.MediaService;
import com.ikeyit.passport.resource.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediaServiceImpl implements MediaService {
    @Autowired
    MediaFileRepository mediaFileRepository;

    @Autowired
    AuthenticationService authenticationService;

    public MediaFile createMediaFile(MediaFile mediaFile) {
        Long sellerId = authenticationService.getCurrentUserId();
        mediaFile.setSellerId(sellerId);
        mediaFileRepository.create(mediaFile);
        return mediaFile;
    }

    public Page<MediaFile> getMediaFiles(Integer fileType, Long folderId, PageParam pageParam) {
        Long sellerId = authenticationService.getCurrentUserId();
        return mediaFileRepository.get(sellerId, fileType, folderId, pageParam);
    }
}
