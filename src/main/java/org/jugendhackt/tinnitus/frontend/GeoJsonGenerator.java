package org.jugendhackt.tinnitus.frontend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class GeoJsonGenerator {

    public static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    private InfluxDB db;
    private String host;
    private String username;
    private String password;
    private static String dbname = "tinnitus";

    public GeoJsonGenerator(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String generateGeoJson(int startTime) {
        this.db = InfluxDBFactory.connect(host, username, password);
        this.db.setDatabase("tinnitus");

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

    private String getLongitude(int id) {
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

    private String getLatitude(int id) {
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
    
    private HashMap<String, Double> getAverages(int startTime) {
        HashMap<String, Double> avgs = new HashMap<>();
        
        QueryResult measurementQuery = db.query(new Query("SHOW MEASUREMENTS", dbname));
        List<QueryResult.Result> measurementsResult = measurementQuery.getResults();
        List<String> measurements = new ArrayList<>();
        for (List<Object> row : measurementsResult.get(0).getSeries().get(0).getValues()) {
            String name = String.valueOf(row.get(0));
            if (name.contains("noise")) measurements.add(name);
        }

        for (String name : measurements) {
            QueryResult points = db.query(new Query("SELECT * FROM " + name, dbname));
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

    private List<String> queryMeasurements() {
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
