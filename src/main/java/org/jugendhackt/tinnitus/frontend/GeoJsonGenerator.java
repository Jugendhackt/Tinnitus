package org.jugendhackt.tinnitus.frontend;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.jugendhackt.tinnitus.frontend.models.Point;
import org.jugendhackt.tinnitus.frontend.models.RootObject;
import org.jugendhackt.tinnitus.frontend.models.locations.Locations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeoJsonGenerator extends TimerTask {

    public static ObjectMapper mapper;
    public static HashMap<Integer, String> cachedJSON = new HashMap<>();
    
    private static HashMap<String, QueryResult> results = new HashMap<>();

    static {
        mapper = new ObjectMapper();
    }

    private static InfluxDB db;
    private static String dbname = "tinnitus";
    
    private static String host = getCredentials().getProperty("host");
    private static String username = getCredentials().getProperty("user");
    private static String password = getCredentials().getProperty("password");
    
    private static Properties getCredentials() {
        Properties props = new Properties();

        try (InputStream in = Files
                .newInputStream(Paths.get("influx-creds.properties")
                        .toAbsolutePath())) {
            props = new Properties();
            props.load(in);

            return props;

        }
        catch (Exception e) {
            System.out.println("influx-creds.properties not found");
            return null;
        }
    }
    
    public void run() {
        System.out.println("Updating Cache...");
        HashMap<Integer, String> updated = new HashMap<>();
        for (int i = 0; i < 25; i = i + 2) {
            System.out.println("Calculating time " + i + "...");
            updated.put(i, generateGeoJson(host, username, password, i));
        }
        cachedJSON = updated;
        System.out.println("Finished Updating!");
    }
    
    public static String getJSON(int start) {
        return cachedJSON.get(start);
    }

    private static String generateGeoJson(String host, String username, String password, int startTime) {
        db = InfluxDBFactory.connect(host, username, password);
        db.setDatabase(dbname);

        HashMap<String, Double> ultimateResult = getAverages(startTime);

        RootObject obj = new RootObject();

        ArrayList<Point> points = new ArrayList<Point>();

        for (String name : ultimateResult.keySet()) {
            int id = Integer.parseInt(name.substring(5));

            String lng = getLongitude(id);
            String lat = getLatitude(id);

            Point point = new Point();
            point.setVol(String.valueOf(ultimateResult.get(name)));
            point.setLng(lng);
            point.setLat(lat);

            points.add(point);
        }

        obj.setPoints(points);

        try {
            return mapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getLongitude(int id) {
        Locations loc;
        try {
            loc = mapper.readValue(
                    Files.newInputStream(Paths.get("locations.json")),
                    Locations.class);
            return loc.getLocations()
                    .get(id)
                    .get(1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return "0";

    }

    private static String getLatitude(int id) {
        Locations loc;
        try {
            loc = mapper.readValue(
                    Files.newInputStream(Paths.get("locations.json")),
                    Locations.class);
            return loc.getLocations()
                    .get(id)
                    .get(0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return "0";

    }
    
    private static HashMap<String, Double> getAverages(int startTime) {
        HashMap<String, Double> avgs = new HashMap<>();
        
        QueryResult measurementQuery = db.query(new Query("SHOW MEASUREMENTS", dbname));
        List<QueryResult.Result> measurementsResult = measurementQuery.getResults();
        List<String> measurements = new ArrayList<>();
        for (List<Object> row : measurementsResult.get(0).getSeries().get(0).getValues()) {
            String name = String.valueOf(row.get(0));
            if (name.contains("noise")) measurements.add(name);
        }

        for (String name : measurements) {
            QueryResult points;
            if (results.containsKey(name)) {
                points  = results.get(name);
            } else {
                points = db.query(new Query("SELECT * FROM " + name, dbname));
                results.put(name, points);
            }
            Double sum = 0.0;
            int valueAmount = 0;
            List<List<Object>> allData = points.getResults().get(0).getSeries().get(0).getValues();
            for (List row : allData) {
                String str = row.get(0)
                        .toString();
                str = str.replaceAll("T", " ")
                        .replaceAll("Z", "");
                int hour = Integer.parseInt(str.split(" ")[1].split(":")[0]);
                if (hour >= startTime && hour <= startTime + 2) {
                    sum = sum + (double) row.get(1);
                    valueAmount++;
                }
                    
            }
            Double avg = sum / (double) valueAmount;
            avgs.put(name, avg);
        }
        return avgs;

    }

    private static List<String> queryMeasurements() {
        QueryResult measurementQuery = db
                .query(new Query("SHOW MEASUREMENTS", "tinnitus"));

        List<QueryResult.Result> measurementsResult = measurementQuery
                .getResults();
        List<String> measurements = new ArrayList<>();

        if (measurementsResult.get(0)
                .getSeries() == null) {
            // measurement doesnt exist
            org.influxdb.dto.Point p = org.influxdb.dto.Point
                    .measurement("noise0")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField("noise", 0)
                    .build();

            db.write(p);

            return queryMeasurements();
        }

        for (Object obj : measurementsResult.get(0)
                .getSeries()
                .get(0)
                .getValues()) {
            String name = String.valueOf(obj);
            if (name.contains("noise")) {
                measurements.add(name);
            }
        }

        return measurements;
    }

}
