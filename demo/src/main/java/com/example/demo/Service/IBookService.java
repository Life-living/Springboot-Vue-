package com.example.demo.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.Pojo.BookInfo;
import com.example.demo.Pojo.Borrow;
import com.example.demo.Pojo.dto.BorrowRecordDTO;

public interface IBookService extends IService<BookInfo> {

    boolean borrowBook(Long userId, Long bookId);

    boolean returnBook(Long bookId);

    Page<BorrowRecordDTO> getAllBorrowRecordsDTO(Long bookId, Boolean onlyUnreturned, Page<Borrow> page);

    Page<BookInfo> searchBooksForUser(String keyword, Integer current, Integer size, Long userId);
}
