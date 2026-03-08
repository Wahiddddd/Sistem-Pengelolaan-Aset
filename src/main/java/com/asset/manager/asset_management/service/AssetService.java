package com.asset.manager.asset_management.service;

import com.asset.manager.asset_management.DTO.AssetRequestDTO;
import com.asset.manager.asset_management.DTO.AssetResponseDTO;
import com.asset.manager.asset_management.DTO.AssetSimpleResponse;
import com.asset.manager.asset_management.DTO.AssetUpdateRequestDTO;
import com.asset.manager.asset_management.entity.Asset;
import com.asset.manager.asset_management.entity.AssetStatus;
import com.asset.manager.asset_management.entity.Category;
import com.asset.manager.asset_management.repository.AssetRepository;
import com.asset.manager.asset_management.repository.CategoryRepository;
import com.asset.manager.asset_management.exception.BusinessException;
import com.asset.manager.asset_management.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetService {
    private static final Logger log = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;
    private final CategoryRepository categoryRepository;

    public AssetService(AssetRepository assetRepository, CategoryRepository categoryRepository) {
        this.assetRepository = assetRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional // jika terjadi error di tengah jalan, semua perubahan data akan dibatalkan
    public AssetResponseDTO createAsset(AssetRequestDTO dto) {
        log.info("Creating new asset with serial number: {} and name: {}", dto.getSerialNumber(), dto.getName());
        // 1. Validasi Kategori
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        // Cek apakah serial number sudah ada sebelumnya
        if (assetRepository.findBySerialNumber(dto.getSerialNumber()).isPresent()) {
            throw new BusinessException("Serial number already exists: " + dto.getSerialNumber());
        }

        // 2. Inisialisasi Objek
        Asset asset = new Asset();

        // 3. Mapping DTO -> Entity
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setName(dto.getName());
        asset.setImagePath(dto.getImagePath());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setMaintenanceFrequency(dto.getMaintenanceFrequency());
        asset.setCategory(category);

        // 4. Logika Otomatis: Set jadwal maintenance pertama
        if (dto.getPurchaseDate() != null && dto.getMaintenanceFrequency() != null) {
            asset.setNextMaintenanceDate(dto.getPurchaseDate().plusMonths(dto.getMaintenanceFrequency()));
        }

        asset.setStatus(AssetStatus.ACTIVE); // Default aset baru adalah Active
        asset.setDeleted(false);

        // 5. Simpan ke Database
        Asset savedAsset = assetRepository.save(asset);

        // 6. Kembalikan ResponseDTO
        return mapToResponseDTO(savedAsset);
    }

    @Transactional
    public AssetResponseDTO updateAsset(Long id, AssetUpdateRequestDTO dto) {
        log.info("Updating asset with ID: {}. New name: {}", id, dto.getName());

        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        asset.setName(dto.getName());
        asset.setImagePath(dto.getImagePath());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setMaintenanceFrequency(dto.getMaintenanceFrequency());
        asset.setCategory(category);

        return mapToResponseDTO(assetRepository.save(asset));
    }

    @Transactional(readOnly = true)
    public List<AssetSimpleResponse> getDueMaintenanceAssets() {
        // Menampilkan aset yang sudah jatuh tempo hari ini atau sebelumnya
        return assetRepository.findByNextMaintenanceDateBeforeAndStatus(LocalDate.now(), AssetStatus.ACTIVE)
                .stream()
                .map(this::mapToSimpleResponse)
                .collect(Collectors.toList());
    }

    // menampilkan asset yang sudah masuk ke Needs maintenance
    public Page<AssetSimpleResponse> getAvailableMaintenance(Pageable pageable) {
        return assetRepository
                .findByStatus(AssetStatus.NEEDS_MAINTENANCE, pageable)
                .map(this::mapToSimpleResponse);
    }

    @Transactional
    public AssetResponseDTO updateImagePath(Long id, String imagePath) {
        log.info("Updating image path for asset ID: {}", id);
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
        asset.setImagePath(imagePath);
        return mapToResponseDTO(assetRepository.save(asset));
    }

    @Transactional(readOnly = true)
    public AssetResponseDTO getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        return mapToResponseDTO(asset);
    }

    @Transactional
    public void deleteAsset(Long id) {
        log.info("Attempting to soft-delete asset with ID: {}", id);
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        asset.setDeleted(true);
        assetRepository.save(asset);
    }

    @Transactional(readOnly = true)
    public Page<AssetSimpleResponse> getAllAssets(Pageable pageable) {

        return assetRepository.findAll(pageable)
                .map(this::mapToSimpleResponse);
    }

    // logic untuk fitur search asset oleh admin
    @Transactional(readOnly = true)
    public List<AssetSimpleResponse> searchAssets(String keyword) {

        log.info("Admin searching asset with keyword: {}", keyword);

        List<Asset> assets = assetRepository
                .findByNameContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(keyword, keyword);

        if (assets.isEmpty()) {
            log.warn("No asset found for keyword: {}", keyword);
        } else {
            log.info("Found {} asset(s) for keyword: {}", assets.size(), keyword);
        }

        return assets.stream()
                .map(this::mapToSimpleResponse)
                .collect(Collectors.toList());
    }

    // Helper untuk list/pagination agar ringan
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

    @Scheduled(cron = "0 0 0 * * ?") // jadi nanti sistem bekerja sendiri untuk memantau aset tanpa campur tangan
                                     // manusia.
    @Transactional
    public void updateMaintenanceStatus() {
        log.info("Scheduled task: Checking for assets due for maintenance...");
        // Mencari yang jatuh tempo hari ini atau sebelumnya (Date < Tommorow)
        List<Asset> assets = assetRepository
                .findByNextMaintenanceDateBeforeAndStatus(LocalDate.now().plusDays(1), AssetStatus.ACTIVE);

        // Mengubah status menjadi NEEDS_MAINTENANCE secara otomatis
        for (Asset asset : assets) {
            asset.setStatus(AssetStatus.NEEDS_MAINTENANCE);
        }
        // menambahkan saveAll jika repository tidak auto-flush
        assetRepository.saveAll(assets);
    }

    @Transactional
    public void forceNeedsMaintenance(Long id) {
        log.info("Manually forcing asset ID {} to NEEDS_MAINTENANCE status", id);
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));
        asset.setStatus(AssetStatus.NEEDS_MAINTENANCE);
        assetRepository.save(asset);
    }

    // Helper method untuk Mapping Entity -> DTO
    private AssetResponseDTO mapToResponseDTO(Asset a) {
        AssetResponseDTO res = new AssetResponseDTO();
        res.setId(a.getId());
        res.setSerialNumber(a.getSerialNumber());
        res.setName(a.getName());
        res.setImagePath(a.getImagePath());

        if (a.getStatus() != null) {
            res.setStatus(a.getStatus().name());
        }

        res.setNextMaintenanceDate(a.getNextMaintenanceDate());

        if (a.getCategory() != null) {
            res.setCategoryName(a.getCategory().getName());
        }
        return res;
    }

}
