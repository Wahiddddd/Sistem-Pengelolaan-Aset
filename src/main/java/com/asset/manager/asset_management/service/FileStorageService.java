package com.asset.manager.asset_management.service;

import com.asset.manager.asset_management.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        // Lokasi penyimpanan di dalam folder resources/static/uploads
        this.fileStorageLocation = Paths.get("src/main/resources/static/uploads")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new BusinessException("Could not create the directory where the uploaded files will be stored.");
        }
    }

    public String storeFile(MultipartFile file) {
        // Ambil nama file asli dan bersihkan path-nya
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Cek jika nama file mengandung karakter ilegal
            if (originalFileName.contains("..")) {
                throw new BusinessException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }

            // Berikan nama unik menggunakan UUID untuk menghindari konflik nama file
            String extension = "";
            int i = originalFileName.lastIndexOf('.');
            if (i > 0) {
                extension = originalFileName.substring(i);
            }
            String fileName = UUID.randomUUID().toString() + extension;

            // Copy file ke lokasi target (overwrite jika ada file dengan nama yang sama)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;
        } catch (IOException ex) {
            throw new BusinessException("Could not store file " + originalFileName + ". Please try again!");
        }
    }
}
