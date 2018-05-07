package com.mall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by wxhong on 2018/5/4.
 */
public interface IFileService {
     String upload(MultipartFile file, String path);
}
