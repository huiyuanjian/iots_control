package app.entity;

import lombok.Data;

/**
 * 控制消息 实体类
 */
@Data
public class CtrlInfo {

    /**
     * 采集代理的mac地址
     */
    private String proxy_mac = "";

    /**
     * 物联网接口mac地址
     */
    private String collect_mac = "";

    /**
     * 随机数（配合固定码 验证用）
     */
    private String random = "";

    /**
     * 认证码（验证用）
     */
    private String key = "";

    /**
     * 变量值的类型
     * string 表示 字符串类型
     * int 表示 整型
     * double 表示 浮点型
     */
    private String data_type;

    /**
     * 生成时的时间戳
     */
    private long createtime = System.currentTimeMillis();

    /**
     * 控制命令
     * 格式如：http://域名/IOServer标识/分组标识/设备标识/变量标识/写变量值_时间戳_超时时间_尝试次数
     */
    private String ctrl_info = "";

}
