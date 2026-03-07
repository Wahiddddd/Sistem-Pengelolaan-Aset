package com.asset.manager.asset_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetRequestDTO {
    private String serialNumber;
    private String name;
    private String imagePath;
    private LocalDate purchaseDate;
    private Integer maintenanceFrequency; // dalam bulan
    private Long categoryId; // Cukup kirim ID-nya saja
}
