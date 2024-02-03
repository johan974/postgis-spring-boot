package com.hin.spatial.postgis.repo;

import com.hin.spatial.postgis.model.City;
import com.hin.spatial.postgis.model.SpatialLab;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpatialLabRepository extends JpaRepository<SpatialLab, Long>{
}
