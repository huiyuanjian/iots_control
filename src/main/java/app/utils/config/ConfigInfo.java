package app.utils.config;

import app.utils.sys.SysUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

/**
 * 控制接口配置信息实体类(用于持久化数据和服务间的通讯)
 */
@Data
@Slf4j
public class ConfigInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 服务id（数据库中的id）
    private String serverid;

    // 服务名称
    private String server_name;

    // 服务类型（字母全大写）
    private String serverType = "CONTROL";

    // 服务的mac地址（用于topic）
    private String macaddress = null;

    /**
     * 心跳间隔（单位是秒）
     * 默认心跳间隔是60秒
     */
    private int send_palpitate_interval = 60;

    // 服务说明
    private String server_remark;

    /**
     * 本服务有权管理的物联网接口服务的mac地址和物联网接口管辖下的采集代理服务的mac地址
     * String 是物联网接口服务的mac地址
     * List 是物联网接口管辖下的采集代理服务的mac地址
     */
    private Map<String,List<String>> collect_proxy_map = new HashMap<>();

    /**
     * mqtt 注册 主题 broker2
     */
    private String mqtt_regist_topic = null;

    /**
     * mqtt 配置 主题 broker2
     */
    private String mqtt_config_topic = null;

    /**
     * mqtt 控制 主题 broker2
     */
    private String mqtt_ctrl_topic = null;

    /**
     * mqtt web应答 主题 broker2
     */
    private String mqtt_answer_web_topic = null;

    /**
     * mqtt collect应答 主题 broker2
     */
    private String mqtt_answer_collect_topic = null;

    /**
     * mqtt 状态 主题 broker2
     */
    private String mqtt_status_topic = null;

    /**
     * 身份验证码
     */
    private String identifying_code = null;

    /**
     * 存放日志上传后的访问路径
     */
    private Map<String,Map<String,String>> map_logs = new HashMap<>();



}
