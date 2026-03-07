package com.asset.manager.asset_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetUpdateRequestDTO {
    private String name;
    private String imagePath;
    private LocalDate purchaseDate;
    private Integer maintenanceFrequency; // dalam bulan
    private Long categoryId; // Cukup kirim ID-nya saja
}
