package com.asset.manager.asset_management.controller;

import com.asset.manager.asset_management.DTO.AssetRequestDTO;
import com.asset.manager.asset_management.DTO.AssetResponseDTO;
import com.asset.manager.asset_management.DTO.AssetSimpleResponse;
import com.asset.manager.asset_management.DTO.AssetUpdateRequestDTO;
import com.asset.manager.asset_management.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    // Menambahkan Aset Baru
    @PostMapping
    public ResponseEntity<AssetResponseDTO> createAsset(@Valid @RequestBody AssetRequestDTO dto) {
        AssetResponseDTO response = assetService.createAsset(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Mengambil Semua Daftar Aset (Dengan Pagination)
    @GetMapping
    public ResponseEntity<Page<AssetSimpleResponse>> getAllAssets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return ResponseEntity.ok(assetService.getAllAssets(pageable));
    }

    // Mengambil Detail Satu Aset berdasarkan ID
    @GetMapping("/{id}")
    public ResponseEntity<AssetResponseDTO> getAssetById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    // Mengambil Daftar Aset yang Mendekati/Sudah Jatuh Tempo Maintenance
    @GetMapping("/due-maintenance")
    public ResponseEntity<List<AssetSimpleResponse>> getDueMaintenanceAssets() {
        return ResponseEntity.ok(assetService.getDueMaintenanceAssets());
    }

    // Mengubah/Update Data Aset
    @PutMapping("/{id}")
    public ResponseEntity<AssetResponseDTO> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetUpdateRequestDTO dto) {

        return ResponseEntity.ok(assetService.updateAsset(id, dto));
    }

    // Menghapus Aset (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AssetSimpleResponse>> searchAssets(
            @RequestParam String keyword) {
        return ResponseEntity.ok(assetService.searchAssets(keyword));
    }
}
