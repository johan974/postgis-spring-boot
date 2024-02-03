package com.hin.spatial.postgis.model;

import org.locationtech.jts.geom.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table( name = "spatiallab")
public class SpatialLab implements Serializable {
    @Id
    private Long id;

    @Column( name = "point")
    private Point point;

    @Column( name = "multipoint")
    private MultiPoint multiPoint;

    @Column( name = "linestring")
    private LineString lineString;

    @Column( name = "multilinestring")
    private MultiLineString multiLineString;

    @Column( name = "polygon")
    private Polygon polygon;

    @Column( name = "multipolygon")
    private MultiPolygon multiPolygon;

    @Column( name = "geometrycollection")
    private GeometryCollection geometryCollection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public MultiPoint getMultiPoint() {
        return multiPoint;
    }

    public void setMultiPoint(MultiPoint multiPoint) {
        this.multiPoint = multiPoint;
    }

    public LineString getLineString() {
        return lineString;
    }

    public void setLineString(LineString lineString) {
        this.lineString = lineString;
    }

    public MultiLineString getMultiLineString() {
        return multiLineString;
    }

    public void setMultiLineString(MultiLineString multiLineString) {
        this.multiLineString = multiLineString;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public MultiPolygon getMultiPolygon() {
        return multiPolygon;
    }

    public void setMultiPolygon(MultiPolygon multiPolygon) {
        this.multiPolygon = multiPolygon;
    }

    public GeometryCollection getGeometryCollection() {
        return geometryCollection;
    }

    public void setGeometryCollection(GeometryCollection geometryCollection) {
        this.geometryCollection = geometryCollection;
    }
}
