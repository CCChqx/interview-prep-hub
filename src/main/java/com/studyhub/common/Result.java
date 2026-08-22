package com.studyhub.common;

import lombok.Data;

@Data
public class Result <T> {
    private String message;
    private Integer code;
    private T data;

    //成功实例 带数据
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setMessage("success");
        result.setCode(0);
        result.setData(data);
        return result;
    }

    //成功数据 不带数据
    public static <T> Result<T> success() {
        return success(null);
    }

    //失败带数据 需要传入错误信息
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<T>();
        result.setMessage(msg);
        result.setCode(500);
        result.setData(null);
        return result;
    }

    //失败 ＋ 自定义错误码
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<T>();
        result.setMessage(msg);
        result.setCode(code);
        result.setData(null);
        return result;
    }
}
