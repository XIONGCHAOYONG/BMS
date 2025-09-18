package com.x.service;

import com.x.pojo.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> getTop10();

    List<Book> getTop10ByCategory(Integer categoryId);

    Book getBookById(Integer bookId);

    List<Book> getNewBooks();

    List<Book> searchBooks(String keyword);
}
