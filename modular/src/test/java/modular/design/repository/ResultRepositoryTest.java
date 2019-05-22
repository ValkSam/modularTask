package modular.design.repository;

import lombok.extern.slf4j.Slf4j;
import modular.design.config.DatabaseConfig;
import modular.design.model.Result;
import modular.design.model.Source;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.util.Assert.notNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = {DatabaseConfig.class,},
        properties = {"spring.config.name=modular", "spring.profiles.default=test"})
@Slf4j
@EnableAutoConfiguration
@Transactional
public class ResultRepositoryTest {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Test
    public void insertAndFindByIdTest() {
        Source source = Source.builder()
                .dir(RandomString.make())
                .build();
        source = sourceRepository.insert(source).get();
        String fileName = RandomString.make();
        List<String> words = Arrays.asList(RandomString.make(), RandomString.make());
        Result result = Result.builder()
                .dirId(source.getId())
                .fileName(fileName)
                .words(words)
                .build();

        Result createdResult = resultRepository.insert(result).get();
        assertTrue("Should return the same object", result == createdResult);
        notNull(createdResult.getId(), "Id should be set while creating");
        assertEquals(source.getId(), createdResult.getDirId());
        assertEquals(fileName, createdResult.getFileName());
        assertEquals(words, createdResult.getWords());

        Result createdAndReadResult = resultRepository.findById(createdResult.getId()).get();
        assertEquals("Inserted record should be found", createdResult.getId(), createdAndReadResult.getId());
        assertEquals(source.getId(), createdAndReadResult.getDirId());
        assertEquals(fileName, createdAndReadResult.getFileName());
        assertEquals(words, createdAndReadResult.getWords());

        assertFalse("should return Optional.empty() for record that doesn't not exist",
                resultRepository.findById(Long.MIN_VALUE).isPresent());
    }

    @Test
    public void deleteByDirIdTest() {
        Source source = Source.builder()
                .dir(RandomString.make())
                .build();
        source = sourceRepository.insert(source).get();

        int resultCreatedCount = 3;

        for (int i = 0; i < resultCreatedCount; i++) {
            resultRepository.insert(
                    Result.builder()
                            .dirId(source.getId())
                            .fileName(RandomString.make())
                            .build()).get();
        }

        assertEquals(resultCreatedCount, resultRepository.deleteByDirId(source.getId()));
    }

}
