package app.controller;

import app.entity.CtrlInfo;
import app.server.serverImpl.CtrlServerImpl;
import app.utils.result.R;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 控制接口调用
 */
@RestController
@RequestMapping("/ctrl")
public class CtrlController {

    @Autowired
    private CtrlServerImpl ctrlServer;

    /**
     * 下发控制命令
     * @param ctrlInfo ioserver的控制命令
     * @return
     */
    @PostMapping("/change")
    public String change(CtrlInfo ctrlInfo){
        R r = new R();
        if (ctrlServer.checkMD5(ctrlInfo)){
            if(ctrlServer.verification(ctrlInfo)){
                if (ctrlServer.checkDataType(ctrlInfo)) {
                    String result = ctrlServer.change(ctrlInfo);
                    if(result != null){
                        r.setResult(result);
                    } else {
                        r.setCode(1);
                        r.setRemark("发生未知异常，请联系系统管理员！");
                    }
                }else {
                    r.setCode(7);
                    r.setRemark("不支持的数值类型！目前只支持'string'、'int'、'double'三种数值类型！");
                }
            } else {
                r.setCode(2);
                r.setRemark("物联网接口的mac地址或者采集代理的mac地址不正确，请确认后再试！");
            }
        } else {
            r.setCode(3);
            r.setRemark("认证码错误！请确认后再试！");
        }
        return JSONObject.toJSONString(r);
    }

    /**
     * 执行结果回调
     */
    @GetMapping("/callback")
    public String callback(String token,String random,String key){
        if (ctrlServer.checkMD5(random,key)) {
            return ctrlServer.callback(token);
        } else {
            R r = new R();
            r.setCode(3);
            r.setRemark("认证码错误！请确认后再试！");
            return JSONObject.toJSONString(r);
        }
    }
}
