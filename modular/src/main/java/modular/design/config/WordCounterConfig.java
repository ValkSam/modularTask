package modular.design.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableConfigurationProperties({
        WordCounterTaskExecutorProperties.class,
        WordCounterTaskFileProperties.class,
})
public class WordCounterConfig {

    private final WordCounterTaskExecutorProperties wordCounterTaskExecutorProperties;

    @Autowired
    public WordCounterConfig(WordCounterTaskExecutorProperties wordCounterTaskExecutorProperties) {
        this.wordCounterTaskExecutorProperties = wordCounterTaskExecutorProperties;
    }

    @Bean("wordCounterTaskLauncherExecutor")
    public ScheduledExecutorService wordCounterTaskLauncherExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean("wordCounterTaskExecutor")
    public ExecutorService wordCounterTaskExecutor() {
        return Executors.newFixedThreadPool(wordCounterTaskExecutorProperties.getTaskThreads(),
                new CustomizableThreadFactory("wordCounter-Thread"));
    }

}
