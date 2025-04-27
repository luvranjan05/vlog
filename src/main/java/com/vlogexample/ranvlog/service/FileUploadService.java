package com.vlogexample.ranvlog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "uploads/";

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot upload empty file.");
        }

        // Ensure the upload directory exists
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate a unique file name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        // Save file to the server
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName; // Return the filename to store in the database
    }

    public byte[] getFile(String fileName) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        return Files.readAllBytes(filePath);
    }
}
