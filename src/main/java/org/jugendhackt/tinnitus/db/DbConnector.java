package org.jugendhackt.tinnitus.db;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

/**
 * @author Flawn
 */
public class DbConnector implements Callable<Void> {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String host;
    private String username;
    private String password;
    private InfluxDB db;

    public DbConnector(String host, String username, String password){
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public Void call(){
        logger.info("Starting to connect to DB");
        try{
            db = InfluxDBFactory.connect(host, username, password);
            db.setDatabase("Tinnitus");
        } catch(Exception e){
            logger.info("Connection couldn't be established!");
            logger.severe(e.getMessage());
        }
        logger.info("Connection is established!");
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new DbWriterTask(db), 1 , 10, TimeUnit.SECONDS);
        
        return null;
    }

}
