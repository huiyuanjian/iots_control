package app.logger;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志文件的上传和入库状态枚举类
 * @author 周西栋
 * @date
 * @param
 * @return
 */
@Slf4j
public enum LogFileStatusEnum {
    NOT_UPLOAD("未上传",0),
    START_UPLOAD("开始上传",1),
    UPLOAD_FAILED("文件上传失败",2),
    UPLOAD_SUCCESS("文件上传成功",3),
    URL_PREPARE_STORAGE("准备存储上传路径",4),
    URL_FAILED_STORAGE("上传路径存储失败",5),
    URL_SUCCESS_STORAGE("上传路径存储成功",6);

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int index;

    LogFileStatusEnum(String name, int index){
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (LogFileStatusEnum c : LogFileStatusEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
}
