package cn.bugstack.trigger.rpc;

import cn.bugstack.api.ITimeoutCenterService;
import cn.bugstack.api.dto.AddTimeoutTaskDTO;
import cn.bugstack.api.dto.CommitTimeoutTaskDTO;
import cn.bugstack.api.dto.RollbackTimeoutTaskDTO;
import cn.bugstack.api.response.WebResponse;
import cn.bugstack.api.vo.TimeoutTaskVO;
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
                .storeQueueKey(TimeoutTaskVO.getStoreQueueKey(bizType, bizId))
                .hashKey(TimeoutTaskVO.getHashKey(bizType, bizId))
                .build();
        TimeoutTaskEntity timeoutTaskEntity = TimeoutTaskEntity.builder()
                .actionTime(addTimeoutTaskDTO.getActionTime())
                .UUID(addTimeoutTaskDTO.getUUID())
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
                    .storeQueueKey(TimeoutTaskVO.getStoreQueueKey(bizType, bizId))
                    .prepareQueueKey(TimeoutTaskVO.getPrepareQueueKey(bizType, bizId))
                    .hashKey(TimeoutTaskVO.getHashKey(bizType, bizId))
                    .build();

            taskQueueService.prepareLimit(taskKeys, 200).forEach(task -> {
                        timeoutTaskVOS.add(TimeoutTaskVO.builder().task(task).build());
            });

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
                .prepareQueueKey(TimeoutTaskVO.getPrepareQueueKey(bizType, bizId))
                .hashKey(TimeoutTaskVO.getHashKey(bizType, bizId))
                .build();
        TimeoutTaskEntity timeoutTaskEntity = TimeoutTaskEntity.builder()
                .taskKeys(taskKeys)
                .UUID(commitTimeoutTaskDTO.getUUID())
                .build();

        taskQueueService.commitedTimeoutTask(timeoutTaskEntity);
    }

    @Override
    public void rollbackTimeoutTask(RollbackTimeoutTaskDTO rollbackTimeoutTaskDTO) {
        String bizType = rollbackTimeoutTaskDTO.getBizType();
        String bizId = rollbackTimeoutTaskDTO.getBizId();

        TaskKeys taskKeys = TaskKeys.builder()
                .prepareQueueKey(TimeoutTaskVO.getPrepareQueueKey(bizType, bizId))
                .deadQueueKey(TimeoutTaskVO.getDeadQueueKey(bizType, bizId))
                .hashKey(TimeoutTaskVO.getHashKey(bizType, bizId))
                .build();
        TimeoutTaskEntity timeoutTaskEntity = TimeoutTaskEntity.builder()
                .taskKeys(taskKeys)
                .task(rollbackTimeoutTaskDTO.getTask())
                .build();

        taskQueueService.rollbackTimeoutTask(timeoutTaskEntity);
    }
}
