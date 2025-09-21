package com.x.pojo.entity;

import com.x.common.enumeration.LendStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lend implements Serializable {
    private Long lendId;
    private Long userId;
    private Long bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LendStatus status; // 0:未还 1:已还 2:逾期 3:将逾期 4.续借
    private Integer renewCount;
}
