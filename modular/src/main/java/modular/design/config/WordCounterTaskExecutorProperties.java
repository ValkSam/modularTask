package modular.design.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("executor")
public class WordCounterTaskExecutorProperties {

    private Integer startDelaySeconds;
    private Integer periodSeconds;
    private Integer taskThreads;

}
