package com.hin.spatial.postgis.service;

import com.hin.spatial.postgis.model.ObjectWithGeometry;
import com.hin.spatial.postgis.repo.ObjectWithGeometryJpaRepository;
import com.hin.spatial.postgis.repo.PgRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ObjectWithGeometryService {
    private final PgRepository pgRepository;
    private final ObjectWithGeometryJpaRepository objectWithGeometryJpaRepository;

    public ObjectWithGeometryService(PgRepository pgRepository, ObjectWithGeometryJpaRepository objectWithGeometryJpaRepository) {
        this.pgRepository = pgRepository;
        this.objectWithGeometryJpaRepository = objectWithGeometryJpaRepository;
    }

    public void executeSql(String sql) throws SQLException {
        pgRepository.insertBySql( sql);
    }

    public List<ObjectWithGeometry> findAll() {
        return objectWithGeometryJpaRepository.findAll();
    }
}
