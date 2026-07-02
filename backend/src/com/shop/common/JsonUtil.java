/* 成员4: 数据访问层 - JSON工具类 */
package com.shop.common;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
public class JsonUtil {
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
    public static String toJson(Object obj) { return gson.toJson(obj); }
    public static <T> T fromJson(String json, Class<T> clazz) { return gson.fromJson(json, clazz); }
    public static Map<String, Object> toMap(String json) {
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(json, type);
    }
    public static String resultToJson(Result result) { return gson.toJson(result.toMap()); }
}
