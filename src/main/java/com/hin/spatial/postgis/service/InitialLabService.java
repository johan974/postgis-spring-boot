package com.hin.spatial.postgis.service;

import com.hin.spatial.postgis.model.ObjectWithGeometry;
import com.hin.spatial.postgis.model.SpatialLab;
import com.hin.spatial.postgis.repo.SpatialLabRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
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
            log.info( "Step 0: Delete all objects");
            objectWithGeometryService.executeSql( "delete from objectwithgeometries where id < 10");

            log.info( "Step 1: Insert CurveToLine");
            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 1, ST_SetSRID( ST_CurveToLine('CIRCULARSTRING(29.8925 40.36667,29.560000 40.511667,29.27528 40.31667)'), 4326), 'curve-to-line-1');
                            """);

            log.info( "Step 2: Another CurveToLine");
            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 2, ST_SetSRID( ST_CurveToLine('CIRCULARSTRING(29.8925 40.36667,29.628611 40.015000,29.27528 40.31667)'), 4326), 'curve-to-line-2');
                            """);

            log.info( "Step 3: Insert EWKT/curveToLine");
            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 3, ST_SetSRID( ST_GeomFromEWKT( ST_CurveToLine( 'CIRCULARSTRING(29.8925 41.36667,29.628611 41.015000,29.27528 41.31667)')), 4326), 'remark-3');
                            """);

            log.info( "Step 4: Insert EWKT without CurvedToLine: error Unsupported WKB type code: 8!");
            // This does not work:
            //    INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 4, ST_SetSRID( ST_GeomFromEWKT( 'CIRCULARSTRING(29.8925 41.36667,29.628611 41.015000,29.27528 41.31667)'), 4326), 'remark-4');
            // Curcularline ... wordt niet ondersteund door WKB? https://en.wikipedia.org/wiki/Well-known_text_representation_of_geometry
            ///   Unsupported WKB type code: 8
            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 4, ST_SetSRID( ST_GeomFromEWKT( ST_CurveToLine( 'CIRCULARSTRING(29.8925 41.36667,29.628611 41.015000,29.27528 41.31667)')), 4326), 'remark-3');
                            """);

            // Dit verstoort de verwerking
//            log.info( "Step 4b: Insert EWKT without CurvedToLine: error Unsupported WKB type code: 8!");
//            objectWithGeometryService.executeSql("""
//            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 4000, ST_GeomFromEWKT( 'CIRCULARSTRING(29.8925 41.36667,29.628611 41.015000,29.27528 41.31667)'), 'remark-3b');
//                            """);
//
//            try {
//                List<ObjectWithGeometry> geoObjects = objectWithGeometryService.findAll();
//            } catch (Exception e) {
//                log.info( "KNOWN ERROR: WkbDecodeException: Unsupported WKB type code: 8");
//                objectWithGeometryService.executeSql( "delete from objectwithgeometries where id = 4000");
//            }


            // Will result in error:
            // Supported Geolatte WKB dialect types:
//                final public static int WKB_POINT = 1;
//                final public static int WKB_LINESTRING = 2;
//                final public static int WKB_POLYGON = 3;
//                final public static int WKB_MULTIPOINT = 4;
//                final public static int WKB_MULTILINESTRING = 5;
//                final public static int WKB_MULTIPOLYGON = 6;
//                final public static int WKB_GEOMETRYCOLLECTION = 7;

            log.info( "Step 5: minimal change");
            // 5 - Insert verstrookt object
            // Resultaten:
            //  Object1.curvedToLine =topo*= object5.lineStrings => true
            //  Object1.curvedToLine =exact= object5.lineStrings => true
            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 5,  ST_SetSRID( ST_GeomFromText( 'LineString (29.89249999999999829 40.36666999999999916, 29.88144613624102064 40.3807825848138009, 29.86971311562948372 40.39433578301414229, 29.85732920403972557 40.40729694375945513, 29.84432423539947621 40.41963484247823146, 29.83072953981728404 40.43131975609178852, 29.81657786810556487 40.44232353461974583, 29.80190331288101291 40.45261966899582262, 29.78674122643250044 40.46218335493046681, 29.77112813555432425 40.47099155266658954, 29.75510165354995706 40.47902304248432159, 29.73870038961830176 40.48625847582123072, 29.72196385584074818 40.49268042188465699, 29.70493237199311665 40.49827340964402822, 29.68764696841177297 40.5030239651019528, 29.67014928714797151 40.50692064375423485, 29.6524814816484934 40.5099540581606874, 29.63468611520430329 40.5121169005603079, 29.61680605841184999 40.51340396047631742, 29.59888438589405979 40.51381213726864416, 29.58096427252977989 40.51334044760364606, 29.56308888944172253 40.51199002782303182, 29.54530129999344368 40.50976413120634589, 29.52764435604593629 40.50666812013350437, 29.51016059472372888 40.50270945316634652, 29.49289213593925041 40.49789766708035188, 29.47588058092224728 40.49224435388964594, 29.45916691199878912 40.48576313292089424, 29.44279139386126332 40.47846961800308918, 29.42679347656718747 40.47038137985250472, 29.41121170050057287 40.46151790374329948, 29.39608360352476524 40.45190054256575252, 29.38144563055044856 40.44155246538534243, 29.36733304573665038 40.43049860162636122, 29.35377984753630187 40.41876558101482431, 29.34081868679099259 40.40638166942506615, 29.32848078807221626 40.39337670078481324, 29.3167958744586592 40.37978200520262817, 29.30579209593069834 40.36563033349090546, 29.29549596155462154 40.35095577826635349, 29.28593227561997736 40.33579369181784102, 29.27712407788386173 40.32018060093966483, 29.27527999999999864 40.31667000000000201)'), 4326), 'linestring-5');
                            """);

            log.info( "Step 6: very small change");
            // 6 - minimal change verstrookte linestring
            //  Original: 40.3166700000000020*1*
            //  Newest:   40.3166710000000020*2*
            // Resultaten:
            //  Object1.curvedToLine =topo*= object6.lineStrings => true
            //  Object1.curvedToLine =exact= object6.lineStrings => true
            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 6,  ST_SetSRID( ST_GeomFromText( 'LineString (29.89249999999999829 40.36666999999999916, 29.88144613624102064 40.3807825848138009, 29.86971311562948372 40.39433578301414229, 29.85732920403972557 40.40729694375945513, 29.84432423539947621 40.41963484247823146, 29.83072953981728404 40.43131975609178852, 29.81657786810556487 40.44232353461974583, 29.80190331288101291 40.45261966899582262, 29.78674122643250044 40.46218335493046681, 29.77112813555432425 40.47099155266658954, 29.75510165354995706 40.47902304248432159, 29.73870038961830176 40.48625847582123072, 29.72196385584074818 40.49268042188465699, 29.70493237199311665 40.49827340964402822, 29.68764696841177297 40.5030239651019528, 29.67014928714797151 40.50692064375423485, 29.6524814816484934 40.5099540581606874, 29.63468611520430329 40.5121169005603079, 29.61680605841184999 40.51340396047631742, 29.59888438589405979 40.51381213726864416, 29.58096427252977989 40.51334044760364606, 29.56308888944172253 40.51199002782303182, 29.54530129999344368 40.50976413120634589, 29.52764435604593629 40.50666812013350437, 29.51016059472372888 40.50270945316634652, 29.49289213593925041 40.49789766708035188, 29.47588058092224728 40.49224435388964594, 29.45916691199878912 40.48576313292089424, 29.44279139386126332 40.47846961800308918, 29.42679347656718747 40.47038137985250472, 29.41121170050057287 40.46151790374329948, 29.39608360352476524 40.45190054256575252, 29.38144563055044856 40.44155246538534243, 29.36733304573665038 40.43049860162636122, 29.35377984753630187 40.41876558101482431, 29.34081868679099259 40.40638166942506615, 29.32848078807221626 40.39337670078481324, 29.3167958744586592 40.37978200520262817, 29.30579209593069834 40.36563033349090546, 29.29549596155462154 40.35095577826635349, 29.28593227561997736 40.33579369181784102, 29.27712407788386173 40.32018060093966483, 29.27527999999999864 40.31667000000000202)'), 4326), 'linestring-5');
                            """);

            log.info( "Step 7: minor change");
            // 7 - minimal-plus change verstrookte linestring:
            //  Original: 40.31667*0*00000000201
            //  Newest:   40.31667*1*00000000201
            // Resultaten:
            //  Object1.curvedToLine =topo*= object7.lineStrings => false
            //  Object1.curvedToLine =exact= object7.lineStrings => false
            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 7,  ST_SetSRID( ST_GeomFromText( 'LineString (29.89249999999999829 40.36666999999999916, 29.88144613624102064 40.3807825848138009, 29.86971311562948372 40.39433578301414229, 29.85732920403972557 40.40729694375945513, 29.84432423539947621 40.41963484247823146, 29.83072953981728404 40.43131975609178852, 29.81657786810556487 40.44232353461974583, 29.80190331288101291 40.45261966899582262, 29.78674122643250044 40.46218335493046681, 29.77112813555432425 40.47099155266658954, 29.75510165354995706 40.47902304248432159, 29.73870038961830176 40.48625847582123072, 29.72196385584074818 40.49268042188465699, 29.70493237199311665 40.49827340964402822, 29.68764696841177297 40.5030239651019528, 29.67014928714797151 40.50692064375423485, 29.6524814816484934 40.5099540581606874, 29.63468611520430329 40.5121169005603079, 29.61680605841184999 40.51340396047631742, 29.59888438589405979 40.51381213726864416, 29.58096427252977989 40.51334044760364606, 29.56308888944172253 40.51199002782303182, 29.54530129999344368 40.50976413120634589, 29.52764435604593629 40.50666812013350437, 29.51016059472372888 40.50270945316634652, 29.49289213593925041 40.49789766708035188, 29.47588058092224728 40.49224435388964594, 29.45916691199878912 40.48576313292089424, 29.44279139386126332 40.47846961800308918, 29.42679347656718747 40.47038137985250472, 29.41121170050057287 40.46151790374329948, 29.39608360352476524 40.45190054256575252, 29.38144563055044856 40.44155246538534243, 29.36733304573665038 40.43049860162636122, 29.35377984753630187 40.41876558101482431, 29.34081868679099259 40.40638166942506615, 29.32848078807221626 40.39337670078481324, 29.3167958744586592 40.37978200520262817, 29.30579209593069834 40.36563033349090546, 29.29549596155462154 40.35095577826635349, 29.28593227561997736 40.33579369181784102, 29.27712407788386173 40.32018060093966483, 29.27527999999999864 40.31667100000000201)'), 4326), 'linestring-6');
                            """);

            // 8 - medium change verstrookte linestring
            //  Original: 40.441*5*5246538534243
            //  Newest:   40.441*6*5246538534243
            // Resultaten:
            //  Object1.curvedToLine =topo*= object8.lineStrings => false
            //  Object1.curvedToLine =exact= object8.lineStrings => false
            log.info( "Step 8: medium change");
            objectWithGeometryService.executeSql("""
            INSERT INTO objectwithgeometries (id,geometry,remarks) VALUES ( 8,  ST_SetSRID( ST_GeomFromText( 'LineString (29.89249999999999829 40.36666999999999916, 29.88144613624102064 40.3807825848138009, 29.86971311562948372 40.39433578301414229, 29.85732920403972557 40.40729694375945513, 29.84432423539947621 40.41963484247823146, 29.83072953981728404 40.43131975609178852, 29.81657786810556487 40.44232353461974583, 29.80190331288101291 40.45261966899582262, 29.78674122643250044 40.46218335493046681, 29.77112813555432425 40.47099155266658954, 29.75510165354995706 40.47902304248432159, 29.73870038961830176 40.48625847582123072, 29.72196385584074818 40.49268042188465699, 29.70493237199311665 40.49827340964402822, 29.68764696841177297 40.5030239651019528, 29.67014928714797151 40.50692064375423485, 29.6524814816484934 40.5099540581606874, 29.63468611520430329 40.5121169005603079, 29.61680605841184999 40.51340396047631742, 29.59888438589405979 40.51381213726864416, 29.58096427252977989 40.51334044760364606, 29.56308888944172253 40.51199002782303182, 29.54530129999344368 40.50976413120634589, 29.52764435604593629 40.50666812013350437, 29.51016059472372888 40.50270945316634652, 29.49289213593925041 40.49789766708035188, 29.47588058092224728 40.49224435388964594, 29.45916691199878912 40.48576313292089424, 29.44279139386126332 40.47846961800308918, 29.42679347656718747 40.47038137985250472, 29.41121170050057287 40.46151790374329948, 29.39608360352476524 40.45190054256575252, 29.38144563055044856 40.44165246538534243, 29.36733304573665038 40.43049860162636122, 29.35377984753630187 40.41876558101482431, 29.34081868679099259 40.40638166942506615, 29.32848078807221626 40.39337670078481324, 29.3167958744586592 40.37978200520262817, 29.30579209593069834 40.36563033349090546, 29.29549596155462154 40.35095577826635349, 29.28593227561997736 40.33579369181784102, 29.27712407788386173 40.32018060093966483, 29.27527999999999864 40.31667000000000201)'), 4326), 'linestring-7');
                            """);


            // TESTS: are EWKT / CurvedLines
            ObjectWithGeometry obj1curvedToLine = objectWithGeometryService.findById( 1L);
            ObjectWithGeometry obj5WithLineString = objectWithGeometryService.findById( 5L);
            ObjectWithGeometry obj6WithMinimalChangeInLineStrings = objectWithGeometryService.findById( 6L);
            ObjectWithGeometry obj7WithMinimalPlusChangeInLineStrings = objectWithGeometryService.findById( 7L);
            ObjectWithGeometry obj8WithMediumChangeInLineStrings = objectWithGeometryService.findById( 8L);
            log.info( "Object1.curvedToLine =topo*= object5.lineStrings => {}", obj1curvedToLine.getGeometry().equalsTopo( obj5WithLineString.getGeometry()));
            log.info( "Object1.curvedToLine =exact= object5.lineStrings => {}", obj1curvedToLine.getGeometry().equalsExact( obj5WithLineString.getGeometry()));
            // Resultaten:
            // Object1.curvedToLine =topo*= object5.lineStrings => true
            // Object1.curvedToLine =exact= object5.lineStrings => true
            GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
            log.info( "GeometryJSON: {}", geoJsonWriter.write( obj1curvedToLine.getGeometry()));

            log.info( "Object1.curvedToLine =topo*= object6.lineStrings => {}", obj1curvedToLine.getGeometry().equalsTopo( obj6WithMinimalChangeInLineStrings.getGeometry()));
            log.info( "Object1.curvedToLine =exact= object6.lineStrings => {}", obj1curvedToLine.getGeometry().equalsExact( obj6WithMinimalChangeInLineStrings.getGeometry()));
            // Resultaten:
            // Object1.curvedToLine =topo*= object6.lineStrings => true
            // Object1.curvedToLine =exact= object6.lineStrings => true

            log.info( "Object1.curvedToLine =topo*= object7.lineStrings => {}", obj1curvedToLine.getGeometry().equalsTopo( obj7WithMinimalPlusChangeInLineStrings.getGeometry()));
            log.info( "Object1.curvedToLine =exact= object7.lineStrings => {}", obj1curvedToLine.getGeometry().equalsExact( obj7WithMinimalPlusChangeInLineStrings.getGeometry()));
            // Resultaten:
            // Object1.curvedToLine =topo*= object7.lineStrings => true
            // Object1.curvedToLine =exact= object7.lineStrings => true

            log.info( "Object1.curvedToLine =topo*= object8.lineStrings => {}", obj1curvedToLine.getGeometry().equalsTopo( obj8WithMediumChangeInLineStrings.getGeometry()));
            log.info( "Object1.curvedToLine =exact= object8.lineStrings => {}", obj1curvedToLine.getGeometry().equalsExact( obj8WithMediumChangeInLineStrings.getGeometry()));
            // Resultaten:
            // Object1.curvedToLine =topo*= object8.lineStrings => false
            // Object1.curvedToLine =exact= object8.lineStrings => false

            // REad EWKT
//            ObjectWithGeometry obj4000Ewkt = objectWithGeometryService.findById( 4000L);
//            log.info( "Object1.curvedToLine =topo*= objectEwkt.lineStrings => {}", obj1curvedToLine.getGeometry().equalsTopo( obj4000Ewkt.getGeometry()));
//            log.info( "Object1.curvedToLine =exact= objectEwkt.lineStrings => {}", obj1curvedToLine.getGeometry().equalsExact( obj4000Ewkt.getGeometry()));

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
