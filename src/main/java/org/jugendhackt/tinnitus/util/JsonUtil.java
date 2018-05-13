package org.jugendhackt.tinnitus.util;

import static org.jugendhackt.tinnitus.frontend.GeoJsonGenerator.mapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jugendhackt.tinnitus.frontend.models.locations.Locations;

/**
 * @author Flawn
 */
public class JsonUtil {
    public static void addLocation(String lat, String lng, String id) {
        try {
            System.out.println("adding");
            Locations locations;

            if (isFileEmpty("locations.json")) {
                locations = new Locations();
            }
            else {
                locations = mapper.readValue(Paths.get("locations.json")
                        .toFile(), Locations.class);
            }

            ArrayList<ArrayList<String>> ar = locations.getLocations();
            ArrayList<String> arr = new ArrayList<String>(
                    Arrays.asList(lat, lng, id));

            if (ar != null) {
                if (ar.contains(arr)) {
                    ar.remove(arr);
                }
            }
            else {
                ar = new ArrayList<ArrayList<String>>();
            }

            ar.add(arr);

            locations.setLocations(ar);

            mapper.writeValue(
                    Files.newOutputStream(Paths.get("locations.json")),
                    locations);
            System.out.println("finished");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isFileEmpty(String path) {
        try {
            return new FileInputStream(Paths.get(path)
                    .toFile()).getChannel()
                            .size() == 0;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}