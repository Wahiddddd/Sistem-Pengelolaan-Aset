package com.asset.manager.asset_management.DTO;

import com.asset.manager.asset_management.entity.AssetStatus;
import com.asset.manager.asset_management.entity.Category;
import lombok.Data;

import java.time.LocalDate;

@Data
//untuk detail Asset
public class AssetResponseDTO {
    private Long id; // Penting untuk tracking
    private String serialNumber;
    private String name;
    private String imagePath;
    private String status;
    private String categoryName; // Lebih praktis untuk tampilan
    private LocalDate nextMaintenanceDate;
}
