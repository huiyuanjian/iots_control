package app.listener;

import app.utils.config.ConfigUtils;
import app.utils.config.ReadLocalConfigInfoThread;
import app.utils.mqtt.TopicSubscrib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(value = 4)
public class TimerTaskListener implements ApplicationRunner {

    @Override
    public void run( ApplicationArguments applicationArguments ) throws Exception {
        // 启用定时任务
        ConfigUtils utils = new ConfigUtils();
        utils.startTimer();

        ListenManager.ENABLE_SUBSCRIB.await();
        ListenManager.READLOCALFILE.await();
        Thread.sleep(10000);
        // 启用订阅
        TopicSubscrib topicSubscrib = new TopicSubscrib();
        topicSubscrib.run();
    }
}
