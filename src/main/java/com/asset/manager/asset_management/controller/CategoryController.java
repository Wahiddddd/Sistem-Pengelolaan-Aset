package com.asset.manager.asset_management.controller;

import com.asset.manager.asset_management.DTO.CategoryRequestDTO;
import com.asset.manager.asset_management.DTO.CategoryResponseDTO;
import com.asset.manager.asset_management.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Menambahkan Kategori Baru
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.createCategory(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Mengambil Semua Daftar Kategori
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // Mengambil Detail Kategori berdasarkan ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        // Digunakan untuk melihat detail spesifik satu kategori
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // Mengubah Nama Kategori
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO dto) {
        // Memperbarui informasi kategori yang sudah ada
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        // Menghapus kategori dari sistem
        // Di Service sudah ada proteksi agar kategori yang masih memiliki aset tidak
        // bisa dihapus
        categoryService.deleteCategory(id);
        // Mengembalikan status 204 No Content sebagai tanda berhasil
        return ResponseEntity.noContent().build();
    }
}
