package cn.bugstack.infrastructure.redis;


import cn.bugstack.domain.task.entity.TimeoutTaskEntity;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;

import java.util.List;

public interface IRedisService {

    <T> RScoredSortedSet<T> getScoredSortedSet(String key);

    <T> void offerScoredSortedSet(String key, T value, double score);

    RScript getScript();

    void deleteFromScoreSortedSet(String prepareQueueKey, Object task);
}
