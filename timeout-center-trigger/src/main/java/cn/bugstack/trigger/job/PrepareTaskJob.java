package cn.bugstack.trigger.job;

import cn.bugstack.domain.task.entity.TaskKeys;
import cn.bugstack.domain.task.service.ITaskQueueService;
import cn.bugstack.type.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class PrepareTaskJob {
    @Resource
    private ITaskQueueService taskQueueService;

    private static final Set<String> set = new HashSet<>();

    // @PostConstruct
    public void doPostConstruct() {
        set.addAll(taskQueueService.getKeysByPattern(Constant.STORE_QUEUE_PREFIX + "*"));
    }

    // @Scheduled(cron = "*/5 * * * * ?")
    public void prepareTaskJob() {
        set.forEach(key -> {
            TaskKeys taskKeys = TaskKeys.builder()
                    .storeQueueKey(key)
                    .build();
            taskQueueService.prepareLimit(taskKeys, 100);
        });
    }

}
