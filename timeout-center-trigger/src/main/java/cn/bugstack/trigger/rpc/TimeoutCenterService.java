package cn.bugstack.trigger.rpc;

import cn.bugstack.api.ITimeoutCenterService;
import cn.bugstack.api.dto.AddTimeoutTaskDTO;
import cn.bugstack.api.response.WebResponse;
import cn.bugstack.api.vo.TimeoutTaskVO;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class TimeoutCenterService implements ITimeoutCenterService {
    @Override
    public WebResponse<TimeoutTaskVO> queryTimeoutTask(String bizType, String bizId) {
        return WebResponse.returnSuccess();
    }

    @Override
    public WebResponse<?> addTimeoutTask(AddTimeoutTaskDTO addTimeoutTaskDTO) {
        return null;
    }

    @Override
    public WebResponse<?> cancelTimeoutTask(String bizType, String bizId) {
        return null;
    }
}
