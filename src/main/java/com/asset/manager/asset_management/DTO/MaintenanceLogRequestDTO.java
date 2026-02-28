package com.asset.manager.asset_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceLogRequestDTO {
    private Long assetId; // Wajib ada untuk relasi
    private String description;
    private BigDecimal cost;
    private String photoBefore;
    private String photoAfter;
    // user_id biasanya diambil dari Session/Token Login, bukan dari Request body
}
