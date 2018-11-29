package app.server.serverImpl;

import app.entity.CtrlInfo;
import app.entity.MesBody;
import app.entity.Message;
import app.server.CtrlServerI;
import app.utils.config.ControlConfig;
import app.utils.md5.MD5Utils;
import app.entity.CtrlResult;
import app.utils.mqtt.MessageManage;
import app.utils.mqtt.MqttUtils;
import app.utils.result.R;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequestMapping("/ctrlServer")
public class CtrlServerImpl implements CtrlServerI {

    @Override
    public boolean verification(CtrlInfo ctrlInfo) {
        // 得到本服务所管理的物联网接口服务的mac地址集合
        Set<String> collect_macs = ControlConfig.COLLECT_PROXY_MAP.keySet();
        if (collect_macs.contains(ctrlInfo.getCollect_mac())){
            // 拿到采集代理的集合
            List<String> proxy_macs = ControlConfig.COLLECT_PROXY_MAP.get(ctrlInfo.getCollect_mac());
            if (proxy_macs.contains(ctrlInfo.getProxy_mac())){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public String change(CtrlInfo ctrlInfo) {
        try {
            // 控制命令
            List<String> ctrls = new ArrayList<>();
            ctrls.add(JSONObject.toJSONString(ctrlInfo));

            // 消息体
            MesBody body = new MesBody();
            body.setSub_type(0);
            body.setContext(ctrls);

            // 消息
            Message message = new Message();
            message.setMsg_id(ControlConfig.SERVERTYPE + System.currentTimeMillis());
            message.setSource_mac(ControlConfig.MACADDRESS);
            message.setSource_type(ControlConfig.SERVERTYPE);
            message.setCreate_time(System.currentTimeMillis());
            message.setCallback_id(""); // 管理mqtt时使用，用作key
            message.setMsg_type(7); // 控制消息类型
            message.setLicense(""); // TODO 还没确定怎么用
            message.setBody(body);

            // mqtt消息
            String msg = JSONObject.toJSONString(message);
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(1);
            mqttMessage.setRetained(true);
            mqttMessage.setPayload(msg.getBytes());

            // 发布控制命令
            String topic = ControlConfig.SERVERTYPE + "/COLLECT/" + ctrlInfo.getCollect_mac().toUpperCase() + "/CTRL";
            MqttUtils mqttUtils = new MqttUtils();
            mqttUtils.publish(topic,mqttMessage);

            // 放进管理容器中
            MessageManage.add(message.getMsg_id(),message);

            // 回调凭证
            return message.getMsg_id();
        } catch (Exception e){
            log.error("下发命令失败，异常信息是：{}",e.getMessage());
            return null;
        }
    }

    @Override
    public String callback( String token ) {
        R r = new R();
        CtrlResult ctrlResult = MessageManage.get(token);
        if (ctrlResult == null) {
            r.setCode(4);
            r.setRemark("关键字无效！");
        } else if (!ctrlResult.isAnswer()){
            r.setCode(5);
            r.setRemark("正在执行，请稍后再试！");
        } else if (ctrlResult.isResult()){
            r.setCode(0);
            r.setRemark("执行成功！");
            MessageManage.remove(token);
        } else {
            r.setCode(1);
            r.setRemark("执行失败！");
            MessageManage.remove(token);
        }
        return JSONObject.toJSONString(r);
    }

    /**
     * 用MD5验证key
     */
    public boolean checkMD5(CtrlInfo ctrlInfo){
        String newStr = ControlConfig.IDENTIFYING_CODE + ctrlInfo.getRandom();
        String md4Str = MD5Utils.MD5(newStr);
        if (md4Str.equals(ctrlInfo.getKey())){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 用MD5验证key
     */
    public boolean checkMD5(String random,String key){
        String newStr = ControlConfig.IDENTIFYING_CODE + random;
        String md4Str = MD5Utils.MD5(newStr);
        if (md4Str.equals(key)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证数值类型是否符合规范
     */
    public boolean checkDataType(CtrlInfo ctrlInfo){
        if ("string".equals(ctrlInfo.getData_type()) ||
                "int".equals(ctrlInfo.getData_type()) ||
                "double".equals(ctrlInfo.getData_type())) {
            return true;
        } else {
            return false;
        }
    }
}
