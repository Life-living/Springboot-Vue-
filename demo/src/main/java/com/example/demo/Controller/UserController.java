package com.example.demo.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.Common.Result;
import com.example.demo.Mapper.UserTokenMapper;
import com.example.demo.Pojo.User;
import com.example.demo.Pojo.UserToken;
import com.example.demo.Pojo.dto.ForgetPasswordDTO;
import com.example.demo.Pojo.dto.UserDTO;
import com.example.demo.Service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController         // = @Controller + @ResponseBody  接口方法返回对象 转换成json文本
@CrossOrigin
@ResponseBody
@RequestMapping("/user")      //127.0.0.1:9090/user/**
class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserTokenMapper userTokenMapper;

    /**
     * 登陆
     *
     * @param dto
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        // 调用服务层验证
        User user = userService.login(username, password);
        if (user != null) {

            // 生成 token
            String token = System.currentTimeMillis() + "-" + (int) (Math.random() * 1000000);
            LocalDateTime expireTime = LocalDateTime.now().plusHours(24);

            System.out.println("------------------------------------------------");
            System.out.println(userTokenMapper.selectList(new QueryWrapper<UserToken>().eq("user_id", user.getId())));
            System.out.println("------------------------------------------------");

            // 删除旧 token
            userTokenMapper.delete(new QueryWrapper<UserToken>().eq("user_id", user.getId()));
            // 保存新 token
            UserToken userToken = new UserToken();
            userToken.setUserId(user.getId());
            userToken.setToken(token);
            userToken.setExpireTime(expireTime);
            userTokenMapper.insert(userToken);

            return Result.success(token);
        } else {
            return Result.fail("账号密码错误！");
        }
    }

    /**
     * 查询
     *
     * @return
     */
    @GetMapping("/info")
    public Result getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    /**
     * 删除用户（需要管理员权限）
     *
     * @param id 要删除的用户ID
     * @return Result
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        if (success) {
            return Result.success("用户删除成功");
        } else {
            return Result.fail("用户删除失败，可能用户不存在");
        }
    }

    /**
     * 退出登陆
     *
     * @param userId
     * @return
     */
    @PostMapping("/logout")
    public Result logout(@RequestAttribute("userId") Long userId) {

        // 1. 从 user_token 表中删除该用户的 token
        userTokenMapper.deleteByUserId(userId);

        // 2. 返回成功
        return Result.success("退出成功!");

    }

    /**
     * 注册
     *
     * @param dto
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO dto) {
        try {
            User user = userService.register(dto.getUsername(), dto.getPassword());
            return Result.success("注册成功");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("注册失败：" + e.getMessage());
        }
    }

    /**
     * 忘记密码 - 重置密码（无需登录）
     *
     * @param dto 包含 username, newPassword
     * @return Result
     */
    @PostMapping("/forget")
    public Result forgetPassword(@RequestBody ForgetPasswordDTO dto) {
        try {
            userService.forgetPassword(dto.getUsername(), dto.getNewPassword());
            return Result.success("密码重置成功");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("密码重置失败：" + e.getMessage());
        }
    }
}
