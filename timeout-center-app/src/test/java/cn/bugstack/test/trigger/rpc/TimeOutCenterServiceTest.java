package cn.bugstack.test.trigger.rpc;

import cn.bugstack.api.ITimeoutCenterService;
import cn.bugstack.api.dto.AddTimeoutTaskDTO;
import cn.bugstack.api.dto.CommitTimeoutTaskDTO;
import cn.bugstack.api.dto.RollbackTimeoutTaskDTO;
import cn.bugstack.api.response.WebResponse;
import cn.bugstack.api.vo.TimeoutTaskVO;
import cn.bugstack.infrastructure.redis.IRedisService;
import cn.bugstack.infrastructure.service.IUUIDService;
import cn.bugstack.type.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.DubboShutdownHook;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@SpringBootTest
public class TimeOutCenterServiceTest {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown Hook - Gracefully closing Dubbo");
            DubboShutdownHook.getDubboShutdownHook().run();
        }));
    }

    @DubboReference
    private ITimeoutCenterService timeoutCenterService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IUUIDService UUIDService;

    private static final String bizType = "group_buy_order";

    private static final String bizId = "0001";

    private static final List<Map<String, String>> tasks = new ArrayList<>();


    private static final List<String> UUIDS = new ArrayList<>();

    private static final int threadCount = 200;

    private static final int requestCount = 100000;

    @BeforeAll
    public static void beforeAll() {
        for (int i = 0; i < requestCount; i++) {
            Map<String, String> task = new HashMap<>();
            String base = StrUtils.combine("order_", i, "_");
            String longText = StrUtils.repeat("X", 10);

            task.put("orderId", base + UUID.randomUUID());
            task.put("orderName", longText);         // 长文本字段
            task.put("orderDesc", longText);         // 再加几个大字段
            task.put("extra", longText);
            tasks.add(task);

            UUIDS.add("UUID_" + i);
        }
    }

    @Test
    public void testOffer() {
        timeoutCenterService.offerTimeoutTask(AddTimeoutTaskDTO.builder()
                        .actionTime(10L)
                        .bizType(bizType)
                        .bizId(bizId)
                        .UUID(UUIDS.get(0))
                        .task(tasks.get(0))
                .build());
    }

    @Test
    public void testPrepare() {
        WebResponse<List<TimeoutTaskVO>> webResponse = timeoutCenterService.prepare(bizType, bizId);
        List<TimeoutTaskVO> tasks = webResponse.getData();
        tasks.forEach(System.out::println);
    }

    @Test
    public void testCommit() {
        CommitTimeoutTaskDTO commitTimeoutTaskDTO = CommitTimeoutTaskDTO.builder()
                .bizType(bizType)
                .bizId(bizId)
                .UUID(UUIDS.get(0))
                .build();
        timeoutCenterService.commitedTimeoutTask(commitTimeoutTaskDTO);
    }

    @Test
    public void testCommon() throws InterruptedException {
        int count = 0;

        timeoutCenterService.offerTimeoutTask(AddTimeoutTaskDTO.builder()
                .actionTime(1L)
                .bizType(bizType)
                .bizId(bizId)
                .task(tasks.get(0))
                .build());

        Thread.sleep(1200);

        WebResponse<List<TimeoutTaskVO>> webResponse = timeoutCenterService.prepare(bizType, bizId);
        webResponse.getData().forEach(System.out::println);
        while (true) {
            log.info("retry:{}", count++);
            try {
                CommitTimeoutTaskDTO commitTimeoutTaskDTO = CommitTimeoutTaskDTO.builder()
                        .bizType(bizType)
                        .bizId(bizId)
                        .task(tasks.get(0))
                        .build();
                timeoutCenterService.commitedTimeoutTask(commitTimeoutTaskDTO);
                break;
            } catch (Exception e) {
                RollbackTimeoutTaskDTO rollbackTimeoutTaskDTO = RollbackTimeoutTaskDTO.builder()
                        .bizType(bizType)
                        .bizId(bizId)
                        .task(tasks.get(0))
                        .build();
                timeoutCenterService.rollbackTimeoutTask(rollbackTimeoutTaskDTO);
            } finally {
                Thread.sleep(1000);
            }
        }
    }

    @Test
    public void qpsTest() throws InterruptedException {

        ExecutorService executor = new ThreadPoolExecutor(threadCount, 2 * threadCount, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        CountDownLatch latch = new CountDownLatch(requestCount);

        long start = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    timeoutCenterService.offerTimeoutTask(AddTimeoutTaskDTO.builder()
                            .actionTime(10L)
                            .bizType(bizType)
                            .bizId(bizId)
                            .task(tasks.get(finalI))
                            .build());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("QPS: " + (requestCount * 1000.0 / (end - start)));

        executor.shutdown();
    }

    @Test
    public void qpsTestWithRedisson() throws InterruptedException {
        ExecutorService executor = new ThreadPoolExecutor(threadCount, 2 * threadCount, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        CountDownLatch latch = new CountDownLatch(requestCount);

        long start = System.currentTimeMillis();

        RBlockingDeque<Object> blockingQueue = redissonClient.getBlockingDeque("test_blocking_queue");
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);


        for (int i = 0; i < requestCount; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    delayedQueue.offer(tasks.get(finalI), 10, TimeUnit.SECONDS);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("Redisson QPS: " + (requestCount * 1000.0 / (end - start)));

        executor.shutdown();
    }

}
