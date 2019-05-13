package modular.design.service;

import modular.design.config.WordCounterTaskFileProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class FileReader {

    @Autowired
    private final WordCounterTaskFileProperties wordCounterTaskFileProperties;

    public FileReader(WordCounterTaskFileProperties wordCounterTaskFileProperties) {
        this.wordCounterTaskFileProperties = wordCounterTaskFileProperties;
    }

    public List<String> read(String fileName) throws IOException {
        return Files.readAllLines(Paths.get(fileName), Charset.forName(wordCounterTaskFileProperties.getCharset()));
    }

}
