package com.service;

import com.entity.Category;
import com.entity.Product;
import com.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PlaceholderConfigurerSupport placeholderConfigurerSupport;


    public boolean addCategory(Category category) {
        if(categoryRepository.findByCategoryNameIgnoreCase(category.getCategoryName()).isPresent()) {
            return false;
        }
        else {
            categoryRepository.save(category);
            return true;
        }
    }

    public boolean deleteCategory(Integer categoryId) {
        if(categoryRepository.findById(categoryId).isPresent()) {
            categoryRepository.deleteById(categoryId);
            return true;
        }
        return false;
    }

    public boolean updateCategory(Category category) {
        Optional<Category> categoryOptional = categoryRepository.findByCategoryId(category.getCategoryId());
        if(categoryOptional.isPresent()) {
            Category oldCategory = categoryOptional.get();
            if(category.getCategoryName() != null) {oldCategory.setCategoryName(category.getCategoryName());}
            if(category.getDescription() != null) {oldCategory.setDescription(category.getDescription());}
            categoryRepository.save(oldCategory);
            return true;
        }
        else return false;
    }
}
