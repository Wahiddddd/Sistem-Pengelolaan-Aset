package com.asset.manager.asset_management.repository;

import com.asset.manager.asset_management.entity.Asset;
import com.asset.manager.asset_management.entity.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    // Untuk cek duplicate serial number saat input aset (Flowchart Admin)
    Optional<Asset> findBySerialNumber(String serialNumber);

    // Untuk fitur pencarian di dashboard
    List<Asset> findByNameContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(String name, String serialNumber);

    // agar menerima Enum
    List<Asset> findByNextMaintenanceDateBeforeAndStatus(LocalDate date, AssetStatus status);

    boolean existsByCategoryId(Long categoryId);

    Page<Asset> findByStatus(AssetStatus status, Pageable pageable);
}
