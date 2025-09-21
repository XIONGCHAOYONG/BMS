package com.x.controller.user;

import com.x.pojo.entity.Book;
import com.x.service.BookService;
import com.x.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/book")
@Tag(name = "书本接口")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/top10")
    @Operation(summary = "获取top10")
    public Result<List<Book>> getTop10() {
        return Result.success(bookService.getTop10());
    }

    @GetMapping("/top10ByCategory/{categoryId}")
    @Operation(summary = "获取分类top10")
    public Result<List<Book>> getTop10ByCategory(@PathVariable Integer categoryId) {
        return Result.success(bookService.getTop10ByCategory(categoryId));
    }

    @GetMapping("/getBookById/{bookId}")
    @Operation(summary = "根据id获取book")
    public Result<Book> getBookById(@PathVariable Integer bookId) {
        return Result.success(bookService.getBookById(bookId));
    }

    @GetMapping("/getNewBooks")
    @Operation(summary = "获取5本最新书本")
    public Result<List<Book>> getNewBooks() {
        return Result.success(bookService.getNewBooks());
    }

    @GetMapping("/searchBooks")
    @Operation(summary = "搜索书本")
    public Result<List<Book>> searchBooks(@RequestParam String keyword) {
        return Result.success(bookService.searchBooks(keyword));
    }


}