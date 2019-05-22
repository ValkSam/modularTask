package modular.design.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FileNamesRetrieverTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void retrieveTest() throws Exception {
        List<String> expectedFileList = new ArrayList<>();
        File rootFolder = temporaryFolder.newFolder("rootFolder");
        File subDirOne = new File(rootFolder, "subDirOne");
        File subDirTwo = new File(rootFolder, "subDirTwo");
        File subDirOneSubDirA = new File(rootFolder, "subDirOneSubDirA");
        File subDirOneSubDirB = new File(rootFolder, "subDirOneSubDirB");
        File subDirTwoSubDirA = new File(rootFolder, "subDirTwoSubDirA");
        File subDirTwoSubDirB = new File(rootFolder, "subDirTwoSubDirB");

        expectedFileList.addAll(createFilesSetWithOneValidAndOneInvalidFiles(rootFolder));
        expectedFileList.addAll(createFilesSetWithOneValidAndOneInvalidFiles(subDirOne));
        expectedFileList.addAll(createFilesSetWithOneValidAndOneInvalidFiles(subDirTwo));
        expectedFileList.addAll(createFilesSetWithOneValidAndOneInvalidFiles(subDirOneSubDirA));
        expectedFileList.addAll(createFilesSetWithOneValidAndOneInvalidFiles(subDirOneSubDirB));
        expectedFileList.addAll(createFilesSetWithOneValidAndOneInvalidFiles(subDirTwoSubDirA));
        expectedFileList.addAll(createFilesSetWithOneValidAndOneInvalidFiles(subDirTwoSubDirB));

        FileNamesRetriever fileNamesRetriever = new FileNamesRetriever();
        int expectedDirCreatedNumber = 7;
        List<String> foundFileList = fileNamesRetriever.retrieve(rootFolder.getAbsolutePath());
        assertEquals("Expected one valid file in each dir is found", expectedDirCreatedNumber, foundFileList.size());
        assertEquals(new TreeSet<>(expectedFileList), new TreeSet<>(foundFileList));
    }

    @Test
    public void retrieveIfRootDirNotExistsShouldReturnEmptyListTest() throws Exception {
        assertEquals(0, new FileNamesRetriever().retrieve("WrongRootFolder").size());
    }

    private List<String> createFilesSetWithOneValidAndOneInvalidFiles(File dir) throws IOException {
        List<String> filesWithValidExtension = new ArrayList<>();
        if (!dir.exists()) {
            dir.mkdir();
        }
        filesWithValidExtension.add(createFile(dir,
                new Random().nextBoolean() ? "fileWithValidExt.txt" : "fileWithValidExt.TXT").getAbsolutePath());
        createFile(dir, "fileWithInvalidExt.EXT");
        return filesWithValidExtension;
    }

    private File createFile(File dir, String fileName) throws IOException {
        File file = new File(dir, fileName);
        file.createNewFile();
        return file;
    }

}