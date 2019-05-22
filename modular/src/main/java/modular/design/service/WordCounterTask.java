package modular.design.service;

import lombok.extern.log4j.Log4j2;
import modular.design.repository.SourceRepository;
import modular.design.service.RootDirProcessingTask.RootDirProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
@Log4j2
public class WordCounterTask implements Runnable {

    private final ExecutorService executorService;
    private final SourceRepository sourceRepository;
    private final RootDirProcessorFactory rootDirProcessorFactory;
    private final ResultSaver resultSaver;

    @Autowired
    public WordCounterTask(@Qualifier("wordCounterTaskExecutor") ExecutorService executorService,
                           SourceRepository sourceRepository,
                           RootDirProcessorFactory rootDirProcessorFactory,
                           ResultSaver resultSaver) {
        this.executorService = executorService;
        this.sourceRepository = sourceRepository;
        this.rootDirProcessorFactory = rootDirProcessorFactory;
        this.resultSaver = resultSaver;
    }

    @Override
    public void run() {
        log.info("Scheduled task has started");

        CompletionService<RootDirProcessingResult> completionService = new ExecutorCompletionService(executorService);

        List<RootDirProcessingTask> tasks = createFileProcessingTaskListForEachRootDir();

        tasks.forEach(completionService::submit);

        for (RootDirProcessingTask task : tasks) {
            Future<RootDirProcessingResult> future;
            try {
                future = completionService.take();
                RootDirProcessingResult rootDirProcessingResult = future.get();
                resultSaver.save(rootDirProcessingResult);
                log.info(format("Task for dir %s has completed and saved", rootDirProcessingResult.getRootDirName()));
            } catch (Exception e) {
                log.error(format("Error while task execution for dir: %s with error: %s", task.getRootDir(), e.getMessage()), e);
            }
        }

        log.info("Scheduled task has finished");
    }

    private List<RootDirProcessingTask> createFileProcessingTaskListForEachRootDir() {
        return sourceRepository.findAll().stream()
                .map(e -> rootDirProcessorFactory.getRootDirProcessingTask(e.getDir()))
                .collect(Collectors.toList());
    }
}
