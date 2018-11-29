package app.utils.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * mqtt 工具类
 * @author 周西栋
 * @date
 * @param
 * @return
 */
@Slf4j
public class MqttUtils {

    private MqttClient client = MqttSingle.getInstance().getMqttClient();

    /**
     * 发布消息 MqttMessage
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public boolean publish(String topic, MqttMessage msg){
        MqttTopic mqttTopic = client.getTopic(topic);
        try {
            mqttTopic.publish(msg);
            log.info("消息发送成功！");
            log.info("消息内容是：{}",new String(msg.getPayload()));
        } catch (MqttException e) {
            try {
                mqttTopic.publish(msg);
                log.info("消息发送成功！");
                log.info("消息内容是：{}",new String(msg.getPayload()));
            } catch (MqttException e1) {
                log.error("消息发送失败！");
                log.error("异常信息为：{}",e1.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * 订阅消息
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public void subscribTopic(String topic){
        //订阅消息
        int[] Qos  = {1};
        String[] array_topic = {topic};
        try {
            client.subscribe(array_topic, Qos);
        } catch (MqttException e) {
            log.error("订阅消息异常!");
            log.error("异常的topic有：{}",topic.toString());
            log.error("异常信息为：{}",e.getMessage());
        }
    }

    /**
     * 取消订阅
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public void unsubscribe(String topic){
        //取消订阅
        String[] array_topic = {topic};
        try {
            client.unsubscribe(array_topic);
        } catch (MqttException e) {
            log.error("取消订阅消息异常!");
            log.error("异常的topic有：{}",topic.toString());
            log.error("异常信息为：{}",e.getMessage());
        }
    }

}
