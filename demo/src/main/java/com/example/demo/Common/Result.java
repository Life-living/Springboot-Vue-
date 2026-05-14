package com.example.demo.Common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)      // 避免返回空字段
public class Result {
    private boolean success;
    private String message;
    private Object data;

    public static Result success(Object data) {
        Result result = new Result();
        result.success = true;
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    public static Result fail(String message) {
        Result result = new Result();
        result.success = false;
        result.message = message;
        return result;
    }
}
