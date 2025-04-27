package com.service;

import com.entity.Category;
import com.entity.Product;
import com.entity.ProductVariant;
import com.repository.BrandRepository;
import com.repository.CategoryRepository;
import com.repository.ProductRepository;
import com.entity.dto.ProductDTO;
import com.entity.dto.ProductVariantDTO;
import com.repository.ProductVariantRepository;
import com.request.ProductRequest;
import com.request.ProductVariantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setSpecifications(product.getSpecifications());
        dto.setPrice(product.getPrice());
        dto.setWeight(product.getWeight());
        String category = categoryRepository.findByCategoryId(product.getCategoryId()).get().getCategoryName();

        System.out.println(category);
        String brand = brandRepository.findByBrandId(product.getBrandId()).get().getBrandName();
        dto.setCategoryName(category);
        dto.setBrandName(brand);
        dto.setSupportRushOrder(product.getSupportRushOrder());

        List<ProductVariantDTO> variantDTOs = product.getVariants().stream().map(variant -> {
            ProductVariantDTO vDto = new ProductVariantDTO();
            vDto.setVariantId(variant.getVariantId());
            vDto.setColor(variant.getColor());
            vDto.setDiscount(variant.getDiscountPercentage());
            vDto.setImageUrl(variant.getImageUrl());
            vDto.setStockQuantity(variant.getStockQuantity());
            return vDto;
        }).collect(Collectors.toList());

        dto.setVariants(variantDTOs);
        return dto;
    }
    public Product toEntity(ProductRequest request) {
        // Tạo Product
        Product product = new Product();
        if(request.getProductId() != null) {
            product.setProductId(request.getProductId());
        }
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setSpecifications(request.getSpecifications());
        product.setWeight(request.getWeight());
        product.setPrice(request.getPrice());
        System.out.println(request.getCategoryName());
        System.out.println("---------------------------------------------------------");
        Integer categoryId = categoryRepository.findByCategoryNameIgnoreCase(request.getCategoryName()).get().getCategoryId();
        Integer brandId = brandRepository.findByBrandNameIgnoreCase(request.getBrandName()).get().getBrandId();

        product.setCategoryId(categoryId);
        product.setBrandId(brandId);
        product.setSupportRushOrder(request.getSupportRushOrder());

        // Map danh sách variants
        List<ProductVariant> variants = request.getVariants().stream()
                .map(variantReq -> {
                    ProductVariant variant = new ProductVariant();
                    variant.setColor(variantReq.getColor());
                    variant.setDiscountPercentage(variantReq.getDiscountPercentage());
                    variant.setStockQuantity(variantReq.getStockQuantity());
                    variant.setImageUrl(variantReq.getImageUrl());

                    variant.setProduct(product);
                    return variant;
                })
                .collect(Collectors.toList());

        product.setVariants(variants);
        return product;
    }
    public Product getProductByProductName(String productName) {
        return productRepository.findByProductName(productName).get();
    }

    public boolean addProduct(ProductRequest request) {
        if(productRepository.findByProductName(request.getProductName()).isPresent()) {
            return false;
        }
        else{
            Product product = toEntity(request);
            productRepository.save(product);
            return true;
        }
    }

    public boolean deleteProduct(Integer productId) {
        if(productRepository.findById(productId).isPresent()) {
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    public void updateProduct(ProductRequest request) {
        Product product = toEntity(request);
        product.setUpdatedAt(LocalDateTime.now());


        Set<Integer> updatedVariantIds = request.getVariants().stream()
                .filter(v -> v.getVariantId() != null)
                .map(ProductVariantRequest::getVariantId)
                .collect(Collectors.toSet());

        List<ProductVariant> currentVariants = productVariantRepository.findByProduct_ProductId(product.getProductId());

        // Bước 3: Xóa những variant cũ mà không còn trong danh sách mới
        for (ProductVariant variant : currentVariants) {
            if (!updatedVariantIds.contains(variant.getVariantId())) {
                productVariantRepository.delete(variant);
            }
        }
        for (ProductVariantRequest variantReq : request.getVariants()) {
            if (variantReq.getVariantId() != null && productVariantRepository.findById(variantReq.getVariantId()).isPresent()) {
                ProductVariant variant = productVariantRepository.findById(variantReq.getVariantId()).get();
                variant.setColor(variantReq.getColor());
                variant.setImageUrl(variantReq.getImageUrl());
                variant.setStockQuantity(variantReq.getStockQuantity());
                variant.setDiscountPercentage(variantReq.getDiscountPercentage());
                productVariantRepository.save(variant);
            } else {
                // Insert variant mới
                ProductVariant newVariant = new ProductVariant();
                newVariant.setProduct(product);
                newVariant.setColor(variantReq.getColor());
                newVariant.setImageUrl(variantReq.getImageUrl());
                newVariant.setStockQuantity(variantReq.getStockQuantity());
                newVariant.setDiscountPercentage(variantReq.getDiscountPercentage());
                productVariantRepository.save(newVariant);
            }
        }

        productRepository.save(product);
    }

    public ProductDTO getProductById(Integer productId) {
        if(productRepository.findById(productId).isPresent()) {
            return  toDTO(productRepository.findById(productId).get());
        }
        return null;
    }

    public List<ProductDTO> getProductsByCategory(String categoryName) {
        Optional<Category> category = categoryRepository.findByCategoryNameIgnoreCase(categoryName);
        if(category.isPresent()) {
            List<Product> products = productRepository.findByCategoryId(category.get().getCategoryId());
            List<ProductDTO> productDTOs = new ArrayList<>();
            for(Product product : products) {
                ProductDTO dto = toDTO(product);
                productDTOs.add(dto);
            }
            return productDTOs;
        }
        else return null;

    }

    public List<ProductDTO> searchProductsByName(String search) {

        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(search);
        List<ProductDTO> productDTOs = new ArrayList<>();
        for(Product product : products) {
            ProductDTO dto = toDTO(product);
            productDTOs.add(dto);
        }
        return productDTOs;
    }

    public List<ProductDTO> getProductByCategoryAndBrand(String categoryName, String brandName) {
        Integer categoryId = categoryRepository.findByCategoryNameIgnoreCase(categoryName).get().getCategoryId();
        Integer brandId = categoryRepository.findByCategoryNameIgnoreCase(categoryName).get().getCategoryId();


        List<Product> products = productRepository.findProductsByCategoryIdAndBrandId(categoryId, brandId);
        List<ProductDTO> productDTOs = new ArrayList<>();
        for(Product product : products) {
            ProductDTO dto = toDTO(product);
            productDTOs.add(dto);
        }
        return productDTOs;
    }








}
