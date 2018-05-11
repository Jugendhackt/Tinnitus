package org.jugendhackt.tinnitus.util.org.jugendhackt.tinnitus.db;

import java.util.logging.Logger;

/**
 * @author Flawn
 */
public class DbConnector {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String host;
    private String port;

    public DbConnector(String host, String port){
        logger.info("Starting to connect to DB");
    }

    public startConnection(){

    }
}
