package org.jugendhackt.tinnitus.rest.server;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class TinnitusServer {

    private ResourceConfig config;
    private Server srv;

    public TinnitusServer() {
        config = new ResourceConfig()
                .packages("org.jugenhackt.tinnitus.rest.server.resources");

        this.srv = JettyHttpContainerFactory
                .createServer(URI.create("/tinnitus"), this.config);
    }
    
    public void start() {
        try {
            Thread.currentThread().join();
            
            this.srv.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
