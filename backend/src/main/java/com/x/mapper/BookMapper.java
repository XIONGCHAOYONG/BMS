package com.x.mapper;

import com.x.pojo.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookMapper {
    @Select("SELECT * FROM books ORDER BY total_lend DESC LIMIT 10")
     List<Book> getTop10();
    @Select("SELECT * FROM books WHERE category_id = #{categoryId} ORDER BY total_lend DESC LIMIT 10")
    public List<Book> getTop10ByCategory(Integer categoryId);;
    @Select("SELECT * FROM books WHERE book_id = #{bookId}")
    public Book getBookById(Integer bookId);

    @Select("SELECT * FROM books ORDER BY create_time DESC LIMIT 5")
    List<Book> getNewBooks();

    @Select("SELECT * FROM books WHERE title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR author LIKE CONCAT('%', #{keyword}, '%') " +
            "OR summary LIKE CONCAT('%', #{keyword}, '%')")
    List<Book> searchBooks(String keyword);
}
