package org.jugendhackt.tinnitus.frontend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

public class GeoJsonGenerator {

    private InfluxDB db;
    private String host;
    private String username;
    private String password;

    public GeoJsonGenerator(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public void generateGeoJson(int startTime, int endTime) {
        this.db = InfluxDBFactory.connect(host, username, password);
        this.db.setDatabase("tinnitus");

        HashMap<String, Double> ultimateResult = getAverages(startTime, endTime);

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
                String str = data.get(j).get(0).toString();
                str = str.replaceAll("T", " ").replaceAll("Z", "");
                System.out.println(str);
                int hour = Integer.parseInt(str.split(" ")[1].split(":")[0]);
                
                if(hour >= startTime && hour <= endTime)
                sum += Double.valueOf(data.get(j)
                        .get(1)
                        .toString());
            }

            ultimateResult.put(meas.get(i), sum / data.size());
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
