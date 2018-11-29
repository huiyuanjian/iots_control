package app.utils.fileIO;

import app.utils.config.ConfigInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.*;

/**
 * 读写文件
 * @author 周西栋
 * @date
 * @param
 * @return
 */
@Configuration
@Slf4j
@Data
public class FileUtils {

    @Value("${config_info.path:#{'/config'}}")
    private String file_path;

    @Value("${config_info.file-name:#{controlConfig.txt}}")
    private String file_name;

    /**
     * 保存配置文件
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public boolean saveLoginInfo( ConfigInfo configInfo){
        file_path = file_path == null ? "/config" : file_path;
        file_name = file_name == null ? "controlConfig.txt" : file_name;
        log.info("配置文件的 -- 保存 -- 路径：{}",file_path);
        log.info("配置文件的 -- 保存 -- 名称：{}",file_name);
        String system_dir = System.getProperty("user.dir");
        system_dir = system_dir.substring(system_dir.length()-13,system_dir.length()-1).equals("iots_control") ? system_dir : (system_dir + "/iots_control");
        String logFiles_url = system_dir+file_path+"/"+file_name;
        log.info("配置文件的 -- 读取 -- 本地文件全路径：{}",logFiles_url);
        File logFiles = new File(logFiles_url);
        if(logFiles.exists()){
            // 如果文件已经存在，则先删除
            logFiles.delete();
        }
        ObjectOutputStream oop = null;
        try {
            oop = new ObjectOutputStream(new FileOutputStream(logFiles));
            oop.writeObject(configInfo);
            oop.flush();
            log.info("配置信息保存到 >>> {} <<< 文件中！",logFiles_url);
            log.info("保存的信息是：{}",configInfo.toString());
            return true;
        } catch (Exception e) {
            log.error("配置信息保存失败！ 异常信息是：{}",e.getMessage());
            return false;
        } finally {
            if (oop != null){
                try {
                    oop.close();
                } catch (IOException e) {
                    log.error("保存 配置信息文件完成后，输出流关闭异常！异常信息是：{}",e.getMessage());
                }
            }
        }
    }

    /**
     * 读取配置文件
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public ConfigInfo readLoginInfo(){
        file_path = file_path == null ? "/config" : file_path;
        file_name = file_name == null ? "controlConfig.txt" : file_name;
        log.info("配置文件的 -- 读取 -- 路径：{}",file_path);
        log.info("配置文件的 -- 读取 -- 名称：{}",file_name);
        String system_dir = System.getProperty("user.dir");
        system_dir = system_dir.substring(system_dir.length()-13,system_dir.length()-1).equals("iots_contorl") ? system_dir : (system_dir + "/iots_control");
        String logFiles_url = system_dir+file_path+"/"+file_name;
        log.info("配置文件的 -- 读取 -- 本地文件全路径：{}",logFiles_url);
        File logFiles = new File(logFiles_url);
        if(!logFiles.exists()){
            log.warn("配置信息不存在！读取失败！");
            return null;
        }
        ConfigInfo configInfo = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(logFiles));
            configInfo =  (ConfigInfo) ois.readObject();
            log.info("配置信息 -- 读取 -- 成功！ 配置信息是：{}",configInfo.toString());
        } catch (Exception e) {
            log.error("配置信息 -- 读取 -- 失败！ 异常信息是：{}",e.getMessage());
        } finally {
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    log.error("读取 配置信息文件完成后，输入流关闭异常！异常信息是：{}",e.getMessage());
                }
            }
        }
        return configInfo;
    }
}
