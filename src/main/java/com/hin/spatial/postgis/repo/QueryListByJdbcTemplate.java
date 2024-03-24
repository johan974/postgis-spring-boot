package com.hin.spatial.postgis.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QueryListByJdbcTemplate {
    private final JdbcTemplate jdbcTemplate;

    public QueryListByJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> readData( int id) {
        return jdbcTemplate.queryForList( String.format( "select id,geometry,remarks from objectwithgeometries where id = %d", id));
    }

    public List<Map<String, Object>> readDataGeometryAsText( int id) {
        return jdbcTemplate.queryForList( String.format( "select id,ST_AsText(geometry),remarks from objectwithgeometries where id = %d", id));
    }

}
