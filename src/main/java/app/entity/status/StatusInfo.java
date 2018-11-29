package app.entity.status;

import lombok.Data;

@Data
public class StatusInfo {

    /**
     * ip+port
     * 格式形如：192.192.192.192:8888
     */
    private String host = "";

    /**
     * mac地址
     */
    private String macAddress = "";

    /**
     * 服务的类型
     *
     * PROXY--采集代理
     * COLLECT--物联网接口
     * WEB--web管理端
     * CONTROL--控制接口服务
     * DISTRIBUTE--分发接口服务
     */
    private String serverType = "";

    /**
     * 服务名称(如果没有则赋一个""值)
     */
    private String serverName = "";

    /**
     * 时间戳
     */
    private long createtime = System.currentTimeMillis();

    /**
     * CPU 使用率(%)
     */
    private float cpu_usage = 0.0f;

    /**
     * CPU 温度(摄氏度)
     */
    private float cpu_temperature = 0.0f;

    /**
     * 内存 已使用(单位 M)
     */
    private int memory_used = 0;

    /**
     * 内存 空闲的(单位 M)
     */
    private int memory_free = 0;

    /**
     * 内存 使用率(%)
     */
    private float mem_usage = 0.0f;

    /**
     * 网络带宽
     */
    private float net_usage = 0.0f;

    /**
     * 磁盘IO
     */
    private float io_usage = 0.0f;

    /**
     * 说明
     */
    private String remark = "";
}
