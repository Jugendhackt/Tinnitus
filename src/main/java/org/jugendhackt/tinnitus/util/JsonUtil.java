package org.jugendhackt.tinnitus.util;

import static org.jugendhackt.tinnitus.frontend.GeoJsonGenerator.mapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import org.jugendhackt.tinnitus.frontend.models.locations.Locations;

/**
 * @author Flawn
 */
public class JsonUtil {
    public static void addLocation(String lat, String lng){
        try {
            System.out.println("adding");
            Locations locations = mapper.readValue(Paths.get("locations.json").toFile(), Locations.class);

            ArrayList<ArrayList<String>> ar = locations.getLocations();
            ar.add(new ArrayList<String>(Arrays.asList(lat,lng)));
            locations.setLocations(ar);

            mapper.writeValue(Files.newOutputStream(Paths.get("locations.json")), locations);
            System.out.println("finished");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
