package org.jugendhackt.tinnitus;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jugendhackt.tinnitus.rest.server.TinnitusServer;

/**
 * @author Flawn
 */
public class Main {

    private static ExecutorService exec;

    public static void main(String[] args) {
        exec = Executors.newFixedThreadPool(2);

        ArrayList<Runnable> run = new ArrayList<Runnable>();
        run.add(new TinnitusServer());
    }

}
