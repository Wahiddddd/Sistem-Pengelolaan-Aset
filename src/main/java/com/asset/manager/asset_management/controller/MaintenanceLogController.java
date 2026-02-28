package com.asset.manager.asset_management.controller;

import com.asset.manager.asset_management.DTO.MaintenanceLogRequestDTO;
import com.asset.manager.asset_management.DTO.MaintenanceLogResponseDTO;
import com.asset.manager.asset_management.service.MaintenanceLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceLogController {

    private final MaintenanceLogService maintenanceLogService;

    public MaintenanceLogController(MaintenanceLogService maintenanceLogService) {
        this.maintenanceLogService = maintenanceLogService;
    }

    @PostMapping("/start/{assetId}/technician/{technicianId}")
    public ResponseEntity<String> startMaintenance(
            @PathVariable Long assetId,
            @PathVariable Long technicianId) {
        maintenanceLogService.startMaintenance(assetId, technicianId);
        return ResponseEntity.ok("Maintenance started successfully.");
    }

    @PostMapping("/finish/{assetId}")
    public ResponseEntity<MaintenanceLogResponseDTO> finishMaintenance(
            @PathVariable Long assetId,
            @Valid @RequestBody MaintenanceLogRequestDTO dto) {
        MaintenanceLogResponseDTO response = maintenanceLogService.finishMaintenance(assetId, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/broken/{assetId}")
    public ResponseEntity<String> markAsBroken(@PathVariable Long assetId) {
        maintenanceLogService.markAsBroken(assetId);
        return ResponseEntity.ok("Asset marked as broken.");
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<MaintenanceLogResponseDTO>> getLogsByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(maintenanceLogService.getLogsByAsset(assetId));
    }
}
