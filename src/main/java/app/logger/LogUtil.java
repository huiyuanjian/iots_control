package app.logger;

import app.entity.MesBody;
import app.entity.Message;
import app.logger.utils.DateUtil;
import app.utils.config.ConfigInfo;
import app.utils.config.ControlConfig;
import app.utils.mqtt.MqttUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class LogUtil {

    @Value("${module.name}")
    private String moduleName;

    public List<String> getChild( File file, List<String> list,String identify,boolean isupload){
        File[] tempList = file.listFiles();
        if(tempList == null) return null;
        log.info("该目录下对象个数：{}",tempList.length);
        for (int i = 0; i < tempList.length; i++) {
            if(isupload){
                if (tempList[i].isFile()&&tempList[i].getName().indexOf(identify)==-1) {
                    list.add(tempList[i].toString());
                    log.info("文     件：{}",tempList[i]);
                }
            }else{
                if (tempList[i].isFile()&&tempList[i].getName().indexOf(identify)>0) {
                    list.add(tempList[i].toString());
                    log.info("文     件：{}",tempList[i]);
                }
            }
            if (tempList[i].isDirectory()) {
                System.out.println("文件夹："+tempList[i]);
                //List<String> list_children = new ArrayList<>();
                list = getChild(tempList[i],list,identify,isupload);
            }
        }
        //检查文件名 是不是 最新的正在占用的
        if(identify.equals("--Already")) list = checkedFileName(list);
        return list;
    }

    /*
     * @Author zcy
     * @Description //TODO 检查文件名 是不是 最新的正在占用的
     * @Date 14:04 2018/11/8
     * @Param
     * @return
     **/
    private List<String> checkedFileName(List<String> list) {
        List<Integer> indexLi=new ArrayList<>();
        String newDate= DateUtil.parseDateToStr(new Date(),"yyyy-MM-dd");
        String path=null;
        for (int i=0; i<list.size();i++){//F:\IOTS\IOTs_Proxy\IOTs_Proxy_2018-11-08.0.log
            String tempName=list.get(i);
            //解析
            path=tempName.substring(0,tempName.lastIndexOf(moduleName));
            String dateTime=tempName.substring(tempName.lastIndexOf(moduleName+"_")+moduleName.length()+1,tempName.indexOf("."));
            String index=tempName.substring(tempName.indexOf(".")+1,tempName.lastIndexOf("."));
            if (newDate.equals(dateTime)){
                indexLi.add(Integer.parseInt(index));
            }
        }
        Integer maxIndel=0;
        String filename=null;
        //查出集合中最大的是哪个
        if (!CollectionUtils.isEmpty(indexLi)){
            maxIndel = Collections.max(indexLi);
            filename=path+moduleName+"_"+newDate+"."+maxIndel+".log";
        }
        if (!StringUtils.isEmpty(filename)){
            list.remove(filename);
        }
        return list;
    }


    /**
     * 文件重命名
     * @param orgFile
     */
    public void reanameFile(String orgFile,String identify){
        if (!StringUtils.isEmpty(orgFile)){
            String orgName= orgFile.substring(0,orgFile.lastIndexOf("."))+identify+orgFile.substring(orgFile.lastIndexOf("."));
            File fileorg= new File(orgFile);
            File fileNew= new File(orgName);
            boolean isok =fileorg.renameTo(fileNew);
            log.info(" 修改成功？？？？？"+isok);
        }
    }

    /**
     * 文件删除
     * @param orgFile
     */
    public void deleteFile(String orgFile){
        if (!StringUtils.isEmpty(orgFile)){
            File fileorg= new File(orgFile);
            boolean isok = fileorg.delete();
            log.info(" 删除成功？？？？？"+isok);
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
