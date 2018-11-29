package app.entity.proxy;

import lombok.Data;

/**
 * @ClassName: VarInfo
 * @Description: 变量信息实体bean
 * @author 周西栋
 * @date 2018年5月7日
 *
 */
@Data
public class VarInfo {

	/**
	 * 变量id
	 */
	private Integer id;

	/**
	 * IOServer上的变量id（朱磊加）
	 */
	private Integer var_id;
	
	/**
	 * 变量名称
	 */
	private String name;

	/**
	 * 关联设备ID
	 */
	private Integer device_id;

	/**
	 * 关联设备名称
	 */
	private String device_name;

	/**
	 * 变量说明
	 */
	private String info;
	
	/**
	 * 变量错误或者超时是否传输NULL
	 */
	private Integer error_as_null;
	
	/**
	 * 变量超时时间（秒）
	 */
	private Integer time_out;
	
	/**
	 * 打包类型：0:间隔时间打包；1：按分钟；2：每15分钟；3：每小时；4每天
	 */
	private Integer pack_type;
	
	/**
	 * 默认打包间隔时间（秒）
	 */
	private Integer time_span;
}
