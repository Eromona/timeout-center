package cn.bugstack.infrastructure.redis;


import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;

public interface IRedisService {

    <T> RScoredSortedSet<T> getScoreSortedSet(String key);

    <T> void offerScoreSortedSet(String key, T value, double score);

    RScript getScript();

    void deleteFromScoreSortedSet(String prepareQueueKey, Object task);

    double getScoreByObject(String prepareQueueKey, Object task);
}
