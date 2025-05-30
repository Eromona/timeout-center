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
        TaskKeys taskKeys = task.getTaskKeys();
        redisService.evalCachedScript(
                "redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2]); " +
                "redis.call('HSET', KEYS[2], ARGV[2], ARGV[3]); ",
                RScript.ReturnType.VALUE,
                Arrays.asList(taskKeys.getStoreQueueKey(), taskKeys.getHashKey()),
                (TimeUtils.getSecondTimestamp() + task.getActionTime()) * 1000L,
                task.getUUID(),
                task.getTask()
        );
    }


    @Override
    public List<Object> prepareAll(TaskKeys keys) {
        return redisService.evalCachedScript(
                "local items = redis.call('ZRANGEBYSCORE', KEYS[1], '-inf', ARGV[1]); " +
                        "if #items == 0 then return {}; end; " +
                        "local result = {}; " +
                        "for i, item in ipairs(items) do " +
                        "  local score = redis.call('ZSCORE', KEYS[1], item); " +
                        "  if score then " +
                        "    redis.call('ZREM', KEYS[1], item); " +
                        "    redis.call('ZADD', KEYS[2], score, item); " +
                        "    local value = redis.call('HGET', KEYS[3], item); " +
                        "    if value then table.insert(result, value); end " +
                        "  end " +
                        "end; " +
                        "return result;",
                RScript.ReturnType.MULTI,
                Arrays.asList(
                        keys.getStoreQueueKey(),      // KEYS[1] -> ZSET: store queue
                        keys.getPrepareQueueKey(),    // KEYS[2] -> ZSET: prepare queue
                        keys.getHashKey()             // KEYS[3] -> HASH: task data
                ),
                System.currentTimeMillis() * 1.0 // ARGV[1]: 当前时间戳
        );

    }

    @Override
    public List<Object> prepareLimit(TaskKeys keys, int limit) {
        return redisService.evalCachedScript(
                "local now = tonumber(ARGV[1]); " +
                        "local maxCount = tonumber(ARGV[2]); " +
                        "local items = redis.call('ZRANGEBYSCORE', KEYS[1], '-inf', now, 'LIMIT', 0, maxCount); " +
                        "if #items == 0 then return {}; end; " +
                        "local result = {}; " +
                        "for i, item in ipairs(items) do " +
                        "  local score = redis.call('ZSCORE', KEYS[1], item); " +
                        "  if score then " +
                        "    redis.call('ZREM', KEYS[1], item); " +
                        "    redis.call('ZADD', KEYS[2], score, item); " +
                        "    local value = redis.call('HGET', KEYS[3], item); " +
                        "    if value then table.insert(result, value); end " +
                        "  end " +
                        "end; " +
                        "return result;",
                RScript.ReturnType.MULTI,
                Arrays.asList(
                        keys.getStoreQueueKey(),     // KEYS[1] - ZSet: store queue
                        keys.getPrepareQueueKey(),   // KEYS[2] - ZSet: prepare queue
                        keys.getHashKey()            // KEYS[3] - Hash: message content
                ),
                System.currentTimeMillis() * 1.0, // ARGV[1] - 当前时间戳
                limit                             // ARGV[2] - 限制条数
        );
    }

    @Override
    public void commitedTimeoutTask(TimeoutTaskEntity timeoutTaskEntity) {
        TaskKeys taskKeys = timeoutTaskEntity.getTaskKeys();
        String UUID = timeoutTaskEntity.getUUID();
        redisService.evalCachedScript(
                "redis.call('ZREM', KEYS[1], ARGV[1]); " +
                "redis.call('HDEL', KEYS[2], ARGV[1]); ",
                RScript.ReturnType.VALUE,
                Arrays.asList(taskKeys.getPrepareQueueKey(), taskKeys.getHashKey()),
                UUID
        );
    }

    @Override
    public void rollbackTimeoutTask(TimeoutTaskEntity timeoutTaskEntity) {
        TaskKeys keys = timeoutTaskEntity.getTaskKeys();
        String prepareQueueKey = keys.getPrepareQueueKey();
        String deadQueueKey = keys.getDeadQueueKey();
        String UUID = timeoutTaskEntity.getUUID();

        redisService.evalCachedScript(
                "local score = redis.call('ZSCORE', KEYS[1], ARGV[1]); " +
                        "if not score then return 0 end; " +
                        "redis.call('ZREM', KEYS[1], ARGV[1]); " +
                        "if (tonumber(score) % 1000) >= 15 then " +
                        "  redis.call('ZADD', KEYS[2], score, ARGV[1]); " +
                        "else " +
                        "  redis.call('ZADD', KEYS[1], score + 1, ARGV[1]); " +
                        "end; " +
                        "return 1;",
                RScript.ReturnType.VALUE,
                Arrays.asList(prepareQueueKey, deadQueueKey),
                UUID
        );
    }
}
