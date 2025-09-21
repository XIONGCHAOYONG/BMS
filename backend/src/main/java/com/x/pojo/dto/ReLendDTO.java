package com.x.pojo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReLendDTO implements Serializable {
    private Long bookId;
    private Long lendId;
    private Integer days;
    private LocalDate dueDate;
}
