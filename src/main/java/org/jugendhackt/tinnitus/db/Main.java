package org.jugendhackt.tinnitus.db;

/**
 * @author Flawn
 */
public class Main {
    public static void main(String[] args){
        DbConnector db = new DbConnector("http://p3h3.de:2985", "tinnitus", "ilovetinnitus");
        db.startConnection();
    }
}
