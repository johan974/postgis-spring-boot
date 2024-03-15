package com.hin.spatial.postgis.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hin.spatial.postgis.model.MyPolygonDto;
import com.hin.spatial.postgis.model.SpatialLab;
import com.hin.spatial.postgis.repo.QueryListByJdbcTemplate;
import com.hin.spatial.postgis.repo.SpatialLabRepository;
import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeometry;
import org.hibernate.spatial.dialect.postgis.PGGeographyJdbcType;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.postgresql.util.PGobject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.hin.spatial.postgis.model.City;
import com.hin.spatial.postgis.service.CityService;

import static com.hin.spatial.postgis.utils.GeoUtils.createPolygon;

@Slf4j
@RestController
public class GeoController {
	private final CityService service;
	private final SpatialLabRepository spatialLabRepository;
	private final QueryListByJdbcTemplate queryListByJdbcTemplate;

    public GeoController(CityService service, SpatialLabRepository spatialLabRepository, QueryListByJdbcTemplate queryListByJdbcTemplate) {
        this.service = service;
        this.spatialLabRepository = spatialLabRepository;
        this.queryListByJdbcTemplate = queryListByJdbcTemplate;
    }

    @GetMapping
	public Page<City> getCityPage(Pageable pageable){
		return service.findAll(pageable);
	}

	// DON'T USE THIS ONE ... see the service and repo ...
//	@GetMapping("/cities/{lat}/{lon}/{distanceM}")
//	public List<City> getCityNear( @PathVariable double lat, @PathVariable double lon, @PathVariable double distanceM){
//		return service.findAround(lat, lon, distanceM);
//	}

	@GetMapping("/cities/{lat}/{lon}/{distanceM}")
	public List<City> getCityNear2( @PathVariable double lat, @PathVariable double lon, @PathVariable double distanceM){
		return service.findAround2(lat, lon, distanceM);
	}

	@GetMapping( "/queryforlist/{id}")
	public String getCityNear2( @PathVariable int id) {
		List<Map<String, Object>> objects = queryListByJdbcTemplate.readData( id);
		objects.forEach( r -> {
			PGobject geometryObject = (PGobject) r.get( "geometry");
			// Gives: unknow wkbtype 8: byte[] geom = WKBReader.hexToBytes( geometryObject.getValue() );
            try {
				PGgeometry geo = new PGgeometry(geometryObject.getValue());
                log.info( "Geometry: {}", geo.getValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
		});
		return "done";
	}



	@PostMapping( value = "/spatiallabs/intersects")
	public List<SpatialLab> getIntersects(@RequestBody MyPolygonDto myPolygon) {
		Polygon polygon = createPolygon( myPolygon.getLowerLeftX(), myPolygon.getLowerLeftY(),
				myPolygon.getUpperRightX(), myPolygon.getUpperRightY());
		List<SpatialLab> resultSet = spatialLabRepository.findItemsIntersects(polygon);
		return resultSet;
	}

	@PostMapping( value = "/cities/intersects")
	public List<City> getCityIntersects(@RequestBody MyPolygonDto myPolygon) {
		List<City> cities = service.findIntersects( myPolygon);
		return cities;
	}

	@PostMapping( value = "/cities/intersects2")
	public List<City> getCityIntersects2(@RequestBody MyPolygonDto myPolygon) {
		List<City> cities = service.findIntersects2( myPolygon);
		return cities;
	}

	@PostMapping( value = "/cities/near/{distance}")
	public List<City> getCityNearest(@RequestBody MyPolygonDto myPolygon, @PathVariable("distance") Double distance) {
		return service.findNearestCities( myPolygon, distance.doubleValue());
	}

	@PostMapping( value = "/spatiallabs/near/{distance}")
	public List<SpatialLab> getIntersects(@RequestBody MyPolygonDto myPolygon, @PathVariable("distance") Double distance) {
		Polygon polygon = createPolygon( myPolygon.getLowerLeftX(), myPolygon.getLowerLeftY(),
				myPolygon.getUpperRightX(), myPolygon.getUpperRightY());
		List<SpatialLab> resultSet = spatialLabRepository.findNearWithinDistance(polygon, distance);
		return resultSet;
	}
}
