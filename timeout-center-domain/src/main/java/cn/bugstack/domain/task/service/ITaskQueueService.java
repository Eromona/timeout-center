package cn.bugstack.domain.task.service;

import cn.bugstack.domain.task.entity.TaskKeys;
import cn.bugstack.domain.task.entity.TimeoutTaskEntity;

import java.util.List;

public interface ITaskQueueService {
    void offerStoreQueue(TimeoutTaskEntity task);

    List<TimeoutTaskEntity> prepareAll(TaskKeys keys);

    List<TimeoutTaskEntity> prepareLimit(TaskKeys keys, int limit);

    List<String> getKeysByPattern(String keyPattern);

    void commitedTimeoutTask(TimeoutTaskEntity timeoutTaskEntity);
}
