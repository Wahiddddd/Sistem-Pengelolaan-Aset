package com.asset.manager.asset_management.service;

import com.asset.manager.asset_management.DTO.AssetSimpleResponse;
import com.asset.manager.asset_management.DTO.MaintenanceLogRequestDTO;
import com.asset.manager.asset_management.DTO.MaintenanceLogResponseDTO;
import com.asset.manager.asset_management.entity.*;
import com.asset.manager.asset_management.repository.AssetRepository;
import com.asset.manager.asset_management.repository.MaintenanceLogRepository;
import com.asset.manager.asset_management.repository.UserRepository;
import com.asset.manager.asset_management.exception.BusinessException;
import com.asset.manager.asset_management.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceLogService {
    private static final Logger log = LoggerFactory.getLogger(MaintenanceLogService.class);

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
    public void startMaintenance(Long assetId, String username) {

        log.info("Technician {} attempting to start maintenance for assetId={}", username, assetId);

        // Validasi Aset
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

        // Validasi Teknisi
        User technician = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with username: " + username));

        if (!technician.getRole().equals(UserRole.ROLE_TEKNISI) && !technician.getRole().equals(UserRole.ROLE_ADMIN)) {
            log.warn("User {} tried to start maintenance but has insufficient permissions", username);
            throw new BusinessException("User does not have permission to start maintenance");
        }

        if (asset.getStatus() != AssetStatus.NEEDS_MAINTENANCE) {
            log.warn("Asset {} is not available for maintenance. Current status={}", assetId, asset.getStatus());
            throw new BusinessException(
                    "Asset is not available for maintenance. Current status is " + asset.getStatus());
        }

        // Cek apakah asset sedang dikerjakan orang lain
        if (logRepository.findByAssetIdAndEndTimeIsNull(assetId).isPresent()) {
            log.warn("Asset {} already in maintenance by another technician", assetId);
            throw new BusinessException("Asset is already in maintenance");
        }

        // Cek teknisi masih punya tugas
        if (logRepository.findByTechnicianIdAndEndTimeIsNull(technician.getId()).isPresent()) {
            log.warn("Technician {} still has unfinished maintenance", username);
            throw new BusinessException("You still have an unfinished maintenance task");
        }

        // Update status asset
        asset.setStatus(AssetStatus.IN_MAINTENANCE);

        MaintenanceLog logEntity = new MaintenanceLog();
        logEntity.setAsset(asset);
        logEntity.setTechnician(technician);
        logEntity.setStartTime(LocalDateTime.now());

        logRepository.save(logEntity);
        assetRepository.save(asset);

        log.info("Maintenance started successfully. assetId={}, technician={}", assetId, username);
    }

    // SELESAI MAINTENANCE (NORMAL)
    @Transactional
    public MaintenanceLogResponseDTO finishMaintenance(Long assetId,
            MaintenanceLogRequestDTO dto) {
        log.info("Finishing maintenance for assetId: {}. Description: {}, Cost: {}", assetId, dto.getDescription(),
                dto.getCost());

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found"));

        MaintenanceLog log = logRepository
                .findByAssetIdAndEndTimeIsNull(assetId)
                .orElseThrow(() -> new RuntimeException("No active maintenance found"));

        // membuat validasi log, Update log
        log.setDescription(dto.getDescription());
        log.setCost(dto.getCost());
        log.setPhotoBefore(dto.getPhotoBefore());
        log.setPhotoAfter(dto.getPhotoAfter());
        log.setEndTime(LocalDateTime.now());

        // Update status asset menjadi ACTIVE
        asset.setStatus(AssetStatus.ACTIVE);

        // Menghitung ulang jadwal maintenance berikutnya dari tanggal SEKARANG
        if (asset.getMaintenanceFrequency() != null) {
            asset.setNextMaintenanceDate(
                    log.getEndTime().toLocalDate()
                            .plusMonths(asset.getMaintenanceFrequency()));
        }

        // save & return
        logRepository.save(log);
        assetRepository.save(asset);

        return mapToResponseDTO(log);
    }

    // Jika aset rusak
    @Transactional
    public void markAsBroken(Long assetId) {
        log.warn("Marking assetId: {} as BROKEN during maintenance", assetId);

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

    @Transactional(readOnly = true)
    public Page<AssetSimpleResponse> getAssetsForMaintenance(Pageable pageable) {

        return assetRepository
                .findByStatus(AssetStatus.NEEDS_MAINTENANCE, pageable)
                .map(this::mapToSimpleResponse);
    }

    @Transactional(readOnly = true)
    public Page<MaintenanceLogResponseDTO> getAllMaintenanceLogs(Pageable pageable) {

        return logRepository.findAllByOrderByStartTimeDesc(pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<MaintenanceLogResponseDTO> searchMaintenanceLogs(String serialNumber, Pageable pageable) {

        Page<MaintenanceLog> logs = logRepository.findByAssetSerialNumberContainingIgnoreCase(serialNumber, pageable);

        if (logs.isEmpty()) {
            log.warn("Maintenance logs not found for serial number: {}", serialNumber);
        } else {
            log.info("Found {} maintenance logs for serial number: {}", logs.getTotalElements(), serialNumber);
        }

        return logs.map(this::mapToResponseDTO);
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
        res.setStatusAsetSekarang(log.getAsset().getStatus() != null ? log.getAsset().getStatus().name() : null);

        return res;
    }

    private AssetSimpleResponse mapToSimpleResponse(Asset a) {
        AssetSimpleResponse res = new AssetSimpleResponse();
        res.setId(a.getId());
        res.setSerialNumber(a.getSerialNumber());
        res.setName(a.getName());
        res.setStatus(a.getStatus() != null ? a.getStatus().name() : null);

        if (a.getCategory() != null) {
            res.setCategoryName(a.getCategory().getName());
        }

        return res;
    }

}
