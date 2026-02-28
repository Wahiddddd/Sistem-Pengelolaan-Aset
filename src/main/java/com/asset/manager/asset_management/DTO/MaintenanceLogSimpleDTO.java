package com.asset.manager.asset_management.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//untuk ditampilkan pagination
@Data
public class MaintenanceLogSimpleDTO {
    private Long id; // Untuk navigasi ke detail
    private String assetName; // Tambahan: Nama aset yang diservis
    private LocalDateTime startTime;
    private BigDecimal cost;
    private String technicianName; // Tambahan: Siapa yang mengerjakan
}
