package com.realtimeradar.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    private final String uploadDir = "/Users/kimhyeseo/uploads"; // 👉 Mac 업로드 폴더

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String uuid = UUID.randomUUID().toString();
            String storedFileName = uuid + "_" + file.getOriginalFilename();
            String uploadPath = uploadDir + "/" + storedFileName;

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file.transferTo(new File(uploadPath));
            return "/uploads/" + storedFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
