package cn.bugstack.domain.task.adapter.repository;

import cn.bugstack.domain.task.entity.TaskKeys;
import cn.bugstack.domain.task.entity.TimeoutTaskEntity;

import java.util.List;

public interface ITaskQueueRepository {

    void offerStoreQueue(TimeoutTaskEntity task);

    List<TimeoutTaskEntity> prepareAll(TaskKeys keys);

    List<TimeoutTaskEntity> prepareLimit(TaskKeys keys, int limit);

    void commitedTimeoutTask(TimeoutTaskEntity timeoutTaskEntity);
}
