package com.x.controller.user;

import com.x.pojo.entity.Category;
import com.x.service.CategoryService;
import com.x.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/category")
@Tag(name = "分类接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @RequestMapping("/getAll")
    @Operation(summary = "获取所有分类")
    public Result<List<Category>> getAll(){
        return Result.success(categoryService.getAll());
    }
}
