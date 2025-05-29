package cn.bugstack.test.trigger.rpc;

import cn.bugstack.api.ITimeoutCenterService;
import cn.bugstack.api.dto.AddTimeoutTaskDTO;
import cn.bugstack.api.dto.CommitTimeoutTaskDTO;
import cn.bugstack.api.dto.RollbackTimeoutTaskDTO;
import cn.bugstack.api.response.WebResponse;
import cn.bugstack.api.vo.TimeoutTaskVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.DubboShutdownHook;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private final String bizType = "group_buy_order";
    private final String bizId = "0001";
    private final Map<String, String> task = new HashMap<>();

    @BeforeEach
    public void beforeAll() {
        task.put("orderId", "123456");
        task.put("orderName", "test");
    }

    @Test
    public void testOffer() throws IOException {
        timeoutCenterService.offerTimeoutTask(AddTimeoutTaskDTO.builder()
                        .actionTime(10L)
                        .bizType(bizType)
                        .bizId(bizId)
                        .task(task)
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
                .task(task)
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
                .task(task)
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
                        .task(task)
                        .build();
                timeoutCenterService.commitedTimeoutTask(commitTimeoutTaskDTO);
                break;
            } catch (Exception e) {
                RollbackTimeoutTaskDTO rollbackTimeoutTaskDTO = RollbackTimeoutTaskDTO.builder()
                        .bizType(bizType)
                        .bizId(bizId)
                        .task(task)
                        .build();
                timeoutCenterService.rollbackTimeoutTask(rollbackTimeoutTaskDTO);
            } finally {
                Thread.sleep(1000);
            }
        }
    }
}
