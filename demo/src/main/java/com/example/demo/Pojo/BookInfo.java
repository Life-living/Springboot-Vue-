package com.example.demo.Pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("book_info")
public class BookInfo {
    @TableId(type = IdType.AUTO)
    private Long Id;

    private String bookName;
    private String bookAuthor;
    private BigDecimal bookPrice;
    private String bookDesc;
    private Byte isBorrowed;     // 0: 可借, 1: 已借
    @TableField(exist = false)
    private Boolean borrowedByMe;
}
