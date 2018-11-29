package app.entity.proxy;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: DeviceInfo
 * @Description: 设备信息实体bean
 * @author 周西栋
 * @date 2018年5月7日
 */
@Data
public class DeviceInfo {

	/**
	 * 设备id
	 */
	private Integer id;

	/**
     * IOServer上的设备id（朱磊加）
	 */
	private Integer device_id;

	/**
     * IOServer上的设备的顺序号（朱磊加）
	 */
	private Integer device_order_id;
	
	/**
	 * 设备名称
	 */
	private String name;
	
	/**
	 * 设备地址
	 */
	private String addr;
	
	/**
	 * 设备通讯超时时间（秒）
	 */
	private Integer time_out;
	
	/**
	 * 默认打包间隔时间（秒）
	 */
	private Integer time_span;
	
	/**
	 * 设备下挂载的变量
	 */
	private List<VarInfo> var_info_list;

}
