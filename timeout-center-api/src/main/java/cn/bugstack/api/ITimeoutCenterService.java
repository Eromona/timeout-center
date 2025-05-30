package cn.bugstack.api;

import cn.bugstack.api.dto.AddTimeoutTaskDTO;
import cn.bugstack.api.dto.CommitTimeoutTaskDTO;
import cn.bugstack.api.dto.RollbackTimeoutTaskDTO;
import cn.bugstack.api.vo.TimeoutTaskVO;
import cn.bugstack.api.response.WebResponse;

import java.util.List;

public interface ITimeoutCenterService {
    /**
     * 添加超时中心任务
     */
    void offerTimeoutTask(AddTimeoutTaskDTO addTimeoutTaskDTO);

    /**
     * 准备中心任务 -> 一阶段提交
     */
    WebResponse<List<TimeoutTaskVO>> prepare(String bizType, String bizId);

    /**
    * 提交超时任务 -> 二阶段提交
    */
    void commitedTimeoutTask(CommitTimeoutTaskDTO commitTimeoutTaskDTO);

    /**
     * 回滚超时任务 -> 二阶段回滚
     */
    void rollbackTimeoutTask(RollbackTimeoutTaskDTO rollbackTimeoutTaskDTO);
}
