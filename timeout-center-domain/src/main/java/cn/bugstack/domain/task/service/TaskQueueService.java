package cn.bugstack.domain.task.service;

import cn.bugstack.domain.task.adapter.repository.ITaskQueueRepository;
import cn.bugstack.domain.task.entity.TaskKeys;
import cn.bugstack.domain.task.entity.TimeoutTaskEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class TaskQueueService implements ITaskQueueService {

    @Resource
    private ITaskQueueRepository taskQueueRepository;

    @Override
    public void offerStoreQueue(TimeoutTaskEntity task) {
        taskQueueRepository.offerStoreQueue(task);
    }

    @Override
    public List<Object> prepareAll(TaskKeys keys) {
        return taskQueueRepository.prepareAll(keys);
    }

    @Override
    public List<Object> prepareLimit(TaskKeys keys, int limit) {
        return taskQueueRepository.prepareLimit(keys, limit);
    }

    @Override
    public List<String> getKeysByPattern(String keyPattern) {
        // TODO
        return Collections.emptyList();
    }

    @Override
    public void commitedTimeoutTask(TimeoutTaskEntity timeoutTaskEntity) {
        taskQueueRepository.commitedTimeoutTask(timeoutTaskEntity);
    }

    @Override
    public void rollbackTimeoutTask(TimeoutTaskEntity timeoutTaskEntity) {
        taskQueueRepository.rollbackTimeoutTask(timeoutTaskEntity);
    }


}
