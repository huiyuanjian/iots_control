package app.entity.common;

import app.entity.proxy.DeviceInfo;
import app.entity.proxy.PackageInfo;
import app.entity.proxy.VarInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 通用的配置信息(用于下发服务配置信息) 实体类
 */
@Data
public class CommonConfig {

    /**
     * 服务id
     */
    private String server_id;

    /**
     * 服务的mac地址
     */
    private String server_mac;

    /**
     * 服务类型
     */
    private String server_type;

    /**
     * 服务名称
     */
    private String server_name;

    /**
     * 服务描述
     */
    private String server_remark;

    /**
     * 心跳间隔（单位是秒）默认心跳间隔是60秒
     */
    private int send_palpitate_interval = 60;

    /***************************************  collect  *************************************/

    /**
     * kafka 上的主题
     * 存放采集到的数据的topic
     */
    private String kafka_topic;

    /**
     * 本服务有权管理的采集代理
     * String 是mac地址
     */
    private List<String> mac_address_list;

    /***************************************  collect  *************************************/
    /***************************************   proxy   *************************************/

    /**
     * 域名
     */
    private String domain;

    /**
     * 设备容器
     */
    private List<DeviceInfo> device_list;

    /**
     * 变量容器
     */
    private List<VarInfo> var_list;

    /**
     * 数据包容器
     */
    private List<PackageInfo> pack_list;

    /***************************************   proxy   *************************************/
    /***************************************  control  *************************************/

    /**
     * 本服务有权管理的物联网接口服务的mac地址和物联网接口管辖下的采集代理服务的mac地址
     * String 是物联网接口服务的mac地址
     * List 是物联网接口管辖下的采集代理服务的mac地址
     */
    private Map<String,List<String>> collect_proxy_map;

    /**
     * 身份验证码
     */
    private String identifying_code;

    /***************************************  control  *************************************/
    /*************************************** distribute ************************************/

    /**
     * 分发接口需要转发kafka上哪些topic下的数据
     */
    private List<String> kafka_topic_distribute;

    /*************************************** distribute ************************************/







}
