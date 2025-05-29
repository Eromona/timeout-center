package cn.bugstack.domain.task.adapter.repository;

import cn.bugstack.domain.task.entity.TaskKeys;
import cn.bugstack.domain.task.entity.TimeoutTaskEntity;

import java.util.List;

public interface ITaskQueueRepository {

    void offerStoreQueue(TimeoutTaskEntity task);

    List<Object> prepareAll(TaskKeys keys);

    List<Object> prepareLimit(TaskKeys keys, int limit);

    void commitedTimeoutTask(TimeoutTaskEntity timeoutTaskEntity);

    void rollbackTimeoutTask(TimeoutTaskEntity timeoutTaskEntity);

}
