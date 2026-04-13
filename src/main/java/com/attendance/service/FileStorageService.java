package com.attendance.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "application/pdf"
    );
    private static final int MAX_FILES = 5;
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10 MB

    @Value("${app.upload.dir}")
    private String uploadDir;

    public void validate(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;
        if (files.size() > MAX_FILES) {
            throw new IllegalArgumentException("Too many attachments. Maximum allowed: " + MAX_FILES);
        }
        for (MultipartFile file : files) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File '" + file.getOriginalFilename() + "' exceeds 10 MB limit");
            }
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
                throw new IllegalArgumentException(
                        "Unsupported file type '" + contentType + "'. Allowed: JPEG, PNG, GIF, PDF");
            }
        }
    }

    public String store(MultipartFile file, Long amendmentId) throws IOException {
        // Validate and sanitize original filename - prevent path traversal
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) originalFilename = "unknown";
        String sanitized = Paths.get(originalFilename).getFileName().toString();
        String extension = "";
        int dotIdx = sanitized.lastIndexOf('.');
        if (dotIdx > 0) extension = sanitized.substring(dotIdx);

        String storedName = UUID.randomUUID() + extension;
        Path targetDir = Paths.get(uploadDir, "amendments", amendmentId.toString()).toAbsolutePath().normalize();
        Files.createDirectories(targetDir);
        Path targetPath = targetDir.resolve(storedName).normalize();

        // Path traversal guard
        if (!targetPath.startsWith(targetDir)) {
            throw new SecurityException("Path traversal attempt detected");
        }
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toString();
    }

    public Path resolveSecurePath(String storedPath) {
        Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path resolved = Paths.get(storedPath).toAbsolutePath().normalize();
        if (!resolved.startsWith(basePath)) {
            throw new SecurityException("Access to path outside upload directory is forbidden");
        }
        return resolved;
    }
}
