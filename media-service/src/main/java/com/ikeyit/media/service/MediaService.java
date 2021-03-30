package com.ikeyit.media.service;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.media.domain.MediaFile;


public interface MediaService {

    MediaFile createMediaFile(MediaFile mediaFile);

    Page<MediaFile> getMediaFiles(Integer fileType, Long folderId, PageParam pageParam);
}
