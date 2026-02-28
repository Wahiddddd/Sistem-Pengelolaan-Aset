package com.asset.manager.asset_management.repository;

import com.asset.manager.asset_management.entity.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Long> {
    // Untuk melihat riwayat servis berdasarkan ID aset tertentu
    List<MaintenanceLog> findByAssetIdOrderByStartTimeDesc(Long assetId);
}
