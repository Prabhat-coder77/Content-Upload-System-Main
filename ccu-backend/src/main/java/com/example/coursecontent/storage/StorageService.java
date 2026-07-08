package com.example.coursecontent.storage;

import org.springframework.core.io.Resource;

import java.io.InputStream;

public interface StorageService {
    
    /**
     * Stores the file and returns the generated storage key.
     */
    String store(String originalFilename, InputStream inputStream, long size, String contentType);
    
    /**
     * Loads the file as a Resource to be streamed down.
     */
    Resource loadAsResource(String storageKey);
    
    /**
     * Delete the file.
     */
    void delete(String storageKey);
}
