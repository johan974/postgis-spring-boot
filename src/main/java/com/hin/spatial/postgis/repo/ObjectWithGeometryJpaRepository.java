package com.hin.spatial.postgis.repo;

import com.hin.spatial.postgis.model.ObjectWithGeometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectWithGeometryJpaRepository extends JpaRepository<ObjectWithGeometry, Long> {
}