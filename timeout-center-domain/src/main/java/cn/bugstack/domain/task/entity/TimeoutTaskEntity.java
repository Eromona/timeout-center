package cn.bugstack.domain.task.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeoutTaskEntity {

    private double actionTime;

    private String UUID;

    private TaskKeys taskKeys;

    private Object task;
}
