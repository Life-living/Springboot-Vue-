package com.example.demo.Service;

import com.example.demo.Pojo.User;

import java.util.List;

public interface IUserService {

    /**
     * 登陆
     *
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);

    /**
     * 查询
     *
     * @return
     */
    List<User> getAllUsers();

    /**
     * 注册
     *
     * @param username
     * @param password
     * @return
     */
    User register(String username, String password);

    /**
     * 忘记密码 - 重置用户密码
     *
     * @param username    用户名
     * @param newPassword 新密码
     */
    void forgetPassword(String username, String newPassword);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);
}
