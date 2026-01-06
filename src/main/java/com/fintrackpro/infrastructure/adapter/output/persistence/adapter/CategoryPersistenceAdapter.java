package com.fintrackpro.infrastructure.adapter.output.persistence.adapter;

import com.fintrackpro.domain.model.Category;
import com.fintrackpro.domain.port.output.CategoryRepositoryPort;
import com.fintrackpro.domain.valueobject.CategoryType;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.repository.JpaCategoryRepository;
import com.fintrackpro.infrastructure.helper.EntityReferenceHelper;
import com.fintrackpro.infrastructure.mapper.CategoryPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final JpaCategoryRepository jpaCategoryRepository;
    private final EntityReferenceHelper entityReferenceHelper;
    private final CategoryPersistenceMapper mapper;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        CategoryEntity savedEntity = jpaCategoryRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaCategoryRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Category> findByUserId(Long userId) {
        return jpaCategoryRepository.findByUser(entityReferenceHelper.getUserReference(userId))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Category> findSystemCategories() {
        return jpaCategoryRepository.findByIsSystemTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Category> findByUserIdAndType(Long userId, CategoryType type) {
        // Get system categories of this type
        List<CategoryEntity> systemCategories = jpaCategoryRepository.findByIsSystemTrueAndType(type);

        // Get user's custom categories of this type
        List<CategoryEntity> userCategories = jpaCategoryRepository.findByUserAndType(
                entityReferenceHelper.getUserReference(userId),
                type);

        // Combine both lists
        return Stream.concat(systemCategories.stream(), userCategories.stream())
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Category> findAllByUserId(Long userId) {
        // Get all system categories
        List<CategoryEntity> systemCategories = jpaCategoryRepository.findByIsSystemTrue();

        // Get user's custom categories
        List<CategoryEntity> userCategories = jpaCategoryRepository.findByUser(
                entityReferenceHelper.getUserReference(userId));

        // Combine both lists
        return Stream.concat(systemCategories.stream(), userCategories.stream())
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndName(Long userId, String name) {
        return jpaCategoryRepository.existsByUserAndName(
                entityReferenceHelper.getUserReference(userId),
                name);
    }

    @Override
    public void deleteById(Long id) {
        jpaCategoryRepository.deleteById(id);
    }

    @Override
    public long countByUserId(Long userId) {
        return jpaCategoryRepository.countByUserAndIsSystemFalse(
                entityReferenceHelper.getUserReference(userId));
    }
}
