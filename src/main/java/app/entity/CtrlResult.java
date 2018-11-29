package app.entity;

import lombok.Data;

/**
 * 控制消息的结果实体类
 */
@Data
public class CtrlResult {

    // 时间戳-键
    private String key;

    // 是否有了结果
    private boolean answer = false;

    // 是否执行成功
    private boolean result = false;

    // 变更时间
    private long updatetime = System.currentTimeMillis();
}
