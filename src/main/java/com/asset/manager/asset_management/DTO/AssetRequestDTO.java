package com.asset.manager.asset_management.DTO;

import com.asset.manager.asset_management.entity.AssetStatus;
import com.asset.manager.asset_management.entity.Category;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssetRequestDTO {
    private String serialNumber;
    private String name;
    private String imagePath;
    private LocalDate purchaseDate;
    private Integer maintenanceFrequency; // dalam bulan
    private Long categoryId; // Cukup kirim ID-nya saja
}
