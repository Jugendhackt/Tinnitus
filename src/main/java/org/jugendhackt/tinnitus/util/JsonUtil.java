package org.jugendhackt.tinnitus.util;

import static org.jugendhackt.tinnitus.frontend.GeoJsonGenerator.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jugendhackt.tinnitus.frontend.models.locations.Locations;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Flawn
 */
public class JsonUtil {
    private static Locations locs;
    
    public static void addLocation(String lat, String lng,
            String id) {
        if(locs == null)
            try {
                loadJson();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
        ArrayList<String> insert = new ArrayList<>();
        insert.add(lat);
        insert.add(lng);
        insert.add(id);
        if (Integer.parseInt(id) < locs.getLocations().size()) {
            locs.getLocations().set(Integer.parseInt(id), insert);
        } else {
            locs.getLocations().add(insert);
        }
        
        try {
            saveJson();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static void loadJson() throws JsonParseException, JsonMappingException, IOException {
      //LOADING OR CREATING BASED ON THE FILES EXISTANCE
        if (new File("locations.json").exists()) {
            locs = mapper.readValue(Paths.get("locations.json")
                    .toFile(), Locations.class);
        }
        else {
            locs = new Locations();
        }
    }
    
    private static void saveJson() throws JsonGenerationException, JsonMappingException, IOException {
        mapper.writeValue(
                Files.newOutputStream(Paths.get("locations.json")),
                locs);
    }
}