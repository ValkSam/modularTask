package modular.design.service;

import modular.design.config.WordCounterConfig;
import modular.design.config.WordCounterTaskExecutorProperties;
import modular.design.model.Source;
import modular.design.repository.SourceRepository;
import modular.design.service.RootDirProcessingTask.RootDirProcessingResult;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WordCounterTaskTest {

    @Mock
    private WordCounterTaskExecutorProperties wordCounterTaskExecutorProperties;
    @Mock
    private SourceRepository sourceRepository;
    @Mock
    private RootDirProcessorFactory rootDirProcessorFactory;
    @Mock
    private ResultSaver resultSaver;

    @Test
    public void runTest() {
        when(wordCounterTaskExecutorProperties.getTaskThreads()).thenReturn(2);
        ExecutorService executorService = new WordCounterConfig(wordCounterTaskExecutorProperties).wordCounterTaskExecutor();

        WordCounterTask wordCounterTask = new WordCounterTask(
                executorService,
                sourceRepository,
                rootDirProcessorFactory,
                resultSaver);

        final Integer sourceRecordsInDb = new Random().nextInt(100) + 10;
        List<Source> sourceList = new ArrayList<Source>() {{
            for (int i = 0; i < sourceRecordsInDb; i++) {
                add(Source.builder()
                        .dir(RandomString.make())
                        .build());
            }
        }};

        when(sourceRepository.findAll()).thenReturn(sourceList);
        when(rootDirProcessorFactory.getRootDirProcessingTask(any()))
                .thenAnswer(invocation -> {
                    assertTrue(sourceList.stream().anyMatch(e -> e.getDir().equals(invocation.getArgument(0))));
                    RootDirProcessingTask rootDirProcessingTask = mock(RootDirProcessingTask.class);
                    when(rootDirProcessingTask.call()).thenReturn(new RootDirProcessingResult(invocation.getArgument(0)));
                    return rootDirProcessingTask;
                });
        doAnswer(invocation -> {
            assertTrue(sourceList.stream()
                    .anyMatch(e -> e.getDir().equals(((RootDirProcessingResult) invocation.getArgument(0)).getRootDirName())));
            return null;
        }).when(resultSaver).save(any());

        wordCounterTask.run();

        verify(resultSaver, times(sourceList.size())).save(any());

    }

}
