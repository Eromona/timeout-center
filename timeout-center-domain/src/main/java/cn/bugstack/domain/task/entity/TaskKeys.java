package cn.bugstack.domain.task.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskKeys {

    private String storeQueueKey;

    private String prepareQueueKey;

    private String deadQueueKey;

    private String hashKey;

}
