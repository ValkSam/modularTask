package modular.design;

import modular.design.service.WordCounterTaskLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@ConditionalOnMissingClass("org.springframework.test.context.junit4.SpringJUnit4ClassRunner")
public class ModularApplication implements CommandLineRunner {

    @Autowired
    private WordCounterTaskLauncher wordCounterTaskLauncher;

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ModularApplication.class)
                .properties("spring.config.name="
                        + "modular")
                .build()
                .run(args);
    }

    @Override
    public void run(String... args) {
        wordCounterTaskLauncher.launch();
    }

}
