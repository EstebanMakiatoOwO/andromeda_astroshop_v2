package com.andromedaastroshop.crudfullstack.crud_fullstack.products.storage;

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

    @Override
    public String upload(MultipartFile file) {
        try {

            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            // validar tipo
            if (!file.getContentType().startsWith("image/")) {
                throw new RuntimeException("Only images allowed");
            }

            // crear carpeta si no existe
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // nombre único
            String fileName = UUID.randomUUID()
                    + "_" + file.getOriginalFilename();

            Path destination = rootLocation.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // URL pública
            return "/images/products/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            Path filePath = rootLocation.resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
}
