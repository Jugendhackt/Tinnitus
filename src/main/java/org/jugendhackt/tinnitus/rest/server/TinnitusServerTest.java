package org.jugendhackt.tinnitus.rest.server;

import org.junit.Ignore;

import junit.framework.TestCase;

public class TinnitusServerTest extends TestCase {

    TinnitusServer srv;
    
    @Ignore
    public void testServer() {
        this.srv = new TinnitusServer();
        
        srv.start();
    }
    
}
