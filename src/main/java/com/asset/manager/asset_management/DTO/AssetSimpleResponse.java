package com.asset.manager.asset_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//untuk menampilkan aset pada menu pagination nya
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetSimpleResponse {
    private Long id; // agar saat diklik, sistem tahu ID mana yang mau dibuka detailnya
    private String serialNumber;
    private String name;
    private String status; // Agar admin tahu mana yang aktif/rusak tanpa buka detail
    private String categoryName; // Agar tahu ini Laptop, Printer, atau Meja
    // imagePath bisa tetap ada jika ingin menampilkan thumbnail kecil
}
