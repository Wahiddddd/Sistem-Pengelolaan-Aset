package com.asset.manager.asset_management.service;

import com.asset.manager.asset_management.DTO.MaintenanceLogRequestDTO;
import com.asset.manager.asset_management.DTO.MaintenanceLogResponseDTO;
import com.asset.manager.asset_management.entity.*;
import com.asset.manager.asset_management.repository.AssetRepository;
import com.asset.manager.asset_management.repository.MaintenanceLogRepository;
import com.asset.manager.asset_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceLogService {
    private final MaintenanceLogRepository logRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public MaintenanceLogService(MaintenanceLogRepository logRepository, AssetRepository assetRepository,
            UserRepository userRepository) {
        this.logRepository = logRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void startMaintenance(Long assetId, Long technicianId) {

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        // Validasi role
        if (!technician.getRole().equals(UserRole.ROLE_TEKNISI)) {
            throw new RuntimeException("User is not a technician");
        }

        // Cek apakah asset memang butuh maintenance
        if (asset.getStatus() != AssetStatus.NEEDS_MAINTENANCE) {
            throw new RuntimeException("Asset is not available for maintenance");
        }

        // Cek apakah asset sedang dikerjakan orang lain
        if (logRepository.findByAssetIdAndEndTimeIsNull(assetId).isPresent()) {
            throw new RuntimeException("Asset already in maintenance");
        }

        // Cek teknisi tidak boleh ambil 2 tugas
        if (logRepository.findByTechnicianIdAndEndTimeIsNull(technicianId).isPresent()) {
            throw new RuntimeException("Technician still has unfinished maintenance");
        }

        // Update status asset
        asset.setStatus(AssetStatus.IN_MAINTENANCE);

        // Buat log baru
        MaintenanceLog log = new MaintenanceLog();
        log.setAsset(asset);
        log.setTechnician(technician);
        log.setStartTime(LocalDateTime.now());

        logRepository.save(log);
        assetRepository.save(asset);
    }

    // SELESAI MAINTENANCE (NORMAL)
    @Transactional
    public MaintenanceLogResponseDTO finishMaintenance(Long assetId,
            MaintenanceLogRequestDTO dto) {

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        MaintenanceLog log = logRepository
                .findByAssetIdAndEndTimeIsNull(assetId)
                .orElseThrow(() -> new RuntimeException("No active maintenance found"));

        // Update log
        log.setDescription(dto.getDescription());
        log.setCost(dto.getCost());
        log.setPhotoBefore(dto.getPhotoBefore());
        log.setPhotoAfter(dto.getPhotoAfter());
        log.setEndTime(LocalDateTime.now());

        // Update status asset menjadi ACTIVE
        asset.setStatus(AssetStatus.ACTIVE);

        // Hitung next maintenance dari tanggal selesai
        if (asset.getMaintenanceFrequency() != null) {
            asset.setNextMaintenanceDate(
                    log.getEndTime().toLocalDate()
                            .plusMonths(asset.getMaintenanceFrequency()));
        }

        logRepository.save(log);
        assetRepository.save(asset);

        return mapToResponseDTO(log);
    }

    // Jika aset rusak
    @Transactional
    public void markAsBroken(Long assetId) {

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        MaintenanceLog log = logRepository
                .findByAssetIdAndEndTimeIsNull(assetId)
                .orElseThrow(() -> new RuntimeException("No active maintenance found"));

        asset.setStatus(AssetStatus.BROKEN);
        log.setDescription("Asset is broken beyond repair");
        log.setCost(BigDecimal.ZERO);
        log.setEndTime(LocalDateTime.now());

        logRepository.save(log);
        assetRepository.save(asset);
    }

    // HISTORY BY ASSET
    @Transactional(readOnly = true)
    public List<MaintenanceLogResponseDTO> getLogsByAsset(Long assetId) {
        return logRepository.findByAssetIdOrderByStartTimeDesc(assetId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Helper method untuk Mapping Entity -> DTO
    private MaintenanceLogResponseDTO mapToResponseDTO(MaintenanceLog log) {

        MaintenanceLogResponseDTO res = new MaintenanceLogResponseDTO();
        res.setId(log.getId());
        res.setAssetName(log.getAsset().getName());
        res.setTechnicianName(log.getTechnician().getUsername());
        res.setStartTime(log.getStartTime());
        res.setEndTime(log.getEndTime());
        res.setDescription(log.getDescription());
        res.setCost(log.getCost());
        res.setPhotoAfter(log.getPhotoAfter());
        res.setStatusAsetSekarang(log.getAsset().getStatus().name());

        return res;
    }

}
