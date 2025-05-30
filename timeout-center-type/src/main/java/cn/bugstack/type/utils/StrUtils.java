package cn.bugstack.type.utils;

public class StrUtils {
    public static String combine(Object ...str1) {
        StringBuilder sb = new StringBuilder();
        for (Object str : str1) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
