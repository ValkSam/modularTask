package modular.design.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Getter
@Slf4j
public class RootDirProcessingTask implements Callable<RootDirProcessingTask.RootDirProcessingResult> {

    private final static String WORD_SEPARATOR_REGEX = "\\s+";
    private final static String WORD_SELECTION_REGEX = "^[a-z]+$";

    private final String rootDir;

    @Autowired
    private FileNamesRetriever fileNamesRetriever;

    @Autowired
    private FileReader fileReader;

    public RootDirProcessingTask(String rootDir) {
        this.rootDir = rootDir;
    }

    public RootDirProcessingTask(String rootDir,
                                 FileNamesRetriever fileNamesRetriever,
                                 FileReader fileReader) {
        this.rootDir = rootDir;
        this.fileNamesRetriever = fileNamesRetriever;
        this.fileReader = fileReader;
    }

    @Override
    public RootDirProcessingResult call() throws Exception {
        log.info(format("Task for dir %s has started", rootDir));
        RootDirProcessingResult result = new RootDirProcessingResult(rootDir);
        List<String> filesInRootDir = fileNamesRetriever.retrieve(rootDir);
        log.info(format("Task for dir %s has got files: %s", rootDir, filesInRootDir.size()));
        for (String fileName : filesInRootDir) {
            try {
                List<String> lines = fileReader.read(fileName);
                result.getWordsByFile().put(fileName,
                        lines.stream()
                                .flatMap(e -> Arrays.stream(e.split(WORD_SEPARATOR_REGEX)))
                                .filter(e -> e.matches(WORD_SELECTION_REGEX))
                                .distinct()
                                .collect(Collectors.toList()));
                log.debug(format("Task for dir %s has completed processing file: %s", rootDir, fileName));
            } catch (Exception e) {
                log.error(format("Task for dir %s. Error %s while processing file: %s", rootDir, e.getMessage(), fileName), e);
            }
        }
        return result;
    }

    @Getter
    public static class RootDirProcessingResult {
        private String rootDirName;
        private Map<String, List<String>> wordsByFile = new HashMap<>();

        public RootDirProcessingResult(String rootDirName) {
            this.rootDirName = rootDirName;
        }
    }

}
