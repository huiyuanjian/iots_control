package app.utils.mqtt;

import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * mqtt 的连接信息 (broker1 专用)
 */
@Data
public class AutoMqttProperties {

    // broker连接地址
    private String host;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 身份id
    private String clientid;

    // 超时（单位是秒）
    private int timeout;

    // 会话心跳时间 （单位为秒）
    private int keepalive;

    public AutoMqttProperties () {
        System.out.println("AutoMqttProperties_before:" + toString());

        Resource resource = new ClassPathResource("/application.yml");
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            this.host = props.getProperty("mqtt_host");
            this.username = props.getProperty("mqtt_username");
            this.password = props.getProperty("mqtt_password");
            this.clientid = props.getProperty("mqtt_clientid");
            this.timeout = Integer.valueOf(props.getProperty("mqtt_timeout"));
            this.keepalive = Integer.valueOf(props.getProperty("mqtt_keepalive"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("AutoMqttProperties_after:" + toString());
    }

}
