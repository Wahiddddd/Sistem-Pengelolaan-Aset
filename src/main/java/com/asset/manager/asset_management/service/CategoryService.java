package com.asset.manager.asset_management.service;

import com.asset.manager.asset_management.DTO.CategoryRequestDTO;
import com.asset.manager.asset_management.DTO.CategoryResponseDTO;
import com.asset.manager.asset_management.entity.Category;
import com.asset.manager.asset_management.repository.AssetRepository;
import com.asset.manager.asset_management.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.asset.manager.asset_management.exception.BusinessException;
import com.asset.manager.asset_management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

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
        log.info("Creating new category with name: {}", dto.getName());

        if (categoryRepository.existsByName(dto.getName())) {
            throw new BusinessException("Category '" + dto.getName() + "' already exists");
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

    // GET CATEGORY BY ID
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        return mapToResponseDTO(category);
    }

    // UPDATE CATEGORY
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        log.info("Updating category with ID: {}. New name: {}", id, dto.getName());

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Cek duplicate kecuali dirinya sendiri
        Optional<Category> existing = categoryRepository.findByName(dto.getName());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new BusinessException("Category name '" + dto.getName() + "' is already used by another category");
        }

        category.setName(dto.getName());

        return mapToResponseDTO(categoryRepository.save(category));
    }

    // DELETE CATEGORY
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Attempting to delete category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Integritas Data: Cek apakah masih ada aset yang terhubung ke kategori ini
        boolean isUsed = assetRepository.existsByCategoryId(id);

        if (isUsed) {
            throw new BusinessException("Category cannot be deleted. It is currently linked to one or more assets.");
        }

        categoryRepository.delete(category);
    }

    // MAPPER
    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
