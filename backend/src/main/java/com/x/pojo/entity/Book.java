package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private Integer bookId;          // 图书ID
    private String title;           // 书名
    private String author;          // 作者
    private String isbn;            // ISBN
    private Integer categoryId;     // 分类ID (外键)
    private String publisher;       // 出版社
    private LocalDate publishDate;  // 出版日期
    private Integer totalStock;     // 总库存
    private Integer availableStock;
    private String summary;         // 摘要
    private Long totalLend ;        // 总借阅
    private Byte status;            // 状态 (0=正常，1=下架)
    private LocalDateTime createTime;
}
