package modular.design.repository;

import lombok.extern.slf4j.Slf4j;
import modular.design.config.DatabaseConfig;
import modular.design.model.Source;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.util.Assert.notNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = {DatabaseConfig.class,},
        properties = {"spring.config.name=modular", "spring.profiles.default=test"})
@Slf4j
@EnableAutoConfiguration
@Transactional
public class SourceRepositoryTest {

    @Autowired
    private SourceRepository sourceRepository;

    @Test
    public void insertAndFindByIdTest() {
        String dir = RandomString.make();
        LocalDateTime lastScanned = LocalDateTime.now();
        Source source = Source.builder()
                .dir(dir)
                .lastScanned(lastScanned)
                .build();

        Source createdSource = sourceRepository.insert(source).get();
        assertTrue("Should return the same object", source == createdSource);
        notNull(createdSource.getId(), "Id should be set while creating");
        assertEquals(dir, createdSource.getDir());
        assertEquals(lastScanned, createdSource.getLastScanned());

        Source foundSource = sourceRepository.findByDir(createdSource.getDir()).get();
        assertEquals("Inserted record should be found", createdSource.getId(), foundSource.getId());
        assertEquals(dir, foundSource.getDir());
        assertEquals(lastScanned, foundSource.getLastScanned());

        assertFalse("should return Optional.empty() for record that doesn't not exist",
                sourceRepository.findByDir("any wrong dir name").isPresent());
    }

    @Test
    public void findByDirTest() {
        String dir = RandomString.make();
        Source source = Source.builder()
                .dir(dir)
                .lastScanned(LocalDateTime.now())
                .build();

        Source createdSource = sourceRepository.insert(source).get();

        Source foundSource = sourceRepository.findByDir(dir).get();
        assertEquals(createdSource.getId(), foundSource.getId());
        assertEquals(createdSource.getDir(), foundSource.getDir());
        assertEquals(createdSource.getLastScanned(), foundSource.getLastScanned());

        assertFalse("should return Optional.empty() for record that doesn't not exist",
                sourceRepository.findByDir(dir + "wrong").isPresent());
    }

    @Test
    public void updateTest() {
        Source source = Source.builder()
                .dir(RandomString.make())
                .build();

        Source createdSource = sourceRepository.insert(source).get();

        Long initialId = createdSource.getId();
        String initialDir = createdSource.getDir();
        LocalDateTime initialLastScanned = createdSource.getLastScanned();

        String newDir = RandomString.make();
        LocalDateTime newLastScanned = LocalDateTime.now();

        source.setDir(newDir);
        source.setLastScanned(newLastScanned);

        Source updatedSource = sourceRepository.update(source).get();
        assertTrue("Should return the same object", createdSource == updatedSource);
        assertEquals(initialId, updatedSource.getId());
        assertNotEquals(initialDir, updatedSource.getDir());
        assertNotEquals(initialLastScanned, updatedSource.getLastScanned());
        assertEquals(newDir, updatedSource.getDir());
        assertEquals(newLastScanned, updatedSource.getLastScanned());
    }

}
