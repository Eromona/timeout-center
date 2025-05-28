package cn.bugstack.test.trigger.rpc;

import cn.bugstack.api.ITimeoutCenterService;
import cn.bugstack.api.response.WebResponse;
import cn.bugstack.api.vo.TimeoutTaskVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TimeOutCenterServiceTest {

    @DubboReference
    private ITimeoutCenterService timeoutCenterService;

    @Test
    public void test() {
        WebResponse<TimeoutTaskVO> timeoutTaskVOWebResponse = timeoutCenterService.queryTimeoutTask("", "");
        log.info(timeoutTaskVOWebResponse.toString());
    }

}
