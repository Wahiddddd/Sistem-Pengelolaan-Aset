package com.asset.manager.asset_management.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaintenanceLogRequestDTO {
    private Long assetId; // Wajib ada untuk relasi
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private BigDecimal cost;
    private String photoBefore;
    private String photoAfter;
    // user_id biasanya diambil dari Session/Token Login, bukan dari Request body
}
