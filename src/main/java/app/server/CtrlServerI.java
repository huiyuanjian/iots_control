package app.server;

import app.entity.CtrlInfo;

/**
 * 控制业务接口
 */
public interface CtrlServerI {

    /**
     * 验证控制信息是否有误
     */
    public boolean verification(CtrlInfo ctrlInfo);

    /**
     * 执行控制命令
     */
    public String change(CtrlInfo ctrlInfo);

    /**
     * 回调，查看结果
     */
    public String callback(String key);
}
