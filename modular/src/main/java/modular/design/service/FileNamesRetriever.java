package modular.design.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FileNamesRetriever {

    private final static String[] FILE_EXTENSION_TO_DO = {"txt", "TXT"};

    public List<String> retrieve(String rootDirName) throws IOException {
        List<String> fileNames = new ArrayList<>();
        File rootDir = new File(rootDirName);
        if (!rootDir.exists()) {
            return fileNames;
        }
        for (File file : rootDir.listFiles()) {
            if (file.isDirectory()) {
                fileNames.addAll(retrieve(file.getAbsolutePath()));
            } else if (FilenameUtils.isExtension(file.getName(), FILE_EXTENSION_TO_DO)) {
                fileNames.add(file.getAbsolutePath());
                log.debug("retrieved file: " + file.getAbsolutePath());
            }
        }
        return fileNames;
    }

}
