package cn.bugstack.infrastructure.redis;


import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;

import java.util.List;

public interface IRedisService {

    <T> T evalCachedScript(String scriptText, RScript.ReturnType returnType,
                                  List<Object> keys, Object... args);

    <T> RScoredSortedSet<T> getScoreSortedSet(String key);

    <T> void offerScoreSortedSet(String key, T value, double score);

    RScript getScript();

    void deleteFromScoreSortedSet(String prepareQueueKey, Object task);

    double getScoreByObject(String prepareQueueKey, Object task);

    RMap<Object, Object> getMap(String key);

    <T> void putToMap(String key1, String key2, T value);

    Object getFromMap(String key1, String key2);

    void removeFromMap(String key1, String key2);

}
