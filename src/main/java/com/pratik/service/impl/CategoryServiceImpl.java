package com.pratik.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pratik.dto.CategoryDto;
import com.pratik.dto.CategoryResponse;
import com.pratik.entity.Category;
import com.pratik.exception.ExistDataException;
import com.pratik.exception.ResourceNotFoundException;
import com.pratik.exception.ValidationException;
import com.pratik.repository.CategoryRepository;
import com.pratik.service.CategoryService;
import com.pratik.util.Validation;

@Service

public class CategoryServiceImpl implements CategoryService  {
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private Validation validation;
	 
	@Override
	public Boolean saveCategory(CategoryDto categorydto) {
		
//		Category category=new Category();
//		category.setName(categorydto.getName());
//		category.se tDescription(categorydto.getDescription());
//		category.setIs_active(categorydto.getIs_active());
		
		//Validation checking
		validation.categoryValidation(categorydto);
		
		// check category exist or not 
		Boolean exist =categoryRepo.existsByName(categorydto.getName().trim());
		if(exist) {
			// throw error
			throw new ExistDataException("category already exist");
		}
		
		
		Category category=mapper.map(categorydto,Category.class);
		
		if(ObjectUtils.isEmpty(category.getId())) {
			category.setIsdeleted(false);
//			category.setCreatedby(1);
			category.setCreatedon(new Date());
		}
		else {
			updateCategory(category);
		}
		Category saveCategory = categoryRepo.save(category);
		if(ObjectUtils.isEmpty(saveCategory)) {
			return false;
		}
		return true; 
	}

	private void updateCategory(Category category) {
		Optional<Category> findById = categoryRepo.findById(category.getId());
		if(findById.isPresent()) {
			Category existCategory = findById.get();
			category.setCreatedby(existCategory.getCreatedby());
			category.setCreatedon(existCategory.getCreatedon());
			category.setIsdeleted(existCategory.getIsdeleted());
			
//			category.setUpdatedby(1);
//			category.setUpdatedon(new Date());
		}
		
		
	}

	@Override
	public List<CategoryDto> getAllCategory() {
		List<Category> categories = categoryRepo.findByIsdeletedFalse();
		  
		List<CategoryDto> categoryDtoList = categories.stream().map(cat -> mapper.map(cat, CategoryDto.class)).toList();
		return categoryDtoList;
	}

	@Override
	public List<CategoryResponse> getActiveCategory() {
		List<Category> categories = categoryRepo.findByIsactiveTrueAndIsdeletedFalse();
		
		List<CategoryResponse> categoryList = categories.stream().map(cat -> mapper.map(cat, CategoryResponse.class)).toList();
		
		return categoryList;
	}
 
	@Override
	public CategoryDto getCategoryById(Integer id) throws Exception {
		Category category = categoryRepo.findByIdAndIsdeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("Category not found with id"+ id));
		
		
		if(!ObjectUtils.isEmpty(category)) {
//			 if(category.getName()==null) {
//				 throw new IllegalArgumentException("Name is null"); 
//			 }
			category.getName().toUpperCase();
			return mapper.map(category, CategoryDto.class);
		}  
		return null;
	}

	@Override
	public Boolean deleteCategory(Integer id) {
		Optional<Category> findByCategory = categoryRepo.findById(id);
		
		if(findByCategory.isPresent()) {
			Category category = findByCategory.get();
			category.setIsdeleted(true);
			categoryRepo.save(category);
			return true;
		}
		return false;
	}

	
	
}
