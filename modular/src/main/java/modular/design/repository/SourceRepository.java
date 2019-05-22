package modular.design.repository;

import modular.design.model.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Repository
public class SourceRepository {

    private final String SELECT_ALL = "SELECT * "
            + " FROM source ";

    private final String SELECT_BY_DIR = "SELECT * "
            + " FROM source "
            + " WHERE dir = :dir";

    private final String INSERT = "INSERT "
            + " INTO source "
            + " (dir, last_scanned) "
            + " VALUES (:dir, :last_scanned)";

    private final String UPDATE = "UPDATE "
            + " source "
            + " SET dir = :dir, last_scanned = :last_scanned "
            + " WHERE id = :id";

    private final NamedParameterJdbcTemplate parameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SourceRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                            JdbcTemplate jdbcTemplate) {
        this.parameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Optional<Source> findByDir(String dir) {
        SqlParameterSource params = new MapSqlParameterSource("dir", dir);
        try {
            return ofNullable(parameterJdbcTemplate.queryForObject(SELECT_BY_DIR, params, new BeanPropertyRowMapper<>(Source.class)));
        } catch (EmptyResultDataAccessException e) {
            return empty();
        }
    }

    @Transactional(readOnly = true)
    public List<Source> findAll() {
        return jdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(Source.class));
    }

    @Transactional
    public Optional<Source> insert(Source source) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("dir", source.getDir())
                .addValue("last_scanned", source.getLastScanned());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (parameterJdbcTemplate.update(INSERT, params, keyHolder, new String[]{"id"}) == 0) {
            return empty();
        }
        source.setId(keyHolder.getKey().longValue());
        return of(source);
    }

    @Transactional
    public Optional<Source> update(Source source) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", source.getId())
                .addValue("dir", source.getDir())
                .addValue("last_scanned", source.getLastScanned());
        if (parameterJdbcTemplate.update(UPDATE, params) == 0) {
            return empty();
        }
        return of(source);
    }

}
