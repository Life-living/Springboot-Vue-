package com.example.demo.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.Pojo.UserToken;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestAttribute;

@Mapper
public interface UserTokenMapper extends BaseMapper<UserToken> {

    // 根据用户 ID 删除 token
    @Delete("DELETE FROM user_token WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);
}
