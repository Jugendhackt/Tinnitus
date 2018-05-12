package org.jugendhackt.tinnitus.db;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.jugendhackt.tinnitus.util.Cache;
import org.jugendhackt.tinnitus.util.DataSet;
import org.jugendhackt.tinnitus.util.Tuple;

/**
 * @author Flawn
 */
public class DbConnector {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String host;
    private String username;
    private String password;
    private InfluxDB db;

    public DbConnector(String host, String port){
        logger.info("Starting to connect to DB");
    }

    public void startConnection(){

        try{
            db = InfluxDBFactory.connect(host, username, password);
            db.setDatabase("Tinnitus");
        } catch(Exception e){
            logger.info("Connection couldn't be established!");
            logger.severe(e.getMessage());
        }
        logger.info("Connection is established!");

    }
    public void writeToDb(Tuple t){
        DataSet<String, Integer> ds = Cache.getInstance().getNextElement();
        Tuple<String, Integer> tuple = ds.getData();
        db.write(Point.measurement("noise")
                .time(Long.valueOf(tuple.getKey()), TimeUnit.MILLISECONDS)
                .addField("db", tuple.getKey())
                .tag("geolocation", String.valueOf(ds.getMpId()))
                .build());
    }

}
