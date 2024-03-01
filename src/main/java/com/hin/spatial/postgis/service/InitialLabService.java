package com.hin.spatial.postgis.service;

import com.hin.spatial.postgis.model.ObjectWithGeometry;
import com.hin.spatial.postgis.model.SpatialLab;
import com.hin.spatial.postgis.repo.SpatialLabRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Order(1)
public class InitialLabService implements ApplicationListener<ApplicationReadyEvent> {
    private final SpatialLabRepository spatialLabRepository;
    private final ObjectWithGeometryService objectWithGeometryService;

    public InitialLabService(SpatialLabRepository spatialLabRepository,
                             ObjectWithGeometryService objectWithGeometryService) {
        this.spatialLabRepository = spatialLabRepository;
        this.objectWithGeometryService = objectWithGeometryService;
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createSpatialLabObject();
        createArcs();
    }

    private void createArcs() {
        try {
            log.info("Creating arcs ... ");
            List<ObjectWithGeometry> geoObjects = objectWithGeometryService.findAll();
            log.info( "Step 1: Number of objects: {}", geoObjects == null ? 0 : geoObjects.size());

            objectWithGeometryService.executeSql( "delete from objectwithgeometries ");
            geoObjects = objectWithGeometryService.findAll();
            log.info( "Step 2: Number of objects: {}", geoObjects == null ? 0 : geoObjects.size());

            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 2, ST_SetSRID( ST_CurveToLine('CIRCULARSTRING(29.8925 40.36667,29.560000 40.511667,29.27528 40.31667)'), 4326), 'remark-1');
                            """);
            geoObjects = objectWithGeometryService.findAll();
            log.info( "Step 3: Number of objects: {}", geoObjects == null ? 0 : geoObjects.size());

            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 3, ST_SetSRID( ST_CurveToLine('CIRCULARSTRING(29.8925 40.36667,29.628611 40.015000,29.27528 40.31667)'), 4326), 'remark-2');
                            """);
            geoObjects = objectWithGeometryService.findAll();
            log.info( "Step 4: Number of objects: {}", geoObjects == null ? 0 : geoObjects.size());

            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 4, ST_SetSRID( ST_GeomFromEWKT( ST_CurveToLine( 'CIRCULARSTRING(29.8925 41.36667,29.628611 41.015000,29.27528 41.31667)')), 4326), 'remark-3');
                            """);
            geoObjects = objectWithGeometryService.findAll();
            log.info( "Step 5: Number of objects: {}", geoObjects == null ? 0 : geoObjects.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createSpatialLabObject() {
        log.info("Filling one data row with all geo stuff ... ");
        if (spatialLabRepository.count() >= 1) {
            log.info("There is already a spatial lab entity");
            return;
        }

        SpatialLab spatialLab = new SpatialLab();
        GeometryFactory gf = new GeometryFactory();

        Point point = gf.createPoint(new Coordinate(4, 5));
        spatialLab.setId(123L);
        spatialLab.setPoint(point);

        Coordinate[] coordinates = new Coordinate[]{new Coordinate(39, 33), new Coordinate(46, 23)};
        MultiPoint multiPoint = gf.createMultiPointFromCoords(coordinates);
        spatialLab.setMultiPoint(multiPoint);

        Coordinate[] coordinates1 = new Coordinate[]{new Coordinate(39, 33), new Coordinate(46, 23)};
        LineString lineString1 = gf.createLineString(coordinates1);
        spatialLab.setLineString(lineString1);

        Coordinate[] coordinates2 = new Coordinate[]{new Coordinate(26, 38), new Coordinate(30, 37), new Coordinate(40.5, 39.785)};
        LineString lineString2 = gf.createLineString(coordinates2);
        LineString[] lineStrings = new LineString[]{lineString1, lineString2};
        MultiLineString multiLineString = gf.createMultiLineString(lineStrings);
        spatialLab.setMultiLineString(multiLineString);

        Coordinate[] polygonCoordinates = new Coordinate[]{new Coordinate(39, 33), new Coordinate(46, 23),
                new Coordinate(40, 27), new Coordinate(39, 33)};
        Coordinate[] holeCoordinates = new Coordinate[]{new Coordinate(40, 34), new Coordinate(45, 24),
                new Coordinate(41, 26), new Coordinate(40, 34)};
        LinearRing hole = gf.createLinearRing(holeCoordinates);
        LinearRing[] holes = new LinearRing[]{hole};
        LinearRing shell = gf.createLinearRing(polygonCoordinates);
        Polygon polygonWithHole = gf.createPolygon(shell, holes);
        Polygon polygon = gf.createPolygon(polygonCoordinates);
        spatialLab.setPolygon(polygon);

        Polygon[] polygons = new Polygon[]{polygon, polygonWithHole};
        MultiPolygon multiPolygon = gf.createMultiPolygon(polygons);
        spatialLab.setMultiPolygon(multiPolygon);

        Geometry[] geometries = new Geometry[]{point, multiLineString, polygon, multiPolygon};
        GeometryCollection geometryCollection = gf.createGeometryCollection(geometries);
        spatialLab.setGeometryCollection(geometryCollection);

        spatialLabRepository.save(spatialLab);
        log.info("Filled one data row with all geo stuff ... ");
    }
}
