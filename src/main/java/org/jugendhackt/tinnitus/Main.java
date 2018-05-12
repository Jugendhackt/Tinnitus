package org.jugendhackt.tinnitus;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jugendhackt.tinnitus.db.DbConnector;
import org.jugendhackt.tinnitus.rest.server.TinnitusServer;
import org.jugendhackt.tinnitus.util.Cache;
import org.jugendhackt.tinnitus.util.DataSet;
import org.jugendhackt.tinnitus.util.Tuple;

/**
 * @author Flawn
 */
public class Main {

    private static ExecutorService exec;
    private static Properties props;

    public static void main(String[] args) {
        mockCache();

        System.out.println(Paths.get("influx-creds.properties").toAbsolutePath());
        
        try (InputStream in = Files.newInputStream(
                Paths.get("influx-creds.properties").toAbsolutePath())) {
            props = new Properties();
            props.load(in);

        }
        catch (Exception e) {
            System.out.println(
                    "influx-creds.properties not found");
        }

        exec = Executors.newFixedThreadPool(2);

        List<Callable<Void>> run = new ArrayList<Callable<Void>>();
        run.add(new TinnitusServer());
        run.add(new DbConnector(props.getProperty("host"),
                props.getProperty("user"), props.getProperty("password")));

        try {
            List<Future<Void>> futures = exec.invokeAll(run);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void mockCache() {
        for (int i = 0; i < 100; i++) {
            Cache.getInstance()
                    .addElement(new DataSet<String, Integer>(0,
                            new Tuple("21:" + i % 60, i * i - 2)));
        }

        System.out.println("Populated cache");
    }

}
