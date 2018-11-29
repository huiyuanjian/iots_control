package app.logger.scheduling;

import app.entity.MesBody;
import app.entity.Message;
import app.entity.log.ServerLog;
import app.logger.LogManager;
import app.logger.utils.DateUtil;
import app.logger.utils.RedisUtils;
import app.utils.config.ControlConfig;
import app.utils.mqtt.MqttUtils;
import app.utils.templates.MessageTemplates;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zcy
 * @date 2018/10/2510:30
 */
@Slf4j
@Component
@EnableScheduling
public class LoggTask {

    @Value("${logging.path}")
    private String logPath;

    @Value("${module.name}")
    private String serverName;

    @Value("${redis-two.host}")
    private String redisHost;

    @Value("${redis-two.port}")
    private int redisPort;

    @Value("${redis-two.password}")
    private String redisPwd;

    @Resource
    private app.logger.LogUtil LogUtil;

    @Resource
    private app.logger.UploadService UploadService;

    /**
     * 日志文件定时上传到fastdfs，上传成功后通过mqtt发布给broker2，让web订阅入库
     */
    @Scheduled(cron = "${schedules.upload}")
    public void runJob_log() {
        File path = new File(logPath);
        List<String> listPaths= new ArrayList<String>();
        // 根据目录 查找log文件
        listPaths = LogUtil.getChild(path,listPaths,"--Already",true);
        // 存放对应关系
        String key = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Map<String,String> map = ControlConfig.MAP_LOGS.get(key);
        if (map != null && !map.isEmpty()) {
            map = new HashMap<>();
        }
        if (listPaths != null) {
            for (String file:listPaths) {
                log.info("正在上传的文件是：{}",file);
                try {
                    //上传log到 fastdfs 服务器
                    String url = UploadService.upload(file);
                    //将添加成功的 进行名称修改 标志 已经上传过  "--Already" 标识
                    LogUtil.reanameFile(file,"--Already");
                    log.info("上传log到 fastdfs 服务器,访问路径为：{}",url);
                    String file_name =
                            map.put(file,url);
                } catch (IOException e) {
                    log.error("日志文件上传失败，失败的日志文件为：{}",file);
                    log.error("异常信息是：{}",e.getMessage());
                }
            }
        }
        // 将信息放回系统缓存中
        ControlConfig.MAP_LOGS.put(key,map);
        // 发布
        if (ControlConfig.MAP_LOGS != null && !ControlConfig.MAP_LOGS.isEmpty()) {
            send(getServerLog(ControlConfig.MAP_LOGS));
        }
    }

    /**
     * 生成用于上传的日志信息
     */
    private ServerLog getServerLog(Map<String,Map<String,String>> map){
        ServerLog serverLog = new ServerLog();
        serverLog.setServer_id(ControlConfig.SERVERID);
        serverLog.setServer_mac(ControlConfig.MACADDRESS);
        serverLog.setServer_type(ControlConfig.SERVERTYPE);
        serverLog.setServer_name(ControlConfig.SERVERNAME);
        serverLog.setUpload_time(new Date());
        serverLog.setMap(map);
        return serverLog;
    }

    /**
     * 发布日志信息
     */
    private void send(ServerLog serverLog){
        // 发布主题
        String topic = "LOG/" + ControlConfig.SERVERTYPE + "/" + ControlConfig.MACADDRESS;
        // 消息模版
        MessageTemplates templates = new MessageTemplates();
        // 发布工具类
        MqttUtils mqttUtils = new MqttUtils();
        boolean upload = mqttUtils.publish(topic,templates.getMqttMessage(templates.getMessage(7, templates.getMesBody(0,serverLog))));
        // 如果上传成功了，则清空容器
        if (upload) {
            ControlConfig.MAP_LOGS.clear();
        }
    }


    /**
     * log日志  fastdfs 成功后 删除
     * zcy
     * 20181025
     */
    @Scheduled(cron = "${schedules.delete}") // 每秒调用一次
    public void runJob_deletelog() {
        List<String> listPaths= new ArrayList<String>();
        File file= new File(logPath);
        //根据目录 查找log文件
        listPaths = LogUtil.getChild(file,listPaths,"--Already",false);
        if (!CollectionUtils.isEmpty(listPaths)){
            for (String path:listPaths) {
                //将删除名称为标志 已经上传过  "--Already" 标识
                LogUtil.deleteFile(path);
            }
        }
    }

    /**
     * 发布日志的url
     * fileName 文件原来的名字
     * url 文件上传后的访问路径
     */
    private void publishLogUrl(String fileName,String url){
        List<String> list = new ArrayList<>();
        list.add(fileName + "<--LOG-->" +url);
        publishLogUrl(list);
    }

    /**
     * 发布日志的url
     * list 中存放 fileName<--LOG-->url
     * fileName 文件原来的名字
     * url 文件上传后的访问路径
     */
    private void publishLogUrl(List<String> list){
        if (list.isEmpty()) return;

        // map不为空时
        // 1 构建MesBody
        MesBody body = new MesBody();
        body.setSub_type(0);
        body.setContext(list);

        // 2 构建Message
        Message message = new Message();
        message.setMsg_id(ControlConfig.SERVERTYPE+System.currentTimeMillis());
        message.setSource_mac(ControlConfig.MACADDRESS);
        message.setSource_type(ControlConfig.SERVERTYPE);
        message.setCreate_time(System.currentTimeMillis());
        message.setCallback_id("");
        message.setMsg_type(7);// 日志
        message.setLicense("");// TODO 授权认证信息
        message.setBody(body);

        // 3 构建MqttMessage
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(1);
        mqttMessage.setRetained(true);
        mqttMessage.setPayload((JSONObject.toJSONString(message)).getBytes());

        // 4 发布
        String topic = ControlConfig.SERVERTYPE + "/WEB/" + ControlConfig.MACADDRESS;
        MqttUtils mqttUtils = new MqttUtils();
        mqttUtils.publish(topic,mqttMessage);

        // 5 添加到发布管理容器中
        LogManager.FILEUPLOADURL_MAP.put(message.getMsg_id(),message);
    }
}
