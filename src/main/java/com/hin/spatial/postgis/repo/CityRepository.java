package com.hin.spatial.postgis.repo;

import com.hin.spatial.postgis.model.City;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    // WRONG - GIVES NO RESULTS and is TERRIBLY SLOW ... ;-(
//    @Query(value = "SELECT * from us_cities where ST_DistanceSphere(geom, :p) < :distanceM", nativeQuery = true)
//    List<City> findNearWithinDistance(Point p, double distanceM);

    @Query(value = "SELECT s from com.hin.spatial.postgis.model.City s where ST_DistanceSphere(s.geom, :p) < :distanceM")
    List<City> findNearWithinDistance2(Point p, double distanceM);

    @Query( "Select s from com.hin.spatial.postgis.model.City s where ST_DistanceSphere( s.geom, :filter) < :distance")
    List<City> findNearWithinDistance(Point filter, double distance);


    @Query("Select s from com.hin.spatial.postgis.model.City s where intersects( s.geom, :filter) = true")
    List<City> findItemsIntersects(@Param("filter") Geometry filter);

    @Query("Select s from com.hin.spatial.postgis.model.City s where ST_Intersects( s.geom, :filter) = true")
    List<City> findItemsIntersects2(@Param("filter") Geometry filter);

}