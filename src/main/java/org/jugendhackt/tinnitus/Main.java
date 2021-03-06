package org.jugendhackt.tinnitus;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.jugendhackt.tinnitus.db.writer.DbConnector;
import org.jugendhackt.tinnitus.frontend.GeoJsonGenerator;
import org.jugendhackt.tinnitus.rest.server.TinnitusServer;
import org.jugendhackt.tinnitus.util.Cache;
import org.jugendhackt.tinnitus.util.DataSet;
import org.jugendhackt.tinnitus.util.Triple;

/**
 * @author Flawn
 */
public class Main {

    private static ExecutorService exec;
    private static ScheduledExecutorService jsoncache;
    private static Properties props;

    public static void main(String[] args) {
//        mockCache();

        try (InputStream in = Files
                .newInputStream(Paths.get("influx-creds.properties")
                        .toAbsolutePath())) {
            props = new Properties();
            props.load(in);

        }
        catch (Exception e) {
            System.out.println("influx-creds.properties not found");
        }
//
//        GeoJsonGenerator gen = new GeoJsonGenerator(props.getProperty("host"),
//                props.getProperty("user"), props.getProperty("password"));
//
//        gen.generateGeoJson(1, 24);

        exec = Executors.newFixedThreadPool(2);
        new Timer().scheduleAtFixedRate(new GeoJsonGenerator(), 0, 36_000_000);
            
        List<Callable<Void>> run = new ArrayList<Callable<Void>>();
        run.add(new TinnitusServer());
        run.add(new DbConnector(props.getProperty("host"),
                props.getProperty("user"), props.getProperty("password")));

        try {
            List<Future<Void>> futures = exec.invokeAll(run);
            
            futures.stream().allMatch(Future::isDone);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void mockCache() {
        for (int i = 0; i < 100; i++) {
            Cache.getInstance()
                    .addElement(new DataSet<String, Integer, Float>(0,
                            new Triple("21" + i % 60, i * i + 2, 12)));
        }

        System.out.println("Populated cache");
    }

}
