package com.hin.spatial.postgis.repo;

import com.hin.spatial.postgis.model.ObjectWithGeometry;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKBWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class PgRepository {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public PgRepository( DataSource pgDataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = pgDataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertBySql(final String insertSql) throws SQLException {
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(insertSql)) {
            stmt.execute();
        }
    }

    public void insertData(final String tableName, final Map<String, Object> data) throws SQLException {
        List<String> columnNameList = createColumnNameList(data);
        String query = generateQuery(tableName, columnNameList);
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(query)) {
            List<Object> values = createValueList(columnNameList, data);
            var i = 1;
            for (final Object value : values) {
                if (value instanceof Geometry geometry) {
                    WKBWriter writer = new WKBWriter();
                    stmt.setBytes(i, writer.write(geometry));
                } else {
                    stmt.setObject(i, value);
                }
                i++;
            }
            stmt.execute();
        }
    }

    public List<Map<String, Object>> loadJdbcData(Long id) {
        return jdbcTemplate.queryForList("select * from objectwithgeometries ");
    }

    public List<ObjectWithGeometry> loadHibernateData(Long id) {
        TypedQuery<ObjectWithGeometry> query = entityManager.createQuery(
                "select geoObject from ObjectWithGeometry geoObject where geoObject.id = :id", ObjectWithGeometry.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    String generateQuery(final String tableName, List<String> columnNameList) {
        String INSERT_QUERY = """
                insert into %s (%s)
                values(%s);
                """;
        String columnNames = String.join(",", columnNameList);
        String placeHolderNames = createPlaceHolderNames(columnNameList);
        return INSERT_QUERY.formatted(tableName, columnNames, placeHolderNames);
    }

    String createPlaceHolderNames(final List<String> columnNameList) {
        return columnNameList.stream().map(columnName -> "?").collect(Collectors.joining(","));
    }

    private List<String> createColumnNameList(final Map<String, Object> data) {
        return List.copyOf(data.keySet())
                .stream()
                .sorted()
                .toList();
    }

    private List<Object> createValueList(final List<String> columnNameList, final Map<String, Object> data) {
        return columnNameList
                .stream()
                .map(data::get)
                .toList();

    }
}
