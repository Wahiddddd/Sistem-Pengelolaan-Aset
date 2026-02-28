package com.asset.manager.asset_management.service;

import com.asset.manager.asset_management.DTO.CategoryRequestDTO;
import com.asset.manager.asset_management.DTO.CategoryResponseDTO;
import com.asset.manager.asset_management.entity.Category;
import com.asset.manager.asset_management.repository.AssetRepository;
import com.asset.manager.asset_management.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AssetRepository assetRepository; // untuk validasi relasi

    public CategoryService(CategoryRepository categoryRepository,
                           AssetRepository assetRepository) {
        this.categoryRepository = categoryRepository;
        this.assetRepository = assetRepository;
    }

    // CREATE CATEGORY
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {

        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Category already exists");
        }

        Category category = new Category();
        category.setName(dto.getName());

        Category saved = categoryRepository.save(category);

        return mapToResponseDTO(saved);
    }

    // GET ALL CATEGORY
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    //  GET CATEGORY BY ID
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return mapToResponseDTO(category);
    }

    // UPDATE CATEGORY
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Cek duplicate kecuali dirinya sendiri
        Optional<Category> existing = categoryRepository.findByName(dto.getName());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Category name already used");
        }

        category.setName(dto.getName());

        return mapToResponseDTO(categoryRepository.save(category));
    }

    // DELETE CATEGORY (AMAN)
    @Transactional
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Cek apakah masih dipakai Asset
        boolean isUsed = assetRepository.existsByCategoryId(id);

        if (isUsed) {
            throw new RuntimeException("Category cannot be deleted. It is used by assets.");
        }

        categoryRepository.delete(category);
    }

    // MAPPER
    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return new CategoryResponseDTO(category.getName());
    }
}
