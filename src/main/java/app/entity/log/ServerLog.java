package app.entity.log;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 日志文件信息实体类
 */
@Data
public class ServerLog {

    /**
     * 服务id
     */
    private String server_id;

    /**
     * 服务名称
     */
    private String server_name;

    /**
     * 服务类型
     */
    private String server_type;

    /**
     * 服务的mac地址
     */
    private String server_mac;

    /**
     * 上传时间
     */
    private Date upload_time;

    /**
     * 日志文件的访问路径信息
     * 第一个String 是日期：yyyy-MM-dd
     * 第二个String 是文件名：服务类型_服务mac地址_日期_时间戳.log
     * 第三个String 是日志文件的访问路径
     * 其中，第二个String中的"日期"的格式是：yyyy-MM-dd
     */
    private Map<String,Map<String,String>> map;

}
