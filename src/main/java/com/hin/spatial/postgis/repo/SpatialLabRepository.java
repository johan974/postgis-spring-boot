package com.hin.spatial.postgis.repo;

import com.hin.spatial.postgis.model.SpatialLab;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpatialLabRepository extends JpaRepository<SpatialLab, Long> {
    //@Query("Select s from SpatialLab s where intersects( s.polygon, :filter) = true")
    @Query("Select s from SpatialLab s where ST_intersects( s.polygon, :filter) = true")
    List<SpatialLab> findItemsIntersects(@Param("filter") Geometry filter);

    @Query( "Select s from SpatialLab s where ST_DistanceSphere( s.polygon, :filter) < :distance")
    List<SpatialLab> findNearWithinDistance( @Param("filter") Geometry filter, @Param("distance") Double distance);
}
