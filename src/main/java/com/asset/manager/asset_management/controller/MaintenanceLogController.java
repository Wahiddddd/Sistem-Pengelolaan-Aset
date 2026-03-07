package com.asset.manager.asset_management.controller;

import com.asset.manager.asset_management.DTO.AssetSimpleResponse;
import com.asset.manager.asset_management.DTO.MaintenanceLogRequestDTO;
import com.asset.manager.asset_management.DTO.MaintenanceLogResponseDTO;
import com.asset.manager.asset_management.service.MaintenanceLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceLogController {

    private final MaintenanceLogService maintenanceLogService;
    private final com.asset.manager.asset_management.service.AssetService assetService;

    public MaintenanceLogController(MaintenanceLogService maintenanceLogService,
            com.asset.manager.asset_management.service.AssetService assetService) {
        this.maintenanceLogService = maintenanceLogService;
        this.assetService = assetService;
    }

    // Endpoint khusus untuk Simulasi/Testing (Agar status jadi NEEDS_MAINTENANCE)
    @PostMapping("/force-test/{assetId}")
    public ResponseEntity<Void> forceNeedsMaintenance(@PathVariable Long assetId) {
        assetService.forceNeedsMaintenance(assetId);
        return ResponseEntity.ok().build();
    }

    // Memulai Proses Maintenance (Sesuai Flowchart Teknisi)
    @PostMapping("/start/{assetId}")
    public ResponseEntity<String> startMaintenance(
            @PathVariable Long assetId,
            Authentication authentication) {

        String username = authentication.getName();

        maintenanceLogService.startMaintenance(assetId, username);

        return ResponseEntity.ok("Maintenance started successfully.");
    }

    // Menyelesaikan Maintenance
    @PostMapping("/finish/{assetId}")
    public ResponseEntity<MaintenanceLogResponseDTO> finishMaintenance(
            @PathVariable Long assetId,
            @Valid @RequestBody MaintenanceLogRequestDTO dto) {
        // Teknisi mengisi form: deskripsi perbaikan, biaya, dan upload foto bukti.
        // Sistem otomatis menghitung jadwal servis berikutnya dan mengembalikan status
        // aset ke 'ACTIVE'.
        MaintenanceLogResponseDTO response = maintenanceLogService.finishMaintenance(assetId, dto);
        return ResponseEntity.ok(response);
    }

    // Melaporkan Aset Rusak Total
    @PostMapping("/broken/{assetId}")
    public ResponseEntity<String> markAsBroken(@PathVariable Long assetId) {
        // Digunakan jika saat pengecekan fisik, teknisi menemukan aset rusak parah.
        // Status aset akan diubah menjadi 'BROKEN' dan tidak akan masuk jadwal servis
        // lagi.
        maintenanceLogService.markAsBroken(assetId);
        return ResponseEntity.ok("Asset marked as broken.");
    }

    // Riwayat Maintenance per Aset
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<MaintenanceLogResponseDTO>> getLogsByAsset(@PathVariable Long assetId) {
        // Menampilkan histori lengkap: siapa teknisinya, kapan diperbaiki, dan berapa
        // biayanya.
        // bagi Admin untuk memantau "kesehatan" jangka panjang suatu aset.
        return ResponseEntity.ok(maintenanceLogService.getLogsByAsset(assetId));
    }

    // Get all history
    @GetMapping("/maintenance-logs")
    public Page<MaintenanceLogResponseDTO> getAllLogs(Pageable pageable) {
        return maintenanceLogService.getAllMaintenanceLogs(pageable);
    }

    // search by serial number
    @GetMapping("/maintenance-logs/search")
    public Page<MaintenanceLogResponseDTO> searchLogs(
            @RequestParam String serialNumber,
            Pageable pageable) {

        return maintenanceLogService.searchMaintenanceLogs(serialNumber, pageable);
    }

    @GetMapping("/assets-for-maintenance")
    public Page<AssetSimpleResponse> getAssetsForMaintenance(Pageable pageable) {
        return maintenanceLogService.getAssetsForMaintenance(pageable);
    }

}
