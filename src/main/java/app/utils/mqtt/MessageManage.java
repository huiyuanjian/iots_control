package app.utils.mqtt;

import app.entity.CtrlResult;
import app.entity.Message;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 消息管理类
 */
public class MessageManage {

    /**
     * 控制消息管理容器
     */
    private static Map<String, String> TOPIC_MAP = new LinkedHashMap<>();

    /**
     * 控制消息管理容器
     */
    private static Map<String, Message> MESSAGE_MAP = new LinkedHashMap<>();

    /**
     * 控制消息的结果
     */
    private static Map<String, CtrlResult> RESULT_MAP = new LinkedHashMap <>();

    /**
     * 已经回调查询过并且返回结果了的控制消息
     * TODO 做一个过期删除，或者定时持久化
     */
    private static Map<String, CtrlResult> RESULT_CALLBACK_MAP = new LinkedHashMap <>();

    /**
     * 将Message放入管理器中
     */
    public static void add(String topic,Message message){
        // 保存topic
        TOPIC_MAP.put(message.getMsg_id(),topic);
        // 用于回答接口调用
        MESSAGE_MAP.put(message.getMsg_id(),message);
        // 用于mqtt消息应答
        PUBLISH_MSG_MAP2.put(message.getMsg_id(),message);
        // 执行结果
        put(message.getMsg_id());
    }

    /**
     * 应答成功则删除PUBLISH_MSG_MAP2中的消息
     */
    public static void answer(String key){
        PUBLISH_MSG_MAP2.remove(key);
        TOPIC_MAP.remove(key);
    }

    /**
     * 重新发送消息
     */
    public static void republish(String key) {
        // 获得原来的消息
        Message message = PUBLISH_MSG_MAP2.get(key);
        // 构造topic
        String topic = TOPIC_MAP.get(key);
        MqttUtils mqttUtils = new MqttUtils();
        // 构造mqtt消息
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(1);
        mqttMessage.setRetained(true);
        mqttMessage.setPayload((JSONObject.toJSONString(message)).getBytes());
        mqttUtils.publish(topic,mqttMessage);
    }

    /**
     * 将执行结果放入管理器中
     */
    public static void put(String key){
        CtrlResult ctrlResult = new CtrlResult();
        ctrlResult.setKey(key);
        RESULT_MAP.put(key,ctrlResult);
    }

    /**
     * 获得控制命令的执行结果
     */
    public static CtrlResult get(String key){
        return RESULT_MAP.get(key);
    }

    /**
     * 变更执行结果
     */
    public static boolean change(String key, boolean result){
        try {
            CtrlResult ctrlResult = RESULT_MAP.get(key);
            ctrlResult.setAnswer(true);
            ctrlResult.setResult(result);
            RESULT_MAP.put(key,ctrlResult);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 变更执行结果
     */
    public static boolean change(Message message){
        if(message != null && message.getBody() != null && message.getBody().getSub_type() != null){
            // 控制消息的子消息类型 0 是成功  1 是失败
            boolean result = message.getBody().getSub_type() == 0 ? true : false;
            try {
                CtrlResult ctrlResult = RESULT_MAP.get(message.getCallback_id());
                ctrlResult.setAnswer(true);
                ctrlResult.setResult(result);
                RESULT_MAP.put(message.getCallback_id(),ctrlResult);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 移除
     */
    public static void remove(String key){
        // 将执行结果转移
        RESULT_CALLBACK_MAP.put(key,RESULT_MAP.get(key));
        // 删除执行结果
        RESULT_MAP.remove(key);
    }

    /**
     * 发布的消息
     * String 是消息的msg_id
     * Message 是消息体信息
     * 如果发布的是应答消息，则需要将所应答的消息信息从SUBSCRIB_MSG_MAP2中移除
     * 发布消息超过10分钟则自动删除
     */
    public static Map<String, Message> PUBLISH_MSG_MAP2 = new LinkedHashMap();

    /**
     * 移除超时的元素
     */
    private static Map<String, Message> outTime(Map<String, Message> messageMap){
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

    static {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PUBLISH_MSG_MAP2 = outTime(PUBLISH_MSG_MAP2);
            }
        },1000);
    }
}
