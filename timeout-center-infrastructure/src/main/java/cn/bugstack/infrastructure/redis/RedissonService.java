package cn.bugstack.infrastructure.redis;


import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service("redissonService")
public class RedissonService implements IRedisService {

    @Resource
    private RedissonClient redissonClient;

    private final Map<String, String> scriptShaCache = new ConcurrentHashMap<>();

    public <T> T evalCachedScript(String scriptText, RScript.ReturnType returnType,
                                  List<Object> keys, Object... args) {
        RScript script = redissonClient.getScript();

        String sha1 = scriptShaCache.computeIfAbsent(scriptText, k -> {
            String loadedSha = script.scriptLoad(k);
            log.info("loaded sha1 : {}", loadedSha);
            return loadedSha;
        });
        try {
            return script.evalSha(RScript.Mode.READ_WRITE, sha1, returnType, keys, args);
        } catch (RedisException ex) {
            // Redis 重启可能导致 SHA1 丢失，重新加载
            log.warn("Script SHA1 not found, reloading script...");
            String loadedSha = script.scriptLoad(scriptText);
            scriptShaCache.put(scriptText, loadedSha);
            return script.evalSha(RScript.Mode.READ_WRITE, loadedSha, returnType, keys, args);
        }
    }


    @Override
    public <T> RScoredSortedSet<T> getScoreSortedSet(String key) {
        return redissonClient.getScoredSortedSet(key);
    }


    @Override
    public <T> void offerScoreSortedSet(String key, T value, double score) {
        RScoredSortedSet<Object> scoredSortedSet = getScoreSortedSet(key);
        scoredSortedSet.add(score, value);
    }

    @Override
    public RScript getScript() {
        return redissonClient.getScript();
    }

    @Override
    public void deleteFromScoreSortedSet(String prepareQueueKey, Object task) {
        getScoreSortedSet(prepareQueueKey).remove(task);
    }

    @Override
    public double getScoreByObject(String prepareQueueKey, Object task) {
        return getScoreSortedSet(prepareQueueKey).getScore(task);
    }

    @Override
    public RMap<Object, Object> getMap(String key) {
        return redissonClient.getMap(key);
    }

    @Override
    public <T> void putToMap(String key1, String key2, T value) {
        getMap(key1).put(key2, value);
    }

    @Override
    public Object getFromMap(String key1, String key2) {
        return getMap(key1).get(key2);
    }

    @Override
    public void removeFromMap(String key1, String key2) {
        getMap(key1).remove(key2);
    }
}
