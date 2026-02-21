package com.asset.manager.asset_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "category", "logs" })
@SQLDelete(sql = "UPDATE assets SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String serialNumber;

    private String name;
    private String imagePath;
    private LocalDate purchaseDate;
    private Integer maintenanceFrequency;

    @Enumerated(EnumType.STRING)
    private com.asset.manager.asset_management.entity.AssetStatus status;

    private LocalDate nextMaintenanceDate;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL)
    private List<MaintenanceLog> logs;
}
