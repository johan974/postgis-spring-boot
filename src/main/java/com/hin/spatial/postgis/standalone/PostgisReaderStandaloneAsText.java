package com.hin.spatial.postgis.standalone;


import org.geotools.geometry.jts.CircularString;
import org.geotools.geometry.jts.CurvedGeometryFactory;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;
import org.locationtech.jts.geom.GeometryFactory;
import org.postgresql.util.PGobject;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PostgisReaderStandaloneAsText {
    public static void main(String[] args) {
        java.sql.Connection conn;

        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5433/postgis";
            conn = DriverManager.getConnection(url, "postgis", "postgis");
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery("select id,ST_AsText(geometry),remarks from objectwithgeometries where id = 43");
            while( r.next() ) {
                String geom = (String) r.getObject(2); // --> gives Unknown Geometry Type: 8
                int id = r.getInt(1);
                System.out.println("Row " + id + " = " + geom.toString());

                GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
                CurvedGeometryFactory curvedfactory = new CurvedGeometryFactory(Double.MAX_VALUE);

                WKTReader2 reader = new WKTReader2(curvedfactory);
                CircularString arc = (CircularString) reader.read(geom);
                System.out.println( "Arc = " + arc);
            }
            s.close();
            conn.close();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
}