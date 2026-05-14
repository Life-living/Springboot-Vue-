package com.example.demo.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Mapper.UserTokenMapper;
import com.example.demo.Pojo.User;
import com.example.demo.Pojo.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service    // spring的Bean
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserTokenMapper userTokenMapper;

    /**
     * @param username
     * @param password
     * @return
     */
    @Override
    public User login(String username, String password) {
        // 查询用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username).eq("password", password);
        System.out.println("------------------------------------");
        System.out.println(userMapper.selectOne(wrapper));
        System.out.println("------------------------------------");
        return userMapper.selectOne(wrapper);
    }

    /**
     * @return
     */
    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    /**
     * @param username
     * @param password
     * @return
     */
    @Override
    public User register(String username, String password) {
        // 1. 校验参数
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("密码至少6位");
        }

        // 2. 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User existingUser = userMapper.selectOne(queryWrapper);
        System.out.println("******************************************************");
        System.out.println(existingUser);
        System.out.println("******************************************************");
        if (existingUser != null) {
//            if (existingUser.getUsername().equals("admin")) {
//                throw new IllegalArgumentException(" ");
//            }
            throw new IllegalArgumentException("用户名已存在");
        }

        // 3. 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
//        user.setRole("user"); // 默认角色

        // 4. 保存到数据库
        userMapper.insert(user);

        return user;
    }

    /**
     * 忘记密码
     * @param username    用户名
     * @param newPassword 新密码
     */
    @Override
    @Transactional
    public void forgetPassword(String username, String newPassword) {
        // 1. 校验参数
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码至少6位");
        }
        if (username.equals("admin")) {
            throw new IllegalArgumentException("当前账户无权修改");
        }

        // 2. 查询用户是否存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new IllegalArgumentException("用户名不存在");
        }

        // 3. 更新密码
        user.setPassword(newPassword); // ⚠️ 生产环境应使用 BCrypt 加密
        userMapper.updateById(user);
    }


    /**
     * @param id 用户ID
     * @return
     */
    @Override
    @Transactional
    public boolean deleteUser(Long id) {

        // 1. 查询用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            return false;
        }

        // 2. 删除该用户的 token 记录（保持数据一致性）
        userTokenMapper.delete(new QueryWrapper<UserToken>().eq("user_id", id));

        // 3. 删除用户本身
        int rows = userMapper.deleteById(id);

        return rows > 0;
    }
}
