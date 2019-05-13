package modular.design.repository;

import modular.design.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.toStringArray;

@Repository
public class ResultRepository {

    private final String SELECT_BY_ID = "SELECT * "
            + " FROM result "
            + " WHERE id = :id";

    private final String INSERT = "INSERT "
            + " INTO result "
            + " (dir_id, file_name, words) "
            + " VALUES (:dir_id, :file_name, :words)";

    private final String DELETE_BY_DIR_ID = "DELETE "
            + " FROM result "
            + " WHERE dir_id = :dir_id";

    private final NamedParameterJdbcTemplate parameterJdbcTemplate;
    private final ConversionService conversionService;

    @Autowired
    public ResultRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                            ConversionService conversionService) {
        this.parameterJdbcTemplate = namedParameterJdbcTemplate;
        this.conversionService = conversionService;
    }

    @Transactional(readOnly = true)
    public Optional<Result> findById(Long id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        BeanPropertyRowMapper<Result> beanPropertyRowMapper = new BeanPropertyRowMapper<>(Result.class);
        beanPropertyRowMapper.setConversionService(conversionService);
        try {
            return ofNullable(parameterJdbcTemplate.queryForObject(SELECT_BY_ID, params, beanPropertyRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return empty();
        }
    }

    @Transactional
    public Optional<Result> insert(Result result) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("dir_id", result.getDirId())
                .addValue("file_name", result.getFileName())
                .addValue("words", isNull(result.getWords()) ? new String[]{} : toStringArray(result.getWords()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (parameterJdbcTemplate.update(INSERT, params, keyHolder, new String[]{"id"}) == 0) {
            return empty();
        }
        result.setId(keyHolder.getKey().longValue());
        return of(result);
    }

    @Transactional
    public int deleteByDirId(Long dirId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("dir_id", dirId);
        return parameterJdbcTemplate.update(DELETE_BY_DIR_ID, params);
    }

}
