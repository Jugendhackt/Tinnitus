package org.jugendhackt.tinnitus.db.writer;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.jugendhackt.tinnitus.util.Cache;
import org.jugendhackt.tinnitus.util.DataSet;
import org.jugendhackt.tinnitus.util.Triple;

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
        DataSet<String, Integer, Float> ds = Cache.getInstance()
                .getNextElement();

        if (ds == null) {
            return;
        }
        
        Triple<String, Integer, Float> triple = ds.getData();

        Point p = Point.measurement("noise" + ds.getMpId())
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("noise", triple.getValue1())
                .addField("dust", triple.getValue2())
                .build();

        db.write("tinnitus", "twoweeks", p);

    }
}
