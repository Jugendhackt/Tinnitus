package org.jugendhackt.tinnitus.rest.server;

import java.net.URI;
import java.util.concurrent.Callable;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class TinnitusServer implements Callable<Void> {

    private ResourceConfig config;
    private Server srv;

    public TinnitusServer() {
        config = new ResourceConfig()
                .packages("org.jugendhackt.tinnitus.rest.server.resources");

        this.srv = JettyHttpContainerFactory
                .createServer(URI.create("http://localhost:8081"), this.config);
    }

    public Void call() {
        try {
            Thread.currentThread()
                    .join();

            this.srv.start();

            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
