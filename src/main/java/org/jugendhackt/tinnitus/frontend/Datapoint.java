package org.jugendhackt.tinnitus.frontend;

import java.util.List;

public class Datapoint {

    private double[] data;
    private double avg;

    public Datapoint(List<List> values) {
        data = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            List row = values.get(i);
            data[i] = Double.valueOf(row.get(1).toString());
        }

        double sum = 0;
        for (double value : data) {
            sum = sum + value;
        }
        avg = sum / (double) data.length;
    }

    public double[] getData() {
        return data;
    }

    public double getAvg() {
        return avg;
    }

}
