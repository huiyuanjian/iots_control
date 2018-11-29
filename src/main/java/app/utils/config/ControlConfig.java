/**   
 * Copyright © 2018 eSunny Info. Tech Ltd. All rights reserved.
 * 
 * @Package: com.htby.bksw.iots_collect.utils.config 
 * @author: zhouxidong   
 * @date: 2018年5月27日 下午2:33:31 
 */
package app.utils.config;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @ClassName: IotsConfig 
 * @Description: 关于控制接口服务的配置
 * @author: zhouxidong
 * @date: 2018年5月27日 下午2:33:31  
 */
@Slf4j
public class ControlConfig {
	
	// 防止新建对象
    private ControlConfig(){};

	// 控制接口服务的服务状态
	public static ControlServerStatusEnum SERVERSTATUS = ControlServerStatusEnum.START_UP;

	/**
	 * 服务id
	 */
	public static String SERVERID = null;

	/**
	 * 服务名称
	 */
	public static String SERVERNAME = null;

	/**
	 * 心跳间隔（单位是秒）
	 * 默认心跳间隔是60秒
	 */
	public static int SEND_PALPITATE_INTERVAL = 60;
	
	/**
	 * 服务类型
	 */
	public static String SERVERTYPE = "CONTROL";

	/**
	 * mac地址
	 */
	public static String MACADDRESS = null;

	/**
	 * 服务说明
	 */
	public static String SERVERREMARK = null;

	/**
	 * 控制的物联网接口mac地址及物联网接口管理的采集代理mac地址集合
	 */
	public static Map<String , List<String>> COLLECT_PROXY_MAP = new HashMap<>();

	/**
	 * mqtt 注册 主题 broker2
	 */
	public static String MQTT_REGIST_TOPIC = null;

	/**
	 * mqtt 配置 主题 broker2
	 */
	public static String MQTT_CONFIG_TOPIC = null;

	/**
	 * mqtt 控制 主题 broker2
	 */
	public static String MQTT_CTRL_TOPIC = null;

	/**
	 * mqtt web应答 主题 broker2
	 */
	public static String MQTT_ANSWER_WEB_TOPIC = null;

	/**
	 * mqtt collect应答 主题 broker2
	 */
	public static String MQTT_ANSWER_COLLECT_TOPIC = null;

	/**
	 * mqtt 服务状态 主题 broker2
	 */
	public static String MQTT_STATUS_TOPIC = null;

	/**
	 * 身份验证码
	 */
	public static String IDENTIFYING_CODE = null;

	/**
	 * 存放上传后的日志访问路径
	 */
	public static Map<String,Map<String,String>> MAP_LOGS = new HashMap<>();

}
