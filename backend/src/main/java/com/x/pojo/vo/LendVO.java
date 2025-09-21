package com.x.pojo.vo;

import com.x.pojo.entity.Lend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LendVO implements Serializable {
    private Long lendId;  //唯一标识，用于resultMap，防止去重
    private Lend lend;
    private String title;
    private String author;
}
