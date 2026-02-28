package com.asset.manager.asset_management.DTO;

import com.asset.manager.asset_management.entity.AssetStatus;
import com.asset.manager.asset_management.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
