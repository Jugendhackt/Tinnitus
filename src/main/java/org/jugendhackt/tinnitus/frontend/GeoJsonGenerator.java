package org.jugendhackt.tinnitus.frontend;

import java.util.ArrayList;
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

        String queryString = "SELECT * from name WHERE time > now() - 2w";

        ArrayList<String> meas = (ArrayList<String>) queryMeasurements();

        for (int i = 0; i < meas.size(); i++) {
            Query q = new Query(queryString.replaceAll("name", meas.get(i)
                    .replaceAll("\\[", "")
                    .replaceAll("\\]", "")), "tinnitus");
            // List<QueryResult.Result> results = db.query(q).getResults();

            System.out.println(queryString.replaceAll("name", meas.get(i)
                    .replaceAll("\\[", "")
                    .replaceAll("\\]", "")));

            QueryResult points = db.query(q);

            List<Datapoint> datapoints = new ArrayList<Datapoint>();

            System.out.println(points.getResults());
            
            if(points.getResults().get(0).getSeries() == null) {
                continue;
            }
            
            List data = points.getResults()
                    .get(0)
                    .getSeries()
                    .get(0)
                    .getValues();
            
            double sum = 0;
            
            // Loop over row
            for(int j = 0; j < data.size(); j++) {
                sum += Double.valueOf(data.get(1).toString());
            }
            
            System.out.println(sum / data.size());

            datapoints.add(new Datapoint(data));

            for(Datapoint d : datapoints) {
                System.out.println(d.getAvg());
            }
        }
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
