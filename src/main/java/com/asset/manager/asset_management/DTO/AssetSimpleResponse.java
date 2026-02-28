package com.asset.manager.asset_management.DTO;

import lombok.Data;

//untuk menampilkan aset pada menu pagination nya
@Data
public class AssetSimpleResponse {
    private Long id; // Wajib ada agar saat diklik, sistem tahu ID mana yang mau dibuka detailnya
    private String serialNumber;
    private String name;
    private String status; // Tambahan: Agar admin tahu mana yang aktif/rusak tanpa buka detail
    private String categoryName; // Tambahan: Agar tahu ini Laptop, Printer, atau Meja
    // imagePath bisa tetap ada jika ingin menampilkan thumbnail kecil
}
