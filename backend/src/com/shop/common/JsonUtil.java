/**
 * JSON工具类
 * 封装Gson库，提供对象与JSON字符串的互相转换
 * 
 * Gson是Google提供的JSON序列化/反序列化工具
 * 设置日期格式为 yyyy-MM-dd HH:mm:ss
 */
/* 成员4: 数据访问层 - JSON工具类 */
package com.shop.common;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonUtil {
    /** 
     * Gson实例（单例模式）
     * setDateFormat: 设置日期序列化格式
     * serializeNulls: 序列化时保留null值
     */
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .create();

    /**
     * 将对象转换为JSON字符串
     * @param obj 要转换的对象
     * @return JSON格式字符串
     */
    public static String toJson(Object obj) { 
        return gson.toJson(obj); 
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     * @param json JSON字符串
     * @param clazz 目标类型的Class对象
     * @param <T> 泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) { 
        return gson.fromJson(json, clazz); 
    }

    /**
     * 将JSON字符串转换为Map
     * 用于解析前端传来的表单数据（键值对形式）
     * @param json JSON字符串
     * @return Map<String, Object> 键值对集合
     */
    public static Map<String, Object> toMap(String json) {
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * 将Result对象转换为JSON字符串
     * 用于API接口响应
     * @param result Result响应对象
     * @return JSON格式字符串
     */
    public static String resultToJson(Result result) { 
        return gson.toJson(result.toMap()); 
    }
}