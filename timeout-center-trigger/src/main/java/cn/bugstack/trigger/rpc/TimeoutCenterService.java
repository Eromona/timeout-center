package cn.bugstack.trigger.rpc;

import cn.bugstack.api.ITimeoutCenterService;
import cn.bugstack.api.dto.AddTimeoutTaskDTO;
import cn.bugstack.api.dto.CommitTimeoutTaskDTO;
import cn.bugstack.api.response.WebResponse;
import cn.bugstack.api.vo.TimeoutTaskVO;
import cn.bugstack.domain.task.adapter.repository.ITaskQueueRepository;
import cn.bugstack.domain.task.entity.TaskKeys;
import cn.bugstack.domain.task.entity.TimeoutTaskEntity;
import cn.bugstack.domain.task.service.ITaskQueueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@DubboService
public class TimeoutCenterService implements ITimeoutCenterService {

    @Resource
    private ITaskQueueService taskQueueService;


    @Override
    public void offerTimeoutTask(AddTimeoutTaskDTO addTimeoutTaskDTO) {
        String bizType = addTimeoutTaskDTO.getBizType();
        String bizId = addTimeoutTaskDTO.getBizId();
        TaskKeys taskKeys = TaskKeys.builder()
                .storeQueueKey(TimeoutTaskVO.getStoreQueueKEey(bizType, bizId))
                .build();
        TimeoutTaskEntity timeoutTaskEntity = TimeoutTaskEntity.builder()
                .actionTime(addTimeoutTaskDTO.getActionTime())
                .taskKeys(taskKeys)
                .task(addTimeoutTaskDTO.getTask())
                .build();
        try {
            taskQueueService.offerStoreQueue(timeoutTaskEntity);
        } catch (Exception e) {
            log.error("offerTimeoutTask error, addTimeoutTaskDTO:{}, timeoutTaskEntity{}",
                    addTimeoutTaskDTO, timeoutTaskEntity,e);
        }
    }

    @Override
    public WebResponse<List<TimeoutTaskVO>> prepare(String bizType, String bizId) {
        try {
            List<TimeoutTaskVO> timeoutTaskVOS = new ArrayList<>();
            TaskKeys taskKeys = TaskKeys.builder()
                    .storeQueueKey(TimeoutTaskVO.getStoreQueueKEey(bizType, bizId))
                    .prepareQueueKey(TimeoutTaskVO.getPrepareQueueKEey(bizType, bizId))
                    .build();
            taskQueueService.prepareAll(taskKeys);
            return WebResponse.returnSuccess(timeoutTaskVOS);
        } catch (Exception e) {
            log.error("pollTimeoutTask error bizType:{}, bizId:{}", bizType, bizId, e);
            return WebResponse.returnFail();
        }
    }

    @Override
    public void commitedTimeoutTask(CommitTimeoutTaskDTO commitTimeoutTaskDTO) {
        String bizType = commitTimeoutTaskDTO.getBizType();
        String bizId = commitTimeoutTaskDTO.getBizId();
        TaskKeys taskKeys = TaskKeys.builder()
                .prepareQueueKey(TimeoutTaskVO.getPrepareQueueKEey(bizType, bizId))
                .build();
        TimeoutTaskEntity timeoutTaskEntity = TimeoutTaskEntity.builder()
                .taskKeys(taskKeys)
                .task(commitTimeoutTaskDTO.getTask())
                .build();
        taskQueueService.commitedTimeoutTask(timeoutTaskEntity);
    }
}
