package org.jugendhackt.tinnitus.db;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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

    private Logger log = Logger.getLogger(this.getClass()
            .getName());

    public DbWriterTask(InfluxDB db) {
        this.db = db;
    }

    @Override
    public void run() {
        DataSet<String, Integer> ds = Cache.getInstance()
                .getNextElement();

        if (ds == null) {
            return;
        }

        Tuple<String, Integer> tuple = ds.getData();

        Point p = Point.measurement("noise" + ds.getMpId())
                // TODO use timestamp/dateformat
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("value", tuple.getValue())
                .build();

        db.write(p);

    }
}
