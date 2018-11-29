package app.logger.scheduling;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * @author zcy
 * @date 2018/10/2510:30
 */
@Configuration // 定时调度的配置类
public class SchedulerConfig implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) { // 开启一个线程调度池
		taskRegistrar.setScheduler(Executors.newScheduledThreadPool(100));
	}

}
