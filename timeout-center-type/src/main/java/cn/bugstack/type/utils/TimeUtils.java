package cn.bugstack.type.utils;

public class TimeUtils {
    public static double getSecondTimestamp() {
        return (double) System.currentTimeMillis() / 1000;
    }

}
