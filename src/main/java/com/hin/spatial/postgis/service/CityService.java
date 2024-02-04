package com.hin.spatial.postgis.service;

import java.util.List;

import com.hin.spatial.postgis.model.MyPolygonDto;
import org.locationtech.jts.geom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hin.spatial.postgis.model.City;
import com.hin.spatial.postgis.repo.CityRepository;
import lombok.extern.slf4j.Slf4j;

import static com.hin.spatial.postgis.utils.GeoUtils.createPolygon;

@Service
@Slf4j
public class CityService {

	@Autowired
	private CityRepository repo;
	
	// WGS-84 SRID
	private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
	
	public Page<City> findAll(Pageable page){
		return repo.findAll(page);
	}
	
	public List<City> findAround(double lat, double lon, double distanceM){
		log.info("Looking for city around ({},{}) withing {} meters", lat, lon, distanceM);
		Point p = factory.createPoint(new Coordinate(lon, lat));
		List<City> cities = repo.findNearWithinDistance(p, distanceM);
		return cities;
	}

	public List<City> findAround2(double lat, double lon, double distanceM){
		log.info("Looking for city around ({},{}) withing {} meters", lat, lon, distanceM);
		Point p = factory.createPoint(new Coordinate(lon, lat));
		List<City> cities = repo.findNearWithinDistance2(p, distanceM);
		return cities;
	}

	public List<City> findIntersects(MyPolygonDto myPolygon){
		Polygon polygon = createPolygon( myPolygon.getLowerLeftX(), myPolygon.getLowerLeftY(),
				myPolygon.getUpperRightX(), myPolygon.getUpperRightY());
		List<City> cities = repo.findItemsIntersects(polygon);
		return cities;
	}
}
