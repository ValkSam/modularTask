package modular.design.config;

import modular.design.converter.PgArrayToStringListConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
@ComponentScan("modular.design")
public class DatabaseConfig {

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Autowired JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean
    public ConversionService conversionService(@Autowired PgArrayToStringListConverter pgArrayToStringListConverter) {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(pgArrayToStringListConverter);
        return conversionService;
    }

}
