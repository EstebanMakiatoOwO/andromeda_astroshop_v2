package com.andromedaastroshop.crudfullstack.crud_fullstack.products.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(MultipartFile file);

    void delete(String fileName);
}
