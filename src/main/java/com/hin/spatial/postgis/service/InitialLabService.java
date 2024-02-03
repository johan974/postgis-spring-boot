package com.hin.spatial.postgis.service;

import com.hin.spatial.postgis.model.SpatialLab;
import com.hin.spatial.postgis.repo.SpatialLabRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class InitialLabService implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private SpatialLabRepository spatialLabRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if( spatialLabRepository.count() >= 1) {
            log.info( "There is already a spatial lab entity");
            return ;
        }

        SpatialLab spatialLab = new SpatialLab();
        GeometryFactory gf = new GeometryFactory();

        Point point = gf.createPoint( new Coordinate( 4, 5));
        spatialLab.setPoint( point);

        Coordinate[] coordinates = new Coordinate[] { new Coordinate( 39, 33), new Coordinate( 46, 23)};
        MultiPoint multiPoint = gf.createMultiPointFromCoords( coordinates);
        spatialLab.setMultiPoint( multiPoint);

        Coordinate[] coordinates1 = new Coordinate[] { new Coordinate( 39, 33), new Coordinate( 46, 23)};
        LineString lineString1 = gf.createLineString( coordinates1);
        spatialLab.setLineString( lineString1);

        Coordinate[] coordinates2 = new Coordinate[] { new Coordinate( 26, 38), new Coordinate( 30, 37), new Coordinate( 40.5, 39.785)};
        LineString lineString2 = gf.createLineString( coordinates2);
        LineString[] lineStrings = new LineString[] { lineString1, lineString2};
        MultiLineString multiLineString = gf.createMultiLineString( lineStrings);
        spatialLab.setMultiLineString( multiLineString);

        Coordinate[] polygonCoordinates = new Coordinate[] { new Coordinate( 39, 33), new Coordinate( 46, 23),
                new Coordinate( 40, 27), new Coordinate( 39, 33)};
        Coordinate[] holeCoordinates = new Coordinate[] { new Coordinate( 40, 34), new Coordinate( 45, 24),
                new Coordinate( 41, 26), new Coordinate( 40, 34)};
        LinearRing hole = gf.createLinearRing( holeCoordinates);
        LinearRing[] holes = new LinearRing[] { hole};
        LinearRing shell = gf.createLinearRing( polygonCoordinates);
        Polygon polygonWithHole = gf.createPolygon( shell, holes);
        Polygon polygon = gf.createPolygon( polygonCoordinates);
        spatialLab.setPolygon( polygon);

        Polygon[] polygons = new Polygon[] { polygon, polygonWithHole};
        MultiPolygon multiPolygon = gf.createMultiPolygon( polygons);
        spatialLab.setMultiPolygon( multiPolygon);

        Geometry[] geometries = new Geometry[] { point, multiLineString, polygon, multiPolygon};
        GeometryCollection geometryCollection = gf.createGeometryCollection( geometries);
        spatialLab.setGeometryCollection( geometryCollection);

        spatialLabRepository.save( spatialLab);
    }
}
