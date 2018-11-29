package app.utils.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 控制接口服务的状态枚举类
 * @author 周西栋
 * @date
 * @param
 * @return
 */
@Slf4j
public enum ControlServerStatusEnum {

    START_UP("服务启动",0),
    READ_LOCAL_CONFIGURATION("读取本地配置",1),
    FAILED_READ_LOCAL_CONFIGURATION("读取本地配置失败",2),
    WAITING_FOR_WEB_CONNECTION("等待Web服务连接",3),
    CONNECT_TO_WEB("连接上Web",4),
    WAITING_FOR_DISPATCH_CONFIGURATION("等待下发配置",5),
    CONFIGURATION_SUCCESS("配置成功",6),
    SERVICE_START("服务开始工作",7),
    SERVICE_FAILED("服务停止工作",8);

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int index;

    ControlServerStatusEnum(String name, int index){
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (ControlServerStatusEnum c : ControlServerStatusEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
}