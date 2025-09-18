package com.x.controller.user;

import com.x.pojo.entity.Category;
import com.x.service.CategoryService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @RequestMapping("/getAll")
    public Result<List<Category>> getAll(){
        return Result.success(categoryService.getAll());
    }
}
