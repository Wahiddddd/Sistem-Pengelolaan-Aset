package com.asset.manager.asset_management.repository;

import com.asset.manager.asset_management.entity.Asset;
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
    List<Asset> findByNameContainingIgnoreCase(String name);

    // Menampilkan aset yang tanggal maintenance-nya hari ini atau sudah lewat (backlog)
    List<Asset> findByNextMaintenanceDateBeforeAndStatus(LocalDate date, String status);
}
