package com.hin.spatial.postgis.model;

import lombok.Data;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table( name = "objectwithgeometries")
public class ObjectWithGeometry implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "geometry")
    private Geometry geometry;

    @Column( name = "remarks")
    private String remark;
}
