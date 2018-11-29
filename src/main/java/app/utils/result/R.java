package app.utils.result;

import lombok.Data;

/**
 * 返回结果
 * @author 周西栋
 * @date
 * @param
 * @return
 */
@Data
public class R {

    /**
     * 返回码
     */
    private int code;

    /**
     * 返回码说明
     */
    private String remark;

    /**
     * 返回结果
     */
    private String result;

    public R (){
        this.code = 0;
        this.remark = "SUCCESS";
    }

    public R (int code, String remark){
        this.code = code;
        this.remark = remark;
    }
}
