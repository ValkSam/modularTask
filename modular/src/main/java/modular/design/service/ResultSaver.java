package modular.design.service;

import modular.design.exception.RecordNotFoundException;
import modular.design.model.Result;
import modular.design.model.Source;
import modular.design.repository.ResultRepository;
import modular.design.repository.SourceRepository;
import modular.design.service.RootDirProcessingTask.RootDirProcessingResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Component
public class ResultSaver {

    private final SourceRepository sourceRepository;
    private final ResultRepository resultRepository;

    public ResultSaver(SourceRepository sourceRepository,
                       ResultRepository resultRepository) {
        this.sourceRepository = sourceRepository;
        this.resultRepository = resultRepository;
    }

    @Transactional
    public void save(RootDirProcessingResult rootDirProcessingResult) {
        Source source = sourceRepository.findByDir(rootDirProcessingResult.getRootDirName())
                .orElseThrow(() -> new RecordNotFoundException(format("table: %s, by dir: %s", "source", rootDirProcessingResult.getRootDirName())));
        source.setLastScanned(LocalDateTime.now());
        sourceRepository.update(source);

        resultRepository.deleteByDirId(source.getId());

        for (Map.Entry<String, List<String>> fileData : rootDirProcessingResult.getWordsByFile().entrySet()) {
            Result result = Result.builder()
                    .dirId(source.getId())
                    .fileName(fileData.getKey())
                    .words(fileData.getValue())
                    .build();
            resultRepository.insert(result);
        }
    }

}
