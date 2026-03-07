package com.asset.manager.asset_management.repository;

import com.asset.manager.asset_management.entity.Asset;
import com.asset.manager.asset_management.entity.AssetStatus;
import com.asset.manager.asset_management.entity.MaintenanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Long> {
    // Untuk melihat riwayat servis berdasarkan ID aset tertentu
    List<MaintenanceLog> findByAssetIdOrderByStartTimeDesc(Long assetId);

    // Untuk cek apakah asset sedang dikerjakan
    Optional<MaintenanceLog> findByAssetIdAndEndTimeIsNull(Long assetId);

    // Untuk cek apakah teknisi sedang punya pekerjaan aktif
    Optional<MaintenanceLog> findByTechnicianIdAndEndTimeIsNull(Long technicianId);

    // pagination
    Page<MaintenanceLog> findAllByOrderByStartTimeDesc(Pageable pageable);

    // untuk search berdasarkan serial number
    Page<MaintenanceLog> findByAssetSerialNumberContainingIgnoreCase(String serialNumber, Pageable pageable);

}
