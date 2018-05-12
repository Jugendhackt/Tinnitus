package org.jugendhackt.tinnitus.db;

import java.util.concurrent.TimeUnit;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.jugendhackt.tinnitus.util.Cache;
import org.jugendhackt.tinnitus.util.DataSet;
import org.jugendhackt.tinnitus.util.Tuple;

/**
 * @author Flawn
 */
public class DbWriterTask implements Runnable {
    private InfluxDB db;

    public DbWriterTask(InfluxDB db) {
        this.db = db;
    }

    @Override
    public void run() {
        System.out.println("1");
        DataSet<String, Integer> ds = Cache.getInstance().getNextElement();
        Tuple<String, Integer> tuple = ds.getData();
        Point p = Point.measurement("noise" + String.valueOf(ds.getMpId()))
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("value", tuple.getValue())
                .build();
        db.write(p);
        System.out.println("fertig");

    }
}
