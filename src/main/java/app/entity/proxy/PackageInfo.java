package app.entity.proxy;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: PackageInfo
 * @Description: 数据包信息实体bean
 * @author 周西栋
 * @date 2018年5月7日
 */
@Data
public class PackageInfo {

	/**
	 * 数据包id
	 */
	private Integer id;
	
	/**
	 * 数据包名称
	 */
	private String name;

	/**
     * 分组标识(多层，形如：groupid1/groupid2/groupid3/...)
	 */
	private String group_id;

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
	
	/**
	 * 变量id的容器
	 */
	private List<Integer> var_list;
}
