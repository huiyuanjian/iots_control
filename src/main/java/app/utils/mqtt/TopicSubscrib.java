package app.utils.mqtt;

import app.utils.config.ControlConfig;

/**
 * 主题订阅
 * @author 周西栋
 * @date
 * @param
 * @return
 */
public class TopicSubscrib {

    public void run(){
        MqttUtils mqttUtils = new MqttUtils();
        mqttUtils.subscribTopic(ControlConfig.MQTT_CONFIG_TOPIC);
        mqttUtils.subscribTopic(ControlConfig.MQTT_CTRL_TOPIC);
        mqttUtils.subscribTopic(ControlConfig.MQTT_ANSWER_COLLECT_TOPIC);
        mqttUtils.subscribTopic(ControlConfig.MQTT_ANSWER_WEB_TOPIC);
    }
}
