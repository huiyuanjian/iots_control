package app.utils.config;

import app.entity.Message;
import app.entity.common.CommonConfig;
import app.utils.fileIO.FileUtils;
import app.utils.mqtt.MessageManage;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ConfigUtils {


    /**
     * 控制接口服务的服务状态变化关系
     */
    public ControlServerStatusEnum change(boolean b){
        switch (ControlConfig.SERVERSTATUS){
            case START_UP:
                ControlConfig.SERVERSTATUS = ControlServerStatusEnum.READ_LOCAL_CONFIGURATION;
                break;
            case READ_LOCAL_CONFIGURATION:
                if(b){
                    ControlConfig.SERVERSTATUS = ControlServerStatusEnum.SERVICE_START;
                }else {
                    ControlConfig.SERVERSTATUS = ControlServerStatusEnum.FAILED_READ_LOCAL_CONFIGURATION;
                }
                break;
            case FAILED_READ_LOCAL_CONFIGURATION:
                ControlConfig.SERVERSTATUS = ControlServerStatusEnum.WAITING_FOR_WEB_CONNECTION;
                break;
            case WAITING_FOR_WEB_CONNECTION:
                if(b){
                    ControlConfig.SERVERSTATUS = ControlServerStatusEnum.CONNECT_TO_WEB;
                }
                break;
            case CONNECT_TO_WEB:
                ControlConfig.SERVERSTATUS = ControlServerStatusEnum.WAITING_FOR_DISPATCH_CONFIGURATION;
                break;
            case WAITING_FOR_DISPATCH_CONFIGURATION:
                if(b){
                    ControlConfig.SERVERSTATUS = ControlServerStatusEnum.CONFIGURATION_SUCCESS;
                }
                break;
            case CONFIGURATION_SUCCESS:
                if (b){
                    ControlConfig.SERVERSTATUS = ControlServerStatusEnum.SERVICE_START;
                }else {
                    ControlConfig.SERVERSTATUS = ControlServerStatusEnum.SERVICE_FAILED;
                }
                break;
            default:
                break;
        }
        return ControlConfig.SERVERSTATUS;
    }

    /**
     * 移除超时的元素
     */
    private Map<String, Message> outTime(Map<String, Message> messageMap){
        Map<String, Message> map = messageMap;
        if (!messageMap.isEmpty()){
            for (Message msg : messageMap.values()){
                // 获取当前时间戳
                Long current_time = System.currentTimeMillis();
                // 获得消息的时间戳
                Long create_time = msg.getCreate_time();
                if(current_time - create_time > 600000){ // 10 分钟
                    map.remove(msg.getMsg_id());
                }
            }
        }
        return map;
    }

    /**
     * 启用定时任务
     */
    public void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MessageManage.PUBLISH_MSG_MAP2 = outTime(MessageManage.PUBLISH_MSG_MAP2);
            }
        },1000);
    }

    /**
     * 更新config
     */
    public void setConfig(ConfigInfo configInfo){

        ControlConfig. SERVERID = configInfo.getServerid();// 服务id
        ControlConfig.SEND_PALPITATE_INTERVAL = configInfo.getSend_palpitate_interval();// 心跳topic
        ControlConfig.SERVERTYPE = configInfo.getServerType();// 服务类型
        ControlConfig.MQTT_ANSWER_COLLECT_TOPIC = configInfo.getMqtt_answer_collect_topic();// collect应答topic
        ControlConfig.MQTT_ANSWER_WEB_TOPIC = configInfo.getMqtt_answer_web_topic();// web应答topic

        ControlConfig.MQTT_CONFIG_TOPIC = configInfo.getMqtt_config_topic();// 配置topic
        ControlConfig.MQTT_CTRL_TOPIC = configInfo.getMqtt_ctrl_topic();// 控制topic
        ControlConfig.MQTT_REGIST_TOPIC = configInfo.getMqtt_regist_topic();// 注册topic
        ControlConfig.COLLECT_PROXY_MAP = configInfo.getCollect_proxy_map();// 管理的物联网接口mac和采集代理mac
        ControlConfig.IDENTIFYING_CODE = configInfo.getIdentifying_code();// 身份识别码

        ControlConfig.SERVERNAME = configInfo.getServer_name();// 服务名称
        ControlConfig.SERVERREMARK = configInfo.getServer_remark();//服务说明
        // 持久化配置信息到文件中
        FileUtils fileUtils = new FileUtils();
        fileUtils.saveLoginInfo(getConfigFromControl());
    }

    /**
     * 更新config
     */
    public void setCommonConfig(CommonConfig commonConfig){

        FileUtils fileUtils = new FileUtils();
        ConfigInfo configInfo = fileUtils.readLoginInfo();
        if(configInfo != null){
            configInfo.setCollect_proxy_map(commonConfig.getCollect_proxy_map());// 管理的物联网接口mac和采集代理mac
            configInfo.setIdentifying_code(commonConfig.getIdentifying_code());// 身份识别码
            configInfo.setSend_palpitate_interval(commonConfig.getSend_palpitate_interval());// 心跳间隔
            configInfo.setServerid(commonConfig.getServer_id());// 服务id
            configInfo.setServerType(commonConfig.getServer_type());// 服务类型

            configInfo.setServer_remark(commonConfig.getServer_remark());// 服务说明
            configInfo.setServer_name(commonConfig.getServer_name());// 服务名称

            // 更新
            setConfig(configInfo);
        } else {
            ControlConfig.SEND_PALPITATE_INTERVAL = commonConfig.getSend_palpitate_interval();
            ControlConfig.IDENTIFYING_CODE = commonConfig.getIdentifying_code();
            ControlConfig.COLLECT_PROXY_MAP = commonConfig.getCollect_proxy_map();
            ControlConfig.SERVERID = commonConfig.getServer_id();
            ControlConfig.SERVERTYPE = commonConfig.getServer_type();

            ControlConfig.SERVERREMARK = commonConfig.getServer_remark();
            ControlConfig.SERVERNAME = commonConfig.getServer_name();

            // 持久化
            fileUtils.saveLoginInfo(getConfigFromControl());
        }
    }

    /**
     * 从内存中读取配置信息
     */
    public ConfigInfo getConfigFromControl(){
        ConfigInfo configInfo = new ConfigInfo();

        configInfo.setMqtt_status_topic(ControlConfig.MQTT_STATUS_TOPIC);
        configInfo.setMqtt_ctrl_topic(ControlConfig.MQTT_CTRL_TOPIC);
        configInfo.setMqtt_answer_collect_topic(ControlConfig.MQTT_ANSWER_COLLECT_TOPIC);
        configInfo.setCollect_proxy_map(ControlConfig.COLLECT_PROXY_MAP);
        configInfo.setIdentifying_code(ControlConfig.IDENTIFYING_CODE);

        configInfo.setServerType(ControlConfig.SERVERTYPE);
        configInfo.setServerid(ControlConfig.SERVERID);
        configInfo.setSend_palpitate_interval(ControlConfig.SEND_PALPITATE_INTERVAL);
        configInfo.setMacaddress(ControlConfig.MACADDRESS);
        configInfo.setMqtt_answer_web_topic(ControlConfig.MQTT_ANSWER_WEB_TOPIC);

        configInfo.setMqtt_config_topic(ControlConfig.MQTT_CONFIG_TOPIC);
        configInfo.setMqtt_regist_topic(ControlConfig.MQTT_REGIST_TOPIC);
        configInfo.setServer_name(ControlConfig.SERVERNAME);
        configInfo.setServer_remark(ControlConfig.SERVERREMARK);
        configInfo.setMap_logs(ControlConfig.MAP_LOGS);

        return configInfo;
    }
}
