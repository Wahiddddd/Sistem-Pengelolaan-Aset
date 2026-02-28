package com.asset.manager.asset_management.repository;

import com.asset.manager.asset_management.entity.MaintenanceLog;
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
}
