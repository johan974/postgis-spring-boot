package com.hin.spatial.postgis.utils;

import org.locationtech.jts.geom.*;

public class GeoUtils {
    private GeoUtils() {}
    private static final GeometryFactory gf = new GeometryFactory( new PrecisionModel(),4326);

    public static Polygon createPolygon( double lowLeftX, double lowLeftY, double upRightX, double upRightY) {
        Coordinate[] polygonCoordinates = new Coordinate[] {
                new Coordinate( lowLeftX, lowLeftY),
                new Coordinate( upRightX, lowLeftY),
                new Coordinate( upRightX, upRightY),
                new Coordinate( lowLeftX, upRightY),
                new Coordinate( lowLeftX, lowLeftY)};
        return gf.createPolygon( gf.createLinearRing( polygonCoordinates));
    }
}
