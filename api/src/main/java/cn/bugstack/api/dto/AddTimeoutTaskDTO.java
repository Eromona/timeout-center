package cn.bugstack.api.dto;

import cn.bugstack.api.enums.TimeoutCenterStateEnum;

import java.io.Serial;
import java.io.Serializable;

public class AddTimeoutTaskDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务唯一id,同一个BizType下不允许重复
     */
    private String bizId;

    /**
     * 任务状态 {@link  TimeoutCenterStateEnum}
     */
    private String state;

    /**
     * 期望执行时间 时间戳-秒级
     */
    private Long actionTime;

    /**
     * 任务数据(建议json格式)
     */
    private String data;
}
