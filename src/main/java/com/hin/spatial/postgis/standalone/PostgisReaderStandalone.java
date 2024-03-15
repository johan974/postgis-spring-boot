package com.hin.spatial.postgis.standalone;


import net.postgis.jdbc.PGgeometry;
import org.postgresql.util.PGobject;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PostgisReaderStandalone {

    public static void main(String[] args) {

        java.sql.Connection conn;

        try {
            /*
             * Load the JDBC driver and establish a connection.
             */
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5433/postgis";
            conn = DriverManager.getConnection(url, "postgis", "postgis");
            /*
             * Add the geometry types to the connection. Note that you
             * must cast the connection to the pgsql-specific connection
             * implementation before calling the addDataType() method.
             */
            // ((org.postgresql.PGConnection)conn).addDataType("geometry", (Class<? extends PGobject>) Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection)conn).addDataType("geometry", (Class<? extends PGobject>) Class.forName("net.postgis.jdbc.PGgeometry"));
            /*
             * Create a statement and execute a select query.
             */
            Statement s = conn.createStatement();
            ResultSet r = s.executeQuery("select id,geometry,remarks from objectwithgeometries where id = 43");
            while( r.next() ) {
                /*
                 * Retrieve the geometry as an object then cast it to the geometry type.
                 * Print things out.
                 */
                PGgeometry geom = (PGgeometry)r.getObject(2);
                int id = r.getInt(1);
                System.out.println("Row " + id + ":");
                System.out.println(geom.toString());
            }
            s.close();
            conn.close();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
}