package com.example.demo.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.Mapper.BookInfoMapper;
import com.example.demo.Mapper.BorrowMapper;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Pojo.BookInfo;
import com.example.demo.Pojo.Borrow;
import com.example.demo.Pojo.User;
import com.example.demo.Pojo.dto.BorrowRecordDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService extends ServiceImpl<BookInfoMapper, BookInfo> implements IBookService {

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookInfoMapper bookInfoMapper;

    @Override
    public Page<BookInfo> searchBooksForUser(String keyword, Integer current, Integer size, Long userId) {
        // 1. 构造图书查询条件
        QueryWrapper<BookInfo> bookWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            bookWrapper.like("book_name", keyword)
                    .or()
                    .like("book_author", keyword);
        }

        // 2. 手动分页查询图书（不调用 this.page，而是先查总数 + 列表）
        long total = baseMapper.selectCount(bookWrapper);

        if (total == 0) {
            // 如果没有数据，直接返回空分页
            Page<BookInfo> emptyPage = new Page<>();
            emptyPage.setCurrent(current);
            emptyPage.setSize(size);
            emptyPage.setTotal(0);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        // 分页查询图书列表
        bookWrapper.last("LIMIT " + (current - 1) * size + ", " + size);
        List<BookInfo> bookList = baseMapper.selectList(bookWrapper);

        // 3. 提取 bookId
        List<Long> bookIds = bookList.stream().map(BookInfo::getId).toList();

        // 4. 查询当前用户未归还的借阅记录（仅限这些书）
        Set<Long> myBorrowedBookIds = new HashSet<>();
        if (userId != null && !bookIds.isEmpty()) {
            QueryWrapper<Borrow> borrowWrapper = new QueryWrapper<>();
            borrowWrapper.eq("user_id", userId)
                    .isNull("return_time")
                    .in("book_id", bookIds);
            List<Borrow> myBorrows = borrowMapper.selectList(borrowWrapper);
            myBorrowedBookIds = myBorrows.stream()
                    .map(Borrow::getBookId)
                    .collect(Collectors.toSet());
        }

        // 5. 转换为 BookVO
        Set<Long> finalMyBorrowedBookIds = myBorrowedBookIds;
        List<BookInfo> voList = bookList.stream().map(book -> {
            BookInfo vo = new BookInfo();
            vo.setId(book.getId());
            vo.setBookName(book.getBookName());
            vo.setBookAuthor(book.getBookAuthor());
            vo.setBookPrice(book.getBookPrice());
            vo.setBookDesc(book.getBookDesc());
            vo.setIsBorrowed(book.getIsBorrowed());
            vo.setBorrowedByMe(finalMyBorrowedBookIds.contains(book.getId()));
            return vo;
        }).toList();

        // 6. 构造分页对象
        Page<BookInfo> resultPage = new Page<>();
        resultPage.setCurrent(current);
        resultPage.setSize(size);
        resultPage.setTotal(total);
        resultPage.setRecords(voList);
        // pages 可以自动算，但 Element Plus 不依赖它，可省略

        return resultPage;
    }

    /**
     * @param userId
     * @param bookId
     * @return
     */
    @Transactional
    @Override
    public boolean borrowBook(Long userId, Long bookId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        if (book.getIsBorrowed() == 1) {
            throw new RuntimeException("该书已被借出");
        }

        // 更新图书状态
        book.setIsBorrowed((byte) 1);
        this.updateById(book);

        // 插入借阅记录
        Borrow borrow = new Borrow();
        borrow.setUserId((long) Math.toIntExact(userId));
        borrow.setBookId((long) Math.toIntExact(bookId));
        borrow.setBorrowTime(new Date());
        borrowMapper.insert(borrow);

        return true;
    }

    /**
     * @param bookId
     * @return
     */
    @Transactional
    @Override
    public boolean returnBook(Long bookId) {
        BookInfo book = this.getById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        if (book.getIsBorrowed() == 0) {
            throw new RuntimeException("该书未被借出");
        }

        // 更新图书状态
        book.setIsBorrowed((byte) 0);
        this.updateById(book);

        // 更新归还时间（找到最新未归还的记录）
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();
        wrapper.eq("book_id", bookId)
                .isNull("return_time")
                .orderByDesc("borrow_time")
                .last("LIMIT 1");

        Borrow borrow = borrowMapper.selectOne(wrapper);
        if (borrow != null) {
            borrow.setReturnTime(new Date());
            borrowMapper.updateById(borrow);
        }

        return true;
    }

    public Page<BorrowRecordDTO> getAllBorrowRecordsDTO(Long bookId, Boolean onlyUnreturned, Page<Borrow> page) {
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();

        if (bookId != null) {
            wrapper.eq("book_id", bookId);
        }
        if (onlyUnreturned != null && onlyUnreturned) {
            wrapper.isNull("return_time");
        }

        wrapper.orderByDesc("borrow_time");

        // 分页查询借阅记录
        Page<Borrow> borrowPage = borrowMapper.selectPage(page, wrapper);

        // 提取所有 bookId

        List<Long> bookIds = borrowPage.getRecords().stream()
                .map(Borrow::getBookId)
                .distinct()
                .toList();

        // 查询所有相关图书信息
        List<BookInfo> books = bookIds.isEmpty() ? List.of() :
                bookInfoMapper.selectBatchIds(bookIds);

        // 转为 Map 提高查找效率
        Map<Long, BookInfo> bookMap = books.stream()
                .collect(Collectors.toMap(BookInfo::getId, b -> b));

        // 转换为 VO 列表
        List<BorrowRecordDTO> voList = borrowPage.getRecords().stream().map(borrow -> {
            BorrowRecordDTO dto = new BorrowRecordDTO();
            BeanUtils.copyProperties(borrow, dto);
            BookInfo book = bookMap.get(borrow.getBookId());
            if (book != null) {
                dto.setBookName(book.getBookName());
                dto.setBookAuthor(book.getBookAuthor());
            }
            return dto;
        }).toList();

        // 构造新的 Page<BorrowRecordVO>
        Page<BorrowRecordDTO> resultPage = new Page<>();
        resultPage.setCurrent(borrowPage.getCurrent());
        resultPage.setSize(borrowPage.getSize());
        resultPage.setTotal(borrowPage.getTotal());
        resultPage.setPages(borrowPage.getPages());
        resultPage.setRecords(voList);

        return resultPage;
    }
}
