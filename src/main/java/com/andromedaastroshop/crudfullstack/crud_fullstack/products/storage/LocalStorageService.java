package com.andromedaastroshop.crudfullstack.crud_fullstack.products.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation = Paths.get("uploads/products");

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public String upload(MultipartFile file) {
        try {
            if (file.isEmpty()) throw new RuntimeException("File is empty");

            if (!file.getContentType().startsWith("image/")) {
                throw new RuntimeException("Only images allowed");
            }

            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), rootLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            return baseUrl + "/images/products/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String url) {
        try {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            Files.deleteIfExists(rootLocation.resolve(fileName));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
}
