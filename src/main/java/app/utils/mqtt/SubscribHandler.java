package app.utils.mqtt;

import app.entity.MesBody;
import app.entity.Message;
import app.entity.common.CommonConfig;
import app.logger.LogManager;
import app.utils.config.ConfigUtils;
import app.utils.config.ControlConfig;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * topic 业务处理类
 * 也就是说，这里的订阅消息全部来自采集代理
 */
@Slf4j
@Component
public class SubscribHandler {

    /**
     * 定义一个阻塞长度为512长度的队列
     * 配置 主题 broker2
     *
     * add        增加一个元索                如果队列已满，则抛出一个IIIegaISlabEepeplian异常
     * remove     移除并返回队列头部的元素      如果队列为空，则抛出一个NoSuchElementException异常
     * element    返回队列头部的元素           如果队列为空，则抛出一个NoSuchElementException异常
     * offer      添加一个元素并返回true       如果队列已满，则返回false
     * poll       移除并返问队列头部的元素      如果队列为空，则返回null
     * peek       返回队列头部的元素           如果队列为空，则返回null
     * put        添加一个元素                如果队列满，则阻塞
     * take       移除并返回队列头部的元素      如果队列为空，则阻塞
     */
    private static BlockingQueue<MqttMessage> queue_config = new ArrayBlockingQueue<MqttMessage>(5);

    /**
     * 定义一个阻塞长度为512长度的队列
     * 控制 主题 broker2
     */
    private static BlockingQueue<MqttMessage> queue_ctrl = new ArrayBlockingQueue<MqttMessage>(512);

    /**
     * 定义一个阻塞长度为16长度的队列
     * 应答 主题 broker2
     */
    private static BlockingQueue<MqttMessage> queue_answer_web = new ArrayBlockingQueue<MqttMessage>(16);

    /**
     * 定义一个阻塞长度为128长度的队列
     * 应答 主题 broker2
     */
    private static BlockingQueue<MqttMessage> queue_answer_collect = new ArrayBlockingQueue<MqttMessage>(128);

    /**
     * 消息处理
     * topic 主题
     * qos 消息质量
     * msg 接收到的消息
     * topic 订阅主题
     * 详细说明参考 《14_mqtt通讯协议》
     */
    public static void handler(String topic,MqttMessage mqttMessage) throws InterruptedException {
        String array_top[] = topic.split("/");
        int len = array_top.length;
        String top = array_top[len > 0 ? (len - 1) : 0];
        switch (top){
            case "CONFIG" :  queue_config.put(mqttMessage); break;
            default: if (!ControlConfig.COLLECT_PROXY_MAP.isEmpty()) {
                switch (top){
                    case "CTRL" :  queue_ctrl.put(mqttMessage); break;
                    case "ANSWER" :  {
                        top = topic.split("/")[0];
                        switch (top) {
                            case "WEB" :  queue_answer_web.put(mqttMessage); break;
                            case "COLLECT" :  queue_answer_collect.put(mqttMessage); break;
                            default: break;
                        }
                    }; break;
                    default: break;
                }
            } break;
        }
    }

    /**
     * 处理配置信息
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    private static void handlerConfig(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        MqttMessage mqttMessage = queue_config.take();
                        Message message = JSONObject.parseObject(new String(mqttMessage.getPayload()),Message.class);
                        if(message != null && message.getBody() != null && message.getBody().getContext() != null){
                            // 回应消息
                            publish2(message);

                            // 得到配置信息
                            String conf_json = message.getBody().getContext().get(0);
                            CommonConfig commonConfig = JSONObject.parseObject(conf_json,CommonConfig.class);
                            if (commonConfig != null){
                                // 将配置信息赋值给ControlConfig,并持久化到本地
                                ConfigUtils configUtils = new ConfigUtils();
                                configUtils.setCommonConfig(commonConfig);
                            }
                        }
                    } catch (InterruptedException e) {
                        log.error("处理配置信息出现异常！异常信息是：{}",e.getMessage());
                    }
                }
            }
        },"配置线程").start();
    }

    /**
     * 处理控制信息
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    private static void handlerCtrl(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        MqttMessage mqttMessage = queue_ctrl.take();
                        Message message = JSONObject.parseObject(new String(mqttMessage.getPayload()),Message.class);
                        if(message != null){
                            // 回应消息
                            publish2(message);

                            // 将消息从MessageManage.MESSAGE_MAP中移除
                            boolean change = MessageManage.change(message);
                            if(change){
                                log.error("控制消息返回执行结果：{}",message.toString());
                            } else {
                                log.error("控制消息超时！消息的内容为：{}",message.toString());
                            }
                        }
                    } catch (InterruptedException e) {
                        log.error("处理控制信息出现异常！异常信息是：{}",e.getMessage());
                    }
                }
            }
        },"控制线程").start();
    }

    /**
     * 处理collect应答信息
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    private static void handlerAnswerCollect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        MqttMessage mqttMessage = queue_answer_collect.take();
                        Message message = JSONObject.parseObject(new String(mqttMessage.getPayload()),Message.class);
                        if(message != null && message.getBody() != null){
                            // 控制消息的子消息类型 0 是成功  1 是失败
                            int sub = message.getBody().getSub_type();
                            if(sub == 0){
                                MessageManage.answer(message.getCallback_id());
                            } else if(sub == 1){
                                MessageManage.republish(message.getCallback_id());
                            }
                        }
                    } catch (InterruptedException e) {
                        log.error("处理collect应答信息出现异常！异常信息是：{}",e.getMessage());
                    }
                }
            }
        },"collect应答线程").start();
    }

    /**
     * 处理web应答信息
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    private static void handlerAnswerWeb(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        MqttMessage mqttMessage = queue_answer_web.take();
                        Message message = JSONObject.parseObject(new String(mqttMessage.getPayload()),Message.class);
                        if(message != null && message.getBody() != null){
                            MesBody body = message.getBody();
                            if (body.getSub_type() == 0){// 应答日志
                                // 消息体不为空即认为应答成功，从管理容器中移除
                                LogManager.FILEUPLOADURL_MAP.remove(message.getCallback_id());
                            }// 应答其他类型，可以在此扩展
                        }
                    } catch (InterruptedException e) {
                        log.error("处理web应答信息出现异常！异常信息是：{}",e.getMessage());
                    }
                }
            }
        },"web应答线程").start();
    }

    /**
     * 向broker2回应
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    private static void publish2(Message message){
        if (message == null || message.getBody() == null || message.getBody().getContext() == null) {
            return;
        }
        // 回应topic
        String answer_topic = "CONTROL/WEB/" + message.getSource_mac() + "/ANSWER";
        // mqtt工具类 broker2
        MqttUtils mqttUtils = new MqttUtils();
        mqttUtils.publish(answer_topic,getAnswer(message.getMsg_id(),"SUCCESS"));
        // 移除已经回应的消息
        MessageManage.PUBLISH_MSG_MAP2.remove(message.getCallback_id());
    }

    /**
     * 向broker2回应
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    private static void publish2(Message message,String context){
        if (message == null || message.getBody() == null || message.getBody().getContext() == null) {
            return;
        }
        // 回应topic
        String answer_topic = ControlConfig.SERVERTYPE + "/" + message.getSource_type().toUpperCase() + "/" + message.getSource_mac() + "/ANSWER";
        // mqtt工具类 broker2
        MqttUtils mqttUtils = new MqttUtils();
        mqttUtils.publish(answer_topic,getAnswer(message.getMsg_id(),context));
        // 移除已经回应的消息
        MessageManage.PUBLISH_MSG_MAP2.remove(message.getCallback_id());
    }

    /**
     * 构造应答消息
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    private static MqttMessage getAnswer(String msg_id,String context){

        // 构造消息内容
        MesBody body = new MesBody();
        List<String> list = new ArrayList <String>();
        list.add(context);
        body.setContext(list);

        // 构造消息
        Message answer_msg = new Message();
        answer_msg.setMsg_id(ControlConfig.MACADDRESS + System.currentTimeMillis());// 此消息的id
        answer_msg.setCallback_id(msg_id);// 应答目标的id
        answer_msg.setLicense("");// TODO 如果有授权码，则再写相应的逻辑
        answer_msg.setSource_mac(ControlConfig.MACADDRESS);// 发布者mac地址
        answer_msg.setSource_type(ControlConfig.SERVERTYPE);// 发布者类型
        answer_msg.setMsg_type(6);// 服务类型
        answer_msg.setCreate_time(System.currentTimeMillis()); // 创建时间
        answer_msg.setBody(body); // 消息体

        // 构造mqtt消息
        MqttMessage answer = new MqttMessage();
        answer.setQos(1);
        answer.setRetained(true);
        answer.setPayload(JSONObject.toJSONString(answer_msg).getBytes());

        return answer;
    }

    /**
     * 转类型
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    private static String getType(int typs){
        String str_type = null;
        switch (typs){
            case 1 : str_type = "REGIST"; break;
            case 2 : str_type = "PING"; break;
            case 3 : str_type = "DATA"; break;
            case 4 : str_type = "CONFIG"; break;
            case 5 : str_type = "STATUS"; break;
            case 6 : str_type = "ANSWER"; break;
            case 7 : str_type = "CTRL"; break;
            default: break;
        }
        return str_type;
    }


    /**
     * 启动队列任务
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    static {
        handlerConfig();
        handlerCtrl();
        handlerAnswerWeb();
        handlerAnswerCollect();
    }
}
