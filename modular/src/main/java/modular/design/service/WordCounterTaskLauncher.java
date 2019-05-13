package modular.design.service;

import modular.design.config.WordCounterTaskExecutorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class WordCounterTaskLauncher {

    private final ScheduledExecutorService wordCounterTaskExecutor;
    private final WordCounterTask wordCounterTask;
    private final WordCounterTaskExecutorProperties wordCounterTaskExecutorProperties;

    @Autowired
    public WordCounterTaskLauncher(@Qualifier("wordCounterTaskLauncherExecutor") ScheduledExecutorService wordCounterTaskExecutor,
                                   WordCounterTask wordCounterTask,
                                   WordCounterTaskExecutorProperties wordCounterTaskExecutorProperties) {
        this.wordCounterTaskExecutor = wordCounterTaskExecutor;
        this.wordCounterTask = wordCounterTask;
        this.wordCounterTaskExecutorProperties = wordCounterTaskExecutorProperties;
    }

    public Future launch() {
        return wordCounterTaskExecutor.scheduleAtFixedRate(
                wordCounterTask,
                wordCounterTaskExecutorProperties.getStartDelaySeconds(),
                wordCounterTaskExecutorProperties.getPeriodSeconds(),
                TimeUnit.SECONDS);
    }

}
