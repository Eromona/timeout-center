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
    public List<TimeoutTaskEntity> prepareAll(TaskKeys keys) {
        return taskQueueRepository.prepareAll(keys);
    }

    @Override
    public List<TimeoutTaskEntity> prepareLimit(TaskKeys keys, int limit) {
        return taskQueueRepository.prepareLimit(keys, limit);
    }

    @Override
    public List<String> getKeysByPattern(String keyPattern) {
        return Collections.emptyList();
    }

    @Override
    public void commitedTimeoutTask(TimeoutTaskEntity timeoutTaskEntity) {
        taskQueueRepository.commitedTimeoutTask(timeoutTaskEntity);
    }


}
