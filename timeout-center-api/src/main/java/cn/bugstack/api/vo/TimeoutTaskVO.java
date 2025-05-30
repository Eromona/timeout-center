package cn.bugstack.api.vo;

import cn.bugstack.type.constant.Constant;
import cn.bugstack.type.utils.StrUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeoutTaskVO implements Serializable {
    private static final long serialVersionUID = -1;

    private Object task;

    public static String getStoreQueueKey(String bizType, String bizId) {
        return StrUtils.combine(Constant.STORE_QUEUE_PREFIX, bizType, "_", bizId);
    }

    public static String getPrepareQueueKey(String bizType, String bizId) {
        return StrUtils.combine(Constant.PREPARE_QUEUE_PREFIX, bizType, "_", bizId);
    }

    public static String getDeadQueueKey(String bizType, String bizId) {
        return StrUtils.combine(Constant.DEAD_QUEUE_PREFIX, bizType, "_", bizId);
    }

    public static String getHashKey(String bizType, String bizId) {
        return StrUtils.combine(Constant.HASH_PREFIX, bizType, "_", bizId);
    }

}
