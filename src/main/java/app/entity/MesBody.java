package app.entity;

import lombok.Data;

import java.util.List;

/**
 * body 消息体
 */
@Data
public class MesBody {

    /**
     * 消息子类型
     */
    private Integer sub_type;

    /**
     * 存放各种消息
     */
    private List<String> context;
}
