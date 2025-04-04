package com.repository;

import com.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    public Optional<Category> findByCategoryId(Integer categoryId);

    public Optional<Category> findByCategoryNameIgnoreCase(String categoryName);
}
