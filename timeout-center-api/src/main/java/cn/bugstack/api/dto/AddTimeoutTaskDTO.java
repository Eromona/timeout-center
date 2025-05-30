package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddTimeoutTaskDTO implements Serializable {
    private static final long serialVersionUID = -1;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务唯一id 同一个BizType下不允许重复
     */
    private String bizId;

    /**
    * 任务UUID 同一个业务+业务id下不可重复
    */
    private String UUID;

    /**
     * 期望执行时间 时间戳-秒级
     */
    private Long actionTime;

    /**
     * 任务数据
     */
    private Object task;

}
