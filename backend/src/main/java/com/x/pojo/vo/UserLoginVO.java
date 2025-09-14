package com.x.pojo.vo;

import com.x.pojo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
   private User user;
    private String token;
}
