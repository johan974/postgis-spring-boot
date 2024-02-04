package com.hin.spatial.postgis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "us_cities")
public class City {

	@Id
	@Column(name="id")
	private long id;
	
	@Column(name="pop_2010")
	private long population2010;
	
	@Column(name="elev_in_ft")
	private long altitude;

	@Column(name="state")
	private String state;

	@JsonIgnore
	@Column(name = "wkb_geometry", columnDefinition = "geometry(Point,4326)")
	private Point geom;

}
