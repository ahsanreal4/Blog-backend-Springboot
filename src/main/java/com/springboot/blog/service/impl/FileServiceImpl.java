package com.springboot.blog.service.impl;

import com.springboot.blog.service.FileService;
import com.springboot.blog.utils.constants.FileTypes;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String getFileBase64String(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());

            return java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');

        if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
            return fileName.substring(lastIndex + 1);
        } else {
            return null;
        }
    }

    @Override
    public String padBase64(String base64String, String fileExtension) {
        if (fileExtension.equalsIgnoreCase(FileTypes.SVG.toString())) {
            return "data:image/svg+xml;base64," + base64String;
        } else if (fileExtension.equalsIgnoreCase(FileTypes.PNG.toString())) {
            return "data:image/png;base64," + base64String;
        }
        else if (fileExtension.equalsIgnoreCase(FileTypes.JPEG.toString())) {
            return "data:image/jpg;base64," + base64String;
        }

        return null;
    }
}
