/* 成员4: 数据访问层 - 统一响应格式 */
package com.shop.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应结果类
 * 所有API接口返回此格式: {code, message, data}
 */
public class Result {

    private int code;       // 状态码: 200成功, 400参数错误, 401未登录, 500服务器错误
    private String message; // 提示信息
    private Object data;    // 返回数据

    public Result() {}

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    /** 成功响应，无返回数据 */
    public static Result success() {
        return new Result(200, "操作成功", null);
    }

    /** 成功响应，带返回数据 */
    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }

    /** 成功响应，自定义消息和返回数据 */
    public static Result success(String message, Object data) {
        return new Result(200, message, data);
    }

    /** 失败响应 */
    public static Result error(int code, String message) {
        return new Result(code, message, null);
    }

    /** 参数错误 */
    public static Result badRequest(String message) {
        return new Result(400, message, null);
    }

    /** 未登录/未授权 */
    public static Result unauthorized() {
        return new Result(401, "请先登录", null);
    }

    /** 无权限 */
    public static Result forbidden() {
        return new Result(403, "无权限访问", null);
    }

    /** 服务器内部错误 */
    public static Result serverError() {
        return new Result(500, "服务器内部错误", null);
    }

    /**
     * 将当前对象转换为Map（用于JsonUtil序列化）
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        map.put("data", data);
        return map;
    }
}
