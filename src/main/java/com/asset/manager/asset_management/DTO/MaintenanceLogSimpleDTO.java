package com.asset.manager.asset_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//untuk ditampilkan pagination
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceLogSimpleDTO {
    private Long id; // Untuk navigasi ke detail
    private String assetName; // Nama aset yang diservis
    private LocalDateTime startTime;
    private BigDecimal cost;
    private String technicianName; // Siapa yang mengerjakan
}
