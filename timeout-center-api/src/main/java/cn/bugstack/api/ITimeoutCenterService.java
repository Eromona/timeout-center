package cn.bugstack.api;

import cn.bugstack.api.dto.AddTimeoutTaskDTO;
import cn.bugstack.api.vo.TimeoutTaskVO;
import cn.bugstack.api.response.WebResponse;

public interface ITimeoutCenterService {

    /**
     * 查询超时中心任务
     */
    WebResponse<TimeoutTaskVO> queryTimeoutTask(String bizType, String bizId);

    /**
     * 添加超时中心任务
     */
    WebResponse<?> addTimeoutTask(AddTimeoutTaskDTO addTimeoutTaskDTO);

    /**
     * 取消超时中心任务
     */
    WebResponse<?> cancelTimeoutTask(String bizType, String bizId);

}
