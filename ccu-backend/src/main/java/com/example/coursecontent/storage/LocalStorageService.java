package com.example.coursecontent.storage;

import com.example.coursecontent.exception.NotFoundException;
import com.example.coursecontent.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "LOCAL", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final Path rootLocation;

    public LocalStorageService(@Value("${app.storage.local.root}") String localRoot) {
        this.rootLocation = Paths.get(localRoot);
        init();
    }

    private void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(String originalFilename, InputStream inputStream, long size, String contentType) {
        try {
            if (inputStream == null) {
                throw new StorageException("Failed to store empty file.");
            }
            
            String extension = "";
            int extIndex = originalFilename.lastIndexOf('.');
            if (extIndex > 0) {
                extension = originalFilename.substring(extIndex);
            }
            
            String generatedFilename = UUID.randomUUID().toString() + extension;
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(generatedFilename))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            Files.copy(inputStream, destinationFile,
                    StandardCopyOption.REPLACE_EXISTING);
                    
            return generatedFilename;
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        try {
            Path file = rootLocation.resolve(storageKey).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new NotFoundException("Could not read file: " + storageKey);
            }
        } catch (MalformedURLException e) {
            throw new NotFoundException("Could not read file: " + storageKey);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Path file = rootLocation.resolve(storageKey).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file: " + storageKey, e);
        }
    }
}
