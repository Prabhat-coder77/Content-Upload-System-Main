package com.example.coursecontent.validation;

import com.example.coursecontent.exception.FileTooLargeException;
import com.example.coursecontent.exception.InvalidFileTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileValidationService {

    private final List<String> allowedTypes;
    private final List<String> allowedExtensions;
    private final long maxFileSize;

    public FileValidationService(
            UploadProperties uploadProperties,
            @Value("${spring.servlet.multipart.max-file-size:25MB}") String maxFileSizeStr) {
        this.allowedTypes = uploadProperties.getAllowedTypes();
        this.allowedExtensions = uploadProperties.getAllowedExtensions();
        this.maxFileSize = parseSize(maxFileSizeStr);
    }

    public void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new FileTooLargeException("File size exceeds logic limit of " + maxFileSize + " bytes");
        }

        String mimeType = file.getContentType();
        if (mimeType == null || !allowedTypes.contains(mimeType.toLowerCase())) {
            throw new InvalidFileTypeException("File type " + mimeType + " is not supported");
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null || !allowedExtensions.contains(extension.toLowerCase())) {
            throw new InvalidFileTypeException("File extension " + extension + " is not allowed");
        }
    }

    private long parseSize(String size) {
        size = size.toUpperCase();
        if (size.endsWith("KB")) {
            return Long.parseLong(size.replace("KB", "").trim()) * 1024;
        } else if (size.endsWith("MB")) {
            return Long.parseLong(size.replace("MB", "").trim()) * 1024 * 1024;
        } else if (size.endsWith("GB")) {
            return Long.parseLong(size.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        } else {
            return Long.parseLong(size.trim()); // Assume bytes
        }
    }
}
