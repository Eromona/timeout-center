package cn.bugstack.api.vo;

import cn.bugstack.api.enums.TimeoutCenterStateEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TimeoutTaskVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务唯一id,同一个BizType下不允许重复
     */
    private String bizId;

    /**
     * 超时中心状态
     * <p>
     * {@link TimeoutCenterStateEnum}
     */
    private Integer state;

    /**
     * 任务数据
     */
    private String data;

    /**
     * 任务创建时间
     */
    private String createTime;

    /**
     * 任务修改时间
     */
    private String updateTime;
    /**
     * 任务版本
     */
    private String version;
}
