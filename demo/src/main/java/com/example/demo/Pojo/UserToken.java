package com.example.demo.Pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_token")
public class UserToken {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;                  // 关联 User.id
    private String token;                // token 字符串
    private LocalDateTime expireTime;   // 过期时间
}
