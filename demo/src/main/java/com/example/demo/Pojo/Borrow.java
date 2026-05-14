package com.example.demo.Pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("borrow")
public class Borrow {
    @TableId(type = IdType.AUTO)
    private Long Id;

    private Long userId;
    private Long bookId;
    private Date borrowTime;
    private Date returnTime;
}
