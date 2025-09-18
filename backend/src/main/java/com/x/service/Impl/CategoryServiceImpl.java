package com.x.service.Impl;


import com.x.mapper.CategoryMapper;
import com.x.pojo.entity.Category;
import com.x.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> getAll() {
        return categoryMapper.getAll();
    }
}
