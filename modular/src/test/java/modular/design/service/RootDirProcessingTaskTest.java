package modular.design.service;

import modular.design.service.RootDirProcessingTask.RootDirProcessingResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RootDirProcessingTaskTest {

    @Mock
    private FileNamesRetriever fileNamesRetriever;

    @Mock
    private FileReader fileReader;

    private RootDirProcessingTask rootDirProcessingTask;

    @Test
    public void callShouldMakeCorrectMethodInvokesTest() throws Exception {
        String rootDir = RandomString.make();
        this.rootDirProcessingTask = new RootDirProcessingTask(
                rootDir,
                fileNamesRetriever,
                fileReader);

        List<String> filesInRootDir = Arrays.stream(new String[new Random().nextInt(100) + 1])
                .map(e -> RandomString.make())
                .collect(Collectors.toList());
        when(fileNamesRetriever.retrieve(rootDir)).thenReturn(filesInRootDir);

        when(fileReader.read(any())).thenReturn(emptyList());

        RootDirProcessingResult rootDirProcessingResult = rootDirProcessingTask.call();

        verify(fileNamesRetriever, only()).retrieve(rootDir);
        verify(fileNamesRetriever, only()).retrieve(any());
        verify(fileReader, times(filesInRootDir.size())).read(any());

        assertEquals(filesInRootDir.size(), rootDirProcessingResult.getWordsByFile().size());
    }

    @Test
    public void callShouldReturnCorrectResultTest() throws Exception {
        String rootDir = RandomString.make();
        this.rootDirProcessingTask = new RootDirProcessingTask(
                rootDir,
                fileNamesRetriever,
                fileReader);

        List<String> filesInRootDir = asList(RandomString.make());
        when(fileNamesRetriever.retrieve(rootDir)).thenReturn(filesInRootDir);

        List<String> fakeFileData = new ArrayList<String>() {{
            add("cc aa bb CC cC Cc");
            add("aa bb ");
            add("dd");
            add("a2a b-b a@a bЯb ee");
            add("zz,yy,ww.qq.pp");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                stringBuilder.append(" ").append(RandomStringUtils.randomAscii(40).replaceAll("\\s", ""));
                add(RandomStringUtils.randomAscii(40).replaceAll("\\s", ""));
            }
            add(stringBuilder.toString());
        }};
        when(fileReader.read(filesInRootDir.get(0))).thenReturn(fakeFileData);

        RootDirProcessingResult rootDirProcessingResult = rootDirProcessingTask.call();

        assertEquals(rootDir, rootDirProcessingResult.getRootDirName());
        assertEquals(filesInRootDir.size(), rootDirProcessingResult.getWordsByFile().entrySet().size());
        assertEquals(new TreeSet(asList("aa", "bb", "cc", "dd", "ee")),
                new TreeSet(rootDirProcessingResult.getWordsByFile().get(filesInRootDir.get(0))));
    }

}