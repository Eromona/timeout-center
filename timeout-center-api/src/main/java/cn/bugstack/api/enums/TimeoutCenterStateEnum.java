package cn.bugstack.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TimeoutCenterStateEnum {
    /**
     * 待处理
     */
    WAIT(0, "待处理"),
    /**
     * 已处理
     */
    SUCCESS(1, "已处理"),
    /**
     * 取消
     */
    CANCEL(2, "取消"),
    /**
     * 处理失败
     */
    FAILED(3, "失败"),
    /**
     * 执行中
     */
    EXECUTION(4, "执行中")
    ;

    private Integer code;
    private String stateStr;

    public static String getStateByCode(Integer code) {
        for (TimeoutCenterStateEnum stateEnum : TimeoutCenterStateEnum.values()) {
            if (stateEnum.getCode().equals(code)) {
                return stateEnum.getStateStr();
            }
        }
        return null;
    }
}
