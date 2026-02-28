package com.asset.manager.asset_management.service;

import com.asset.manager.asset_management.DTO.AssetRequestDTO;
import com.asset.manager.asset_management.DTO.AssetResponseDTO;
import com.asset.manager.asset_management.DTO.AssetSimpleResponse;
import com.asset.manager.asset_management.entity.Asset;
import com.asset.manager.asset_management.entity.AssetStatus;
import com.asset.manager.asset_management.entity.Category;
import com.asset.manager.asset_management.repository.AssetRepository;
import com.asset.manager.asset_management.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetService {
    private final AssetRepository assetRepository;
    private final CategoryRepository categoryRepository;

    public AssetService(AssetRepository assetRepository, CategoryRepository categoryRepository) {
        this.assetRepository = assetRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public AssetResponseDTO createAsset(AssetRequestDTO dto) {
        // 1. Validasi Kategori
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Cek apakah serial number sudah ada sebelumnya
        if (assetRepository.findBySerialNumber(dto.getSerialNumber()).isPresent()) {
            throw new RuntimeException("Serial number already exists");
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

        asset.setStatus(AssetStatus.ACTIVE);
        asset.setDeleted(false);

        // 5. Simpan ke Database
        Asset savedAsset = assetRepository.save(asset);

        // 6. Kembalikan ResponseDTO
        return mapToResponseDTO(savedAsset);
    }

    @Transactional
    public AssetResponseDTO updateAsset(Long id, AssetRequestDTO dto) {

        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        // Cek duplicate serial number (kecuali milik sendiri)
        Optional<Asset> existing = assetRepository.findBySerialNumber(dto.getSerialNumber());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Serial number already used by another asset");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        asset.setSerialNumber(dto.getSerialNumber());
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

    @Transactional(readOnly = true)
    public AssetResponseDTO getAssetById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        return mapToResponseDTO(asset);
    }

    @Transactional
    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        assetRepository.delete(asset);
    }

    @Transactional(readOnly = true)
    public Page<AssetSimpleResponse> getAllAssets(Pageable pageable) {

        return assetRepository.findAll(pageable)
                .map(this::mapToSimpleResponse);
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

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateMaintenanceStatus() {
        List<Asset> assets = assetRepository
                .findByNextMaintenanceDateBeforeAndStatus(LocalDate.now(), AssetStatus.ACTIVE);

        for (Asset asset : assets) {
            asset.setStatus(AssetStatus.NEEDS_MAINTENANCE);
        }
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
