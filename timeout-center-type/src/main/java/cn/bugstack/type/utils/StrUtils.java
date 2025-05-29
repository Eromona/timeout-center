package cn.bugstack.type.utils;

public class StrUtils {
    public static String combine(String ...str1) {
        StringBuilder sb = new StringBuilder();
        for (String str : str1) {
            sb.append(str);
        }
        return sb.toString();
    }
}
