package org.jugendhackt.tinnitus.frontend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public GeoJsonGenerator(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String generateGeoJson(int startTime, int endTime) {
        this.db = InfluxDBFactory.connect(host, username, password);
        this.db.setDatabase("tinnitus");

        HashMap<String, Double> ultimateResult = getAverages(startTime,
                endTime);

        RootObject obj = new RootObject();

        ArrayList<Point> points = new ArrayList<Point>();

        for (String name : ultimateResult.keySet()) {
            name = name.replaceAll("\\[", "");
            name = name.replaceAll("\\]", "");
            int id = Integer.parseInt(
                    new Character(name.charAt(name.length() - 1)).toString());

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

    private HashMap<String, Double> getAverages(int startTime, int endTime) {
        String queryString = "SELECT * from name WHERE time > now() - 2w";

        ArrayList<String> meas = (ArrayList<String>) queryMeasurements();
        HashMap<String, Double> ultimateResult = new HashMap<String, Double>();

        for (int i = 0; i < meas.size(); i++) {
            Query q = new Query(queryString.replaceAll("name", meas.get(i)
                    .replaceAll("\\[", "")
                    .replaceAll("\\]", "")), "tinnitus");
            // List<QueryResult.Result> results = db.query(q).getResults();

            QueryResult points = db.query(q);

            List<Datapoint> datapoints = new ArrayList<Datapoint>();

            if (points.getResults()
                    .get(0)
                    .getSeries() == null) {
                continue;
            }

            List<List<Object>> data = points.getResults()
                    .get(0)
                    .getSeries()
                    .get(0)
                    .getValues();

            double sum = 0;

            // Loop over row
            for (int j = 0; j < data.size(); j++) {
                String str = data.get(j)
                        .get(0)
                        .toString();
                str = str.replaceAll("T", " ")
                        .replaceAll("Z", "");
                int hour = Integer.parseInt(str.split(" ")[1].split(":")[0]);

                if (hour >= startTime && hour <= endTime)
                    sum += Double.valueOf(data.get(j)
                            .get(1)
                            .toString());
            }

            ultimateResult.put(meas.get(i)
                    .replaceAll("\\[", "")
                    .replaceAll("\\]", ""), sum / data.size());
        }

        return ultimateResult;
    }

    private List<String> queryMeasurements() {
        QueryResult measurementQuery = db
                .query(new Query("SHOW MEASUREMENTS", "tinnitus"));

        List<QueryResult.Result> measurementsResult = measurementQuery
                .getResults();
        List<String> measurements = new ArrayList<>();

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
