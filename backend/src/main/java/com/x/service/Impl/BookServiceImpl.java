package com.x.service.Impl;

import com.x.mapper.BookMapper;
import com.x.pojo.entity.Book;
import com.x.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookMapper bookMapper;
    @Override
    public List<Book> getTop10() {
        return bookMapper.getTop10();
    }

    @Override
    public List<Book> getTop10ByCategory(Integer categoryId) {
        return bookMapper.getTop10ByCategory(categoryId);
    }

    @Override
    public Book getBookById(Integer bookId) {
        return bookMapper.getBookById(bookId);
    }

    @Override
    public List<Book> getNewBooks() {
        return  bookMapper.getNewBooks();
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        return bookMapper.searchBooks(keyword);
    }
}
