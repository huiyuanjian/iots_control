package app.logger;

import app.entity.Message;
import app.utils.config.ControlConfig;
import app.utils.mqtt.MqttUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 日志相关管理类
 */
@Slf4j
public class LogManager {

    /**
     * 日志上传的url管理容器
     */
    public static Map<String, Message> FILEUPLOADURL_MAP = new LinkedHashMap<>();


    /**
     * 循环遍历 FILEUPLOADURL_MAP ，如果一定时间内没有发布成功，则再次发布
     */
    private static void checkUploadResult(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 发布主题
                String topic = ControlConfig.SERVERTYPE + "/WEB/" + ControlConfig.MACADDRESS;
                // mqtt消息
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(1);
                mqttMessage.setRetained(true);
                // mqtt工具类
                MqttUtils mqttUtils = new MqttUtils();
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("日志线程睡眠异常，异常信息为：{}",e.getMessage());
                    }
                    // 管理容器中有值时
                    if(!FILEUPLOADURL_MAP.isEmpty()){
                        Map<String, Message> map = FILEUPLOADURL_MAP;
                        for(Message message : map.values()){
                            // 超过十秒时
                            if (System.currentTimeMillis() - message.getCreate_time() > 10000){
                                mqttMessage.setPayload((JSONObject.toJSONString(message)).getBytes());
                                mqttUtils.publish(topic,mqttMessage);
                            }
                        }
                    }
                }
            }
        },"日志线程").start();
    }

    // 静态代码块
    static {
        // 监听
        checkUploadResult();
    }
}
