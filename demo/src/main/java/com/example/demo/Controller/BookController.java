package com.example.demo.Controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.Common.Result;
import com.example.demo.Pojo.BookInfo;
import com.example.demo.Pojo.Borrow;
import com.example.demo.Pojo.dto.BorrowRecordDTO;
import com.example.demo.Service.IBookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/book")
class BookController {

    @Autowired
    private IBookService bookService;

    /**
     * 分页查询图书（支持搜索）
     *
     * @param keyword
     * @param current
     * @param size
     * @return
     */
    @GetMapping("/bookInfo/list")
    public Result listBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) { // 获取 userId

        Long userId = (Long) request.getAttribute("userId");
        Page<BookInfo> page = bookService.searchBooksForUser(keyword, current, size, userId);
        return Result.success(page);
    }

    /**
     * 添加图书
     *
     * @param book
     * @return
     */
    @PostMapping("/bookInfo/add")
    public Result addBook(@RequestBody BookInfo book) {
        book.setIsBorrowed((byte) 0); // 默认未借出
        bookService.save(book);
        return Result.success("添加成功");
    }

    /**
     * 修改图书
     *
     * @param book
     * @return
     */
    @PostMapping("/bookInfo/update")
    public Result updateBook(@RequestBody BookInfo book) {
        bookService.updateById(book);
        return Result.success("修改成功");
    }

    /**
     * 删除图书
     *
     * @param id
     * @return
     */
    @DeleteMapping("/bookInfo/delete/{id}")
    public Result deleteBook(@PathVariable Long id) {
        bookService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 借书
     *
     * @param userId
     * @param bookId
     * @return
     */
    @PostMapping("/bookInfo/borrow")
    public Result borrowBook(@RequestAttribute("userId") Long userId,
                             @RequestParam Long bookId) {
        bookService.borrowBook(userId, bookId);
        return Result.success("借阅成功");
    }

    /**
     * 归还
     *
     * @param bookId
     * @return
     */
    @PostMapping("/bookInfo/return")
    public Result returnBook(@RequestParam Long bookId) {
        bookService.returnBook(bookId);
        return Result.success("归还成功");
    }

    /**
     * 查询所有借阅记录（支持条件筛选和分页，含用户名、书名、作者）
     */
    @GetMapping("/borrow/list")
    public Result getAllBorrowRecords(
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Boolean onlyUnreturned,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<Borrow> page = new Page<>(current, size);
        Page<BorrowRecordDTO> result = bookService.getAllBorrowRecordsDTO(bookId, onlyUnreturned, page);
        return Result.success(result);
    }

}
