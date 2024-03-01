package com.hin.spatial.postgis.repo;

import com.hin.spatial.postgis.model.ObjectWithGeometry;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectWithGeometryRepository extends JpaRepository<ObjectWithGeometry, Long> {
    @Query(value = "SELECT s from com.hin.spatial.postgis.model.ObjectWithGeometry s where ST_DistanceSphere(s.geometry, :p) < :distanceM")
    List<ObjectWithGeometry> findNearWithinDistance2(Point p, double distanceM);

//    @Query( "Insert into com.hin.spatial.postgis.model.ArcDemo (geometry, remark) VALUES ( ST_GeomFromEWKT(:ewkt), :remark)")
//    Integer insertArcDemo(String ewkt, String remark);
}
