package cn.bugstack.infrastructure.redis;


import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("redissonService")
public class RedissonService implements IRedisService {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public <T> RScoredSortedSet<T> getScoredSortedSet(String key) {
        return redissonClient.getScoredSortedSet(key);
    }

    @Override
    public <T> void offerScoredSortedSet(String key, T value, double score) {
        RScoredSortedSet<Object> scoredSortedSet = getScoredSortedSet(key);
        scoredSortedSet.add(score, value);
    }

    @Override
    public RScript getScript() {
        return redissonClient.getScript();
    }

    @Override
    public void deleteFromScoreSortedSet(String prepareQueueKey, Object task) {
        getScoredSortedSet(prepareQueueKey).remove(task);
    }


}
