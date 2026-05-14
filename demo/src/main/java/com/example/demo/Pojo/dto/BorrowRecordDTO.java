package com.example.demo.Pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class BorrowRecordDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private String username;
    private String bookName;
    private String bookAuthor;
    private Date borrowTime;
    private Date returnTime;
}
