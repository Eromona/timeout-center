package cn.bugstack.api.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TimeoutTaskVO implements Serializable {
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
