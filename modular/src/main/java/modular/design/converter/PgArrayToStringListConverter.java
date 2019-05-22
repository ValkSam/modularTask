package modular.design.converter;

import modular.design.exception.PgConversionException;
import org.postgresql.jdbc.PgArray;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
public class PgArrayToStringListConverter implements Converter<PgArray, List<String>> {

    @Nullable
    @Override
    public List<String> convert(PgArray source) {
        try {
            return Arrays.asList((String[]) source.getArray());
        } catch (SQLException e) {
            throw new PgConversionException(e);
        }
    }

}
