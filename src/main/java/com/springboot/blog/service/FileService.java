package com.springboot.blog.service;

import java.io.File;

public interface FileService {
    String getFileBase64String(File file);

    String getFileExtension(String fileName);

    String padBase64(String base64String, String fileExtension);
}
