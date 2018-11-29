package app.utils.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * 发布消息的回调类
 *
 * 必须实现MqttCallback的接口并实现对应的相关接口方法
 *      ◦CallBack 类将实现 MqttCallBack。每个客户机标识都需要一个回调实例。在此示例中，构造函数传递客户机标识以另存为实例数据。在回调中，将它用来标识已经启动了该回调的哪个实例。
 *  ◦必须在回调类中实现三个方法：
 *
 *  public void messageArrived(MqttTopic topic, MqttMessage message)
 *  接收已经预订的发布。
 *
 *  public void connectionLost(Throwable cause)
 *  在断开连接时调用。
 *
 *  public void deliveryComplete(MqttDeliveryToken token))
 *      接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用。
 *  ◦由 MqttClient.connect 激活此回调。
 *
 * *********************  原先实现的接口是 MqttCallback  *********************
 */
@Slf4j
public class PushCallback implements MqttCallbackExtended {

    /**
     * 断线重连
     * @param cause
     */
    public void connectionLost(Throwable cause) {
        log.error("连接断开，正在重试连接。。。");
//        MqttSingle single = MqttSingle.getInstance();
//        single.reLink();
//        log.error("已经重新连接。。。");
    }

    /**
     * 订阅消息
     * @param topic
     * @param mqttMessage
     * @throws Exception
     */
    @Override
    public void messageArrived( String topic, MqttMessage mqttMessage ) throws Exception {
        // subscribe后得到的消息会执行到这里面
        log.info("接收消息主题:{}",topic);
        log.info("接收消息Qos:{}",mqttMessage.getQos());
        log.info("接收消息内容:{}",new String(mqttMessage.getPayload()));

        // 业务处理
        SubscribHandler.handler(topic,mqttMessage);
    }

    /**
     * 消息订阅
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
        // subscribe后得到的消息会执行到这里面
        log.info("接收消息主题:{}",topic.getName());
        log.info("接收消息Qos:{}",message.getQos());
        log.info("接收消息内容:{}",new String(message.getPayload()));
    }

    /**
     * 发布消息的回调
     * @param iMqttDeliveryToken
     */
    @Override
    public void deliveryComplete( IMqttDeliveryToken iMqttDeliveryToken ) {
        // publish后会执行到这里
        log.info("deliveryComplete: {}",iMqttDeliveryToken.isComplete());
    }

    @Override
    public void connectComplete(boolean b, String s) {
        log.info("回调 b 的值：{}",b);
        log.info("回调 s 的值：{}",s);
        // 订阅消息
        TopicSubscrib topicSubscrib = new TopicSubscrib();
        topicSubscrib.run();
    }
}
