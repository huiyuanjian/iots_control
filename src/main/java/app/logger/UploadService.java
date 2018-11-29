package app.logger;

import app.logger.utils.FastDFSClient;
import app.logger.utils.FastDFSFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component("uploadService")
@Slf4j
public class UploadService {

    public static void main(String[] args) {
        UploadService service= new UploadService();
        String url= null;
        try {
            url = service.upload("f:\\10.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("args = [" + url + "]");
    }

    /**
     * 上传文件
     * path是文件路径
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public String upload(String path) throws IOException {

        File file = new File(path);

        FileInputStream input = new FileInputStream(file);

        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", input);

        UploadService uploadService = new UploadService();

        String url = uploadService.saveFile(multipartFile);

        log.info("访问路径是：{}",url);

        log.info("当前程序的系统路径：{}",System.getProperty("user.dir"));
        return url;

    }


    /**
     * 将文件上传到fastdfs上
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public String saveFile( MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if(inputStream != null){
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = FastDFSClient.upload(file);  //upload to fastdfs
        } catch (Exception e) {
            log.error("upload file Exception! : {}",e);
        }
        if (fileAbsolutePath==null) {
            log.error("upload file failed,please upload again!");
        }
//        String path=FastDFSClient.getTrackerUrl()+fileAbsolutePath[0]+ "/"+fileAbsolutePath[1];
        String path= FastDFSClient.getTrackerUrl().split(":8080")[0]+ "/"+fileAbsolutePath[1];
        return path;
    }

    /**
     * 日志文件的状态转换流程
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public LogFileStatusEnum change(LogFileStatusEnum status, boolean b){
        switch (status){
            case NOT_UPLOAD:
                status = LogFileStatusEnum.START_UPLOAD;
                break;
            case START_UPLOAD:
                if(b){
                    status = LogFileStatusEnum.UPLOAD_SUCCESS;
                }else {
                    status = LogFileStatusEnum.UPLOAD_FAILED;
                }
                break;
            case UPLOAD_FAILED:
                if(b){
                    status = LogFileStatusEnum.UPLOAD_SUCCESS;
                }
                break;
            case UPLOAD_SUCCESS:
                status = LogFileStatusEnum.URL_PREPARE_STORAGE; // 当发送入库指令后，变更成此状态
                break;
            case URL_PREPARE_STORAGE:
                if(b){
                    status = LogFileStatusEnum.URL_SUCCESS_STORAGE;
                }else {
                    status = LogFileStatusEnum.URL_FAILED_STORAGE;
                }
                break;
            case URL_FAILED_STORAGE:
                if(b){
                    status = LogFileStatusEnum.URL_SUCCESS_STORAGE;
                }
                break;
            default:
                break;
        }
        return status;
    }
}