package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.task.adapter.repository.ITaskQueueRepository;
import cn.bugstack.domain.task.entity.TaskKeys;
import cn.bugstack.domain.task.entity.TimeoutTaskEntity;
import cn.bugstack.infrastructure.redis.IRedisService;
import cn.bugstack.type.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
public class RedisTaskQueueRepository implements ITaskQueueRepository {

    @Resource
    private IRedisService redisService;

    @Override
    public void offerStoreQueue(TimeoutTaskEntity task) {

        redisService.offerScoreSortedSet(task.getTaskKeys().getStoreQueueKey(), task.getTask(),
                (TimeUtils.getSecondTimestamp() + task.getActionTime()) * 1000);

    }


    @Override
    public List<Object> prepareAll(TaskKeys keys) {
        RScript script = redisService.getScript();
        return script.eval(
                RScript.Mode.READ_WRITE,
                "local items = redis.call('ZRANGEBYSCORE', KEYS[1], '-inf', ARGV[1]); " +
                        "if #items == 0 then return {}; end; " +
                        "for i, item in ipairs(items) do " +
                        "  local score = redis.call('ZSCORE', KEYS[1], item); " +
                        "  if score then " +
                        "    redis.call('ZREM', KEYS[1], item); " +
                        "    redis.call('ZADD', KEYS[2], score, item); " +
                        "  end " +
                        "end; " +
                        "return items;",
                RScript.ReturnType.MULTI,
                Arrays.asList(keys.getStoreQueueKey(), keys.getPrepareQueueKey()),
                System.currentTimeMillis() * 1.0
        );
    }

    @Override
    public List<Object> prepareLimit(TaskKeys keys, int limit) {
        RScript script = redisService.getScript();
        return script.eval(
                RScript.Mode.READ_WRITE,
                "local now = tonumber(ARGV[1]); " +
                        "local maxCount = tonumber(ARGV[2]); " +
                        "local items = redis.call('ZRANGEBYSCORE', KEYS[1], '-inf', now, 'LIMIT', 0, maxCount); " +
                        "if #items == 0 then return {}; end; " +
                        "for i, item in ipairs(items) do " +
                        "  local score = redis.call('ZSCORE', KEYS[1], item); " +
                        "  if score then " +
                        "    redis.call('ZREM', KEYS[1], item); " +
                        "    redis.call('ZADD', KEYS[2], score, item); " +
                        "  end " +
                        "end; " +
                        "return items;",
                RScript.ReturnType.MULTI,
                Arrays.asList("storeQueueKey", "prepareQueueKey"),
                System.currentTimeMillis() * 1.0,
                limit
        );
    }

    @Override
    public void commitedTimeoutTask(TimeoutTaskEntity timeoutTaskEntity) {
        redisService.deleteFromScoreSortedSet(timeoutTaskEntity.getTaskKeys().getPrepareQueueKey(),
                timeoutTaskEntity.getTask());
    }

    @Override
    public void rollbackTimeoutTask(TimeoutTaskEntity timeoutTaskEntity) {
        String prepareQueueKey = timeoutTaskEntity.getTaskKeys().getPrepareQueueKey();
        String deadQueueKey = timeoutTaskEntity.getTaskKeys().getDeadQueueKey();
        Object task = timeoutTaskEntity.getTask();

        log.info("rollback timeout, timeoutTaskEntity:{}", timeoutTaskEntity);
        long score = (long) redisService.getScoreByObject(prepareQueueKey, task);
        redisService.deleteFromScoreSortedSet(prepareQueueKey, task);
        if ((score % 1000) >= 15) {
            redisService.offerScoreSortedSet(deadQueueKey, task, score);
        } else {
            redisService.offerScoreSortedSet(prepareQueueKey, task, score + 1);
        }
    }
}
