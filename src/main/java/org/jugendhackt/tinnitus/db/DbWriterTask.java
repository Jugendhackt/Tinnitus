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
        DataSet<String, Integer> ds = Cache.getInstance().getNextElement();
        Tuple<String, Integer> tuple = ds.getData();
        db.write(Point.measurement("noise" + String.valueOf(ds.getMpId()))
                .time(Long.valueOf(tuple.getKey()), TimeUnit.MILLISECONDS)
                .addField("db", tuple.getValue())
                .build());
    }
}
