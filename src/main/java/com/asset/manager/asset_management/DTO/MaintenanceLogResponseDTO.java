package com.asset.manager.asset_management.DTO;

import com.asset.manager.asset_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceLogResponseDTO {
    private Long id;
    private String assetName; // Agar Admin tahu aset apa yang diservis
    private String technicianName; // Nama teknisi yang mengerjakan
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private BigDecimal cost;
    private String photoAfter;

    private String statusAsetSekarang;
}
