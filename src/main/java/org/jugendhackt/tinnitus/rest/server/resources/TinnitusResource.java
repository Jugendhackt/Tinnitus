package org.jugendhackt.tinnitus.rest.server.resources;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jugendhackt.tinnitus.frontend.GeoJsonGenerator;
import org.jugendhackt.tinnitus.util.Cache;
import org.jugendhackt.tinnitus.util.DataSet;
import org.jugendhackt.tinnitus.util.JsonUtil;
import org.jugendhackt.tinnitus.util.TimeUtils;
import org.jugendhackt.tinnitus.util.Triple;

@Path("/nw")
public class TinnitusResource {

    private Logger log = Logger.getLogger(this.getClass()
            .getName());

    @Path("/recvdata")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void recvData(@FormParam("mpid") String mpid,
            @FormParam("time") String time, @FormParam("noise") String noise, @FormParam("dust") String dust) {
        
        log.info("Received: MPID=" + mpid + " TIME=" + time + " DUST=" + dust + " NOISE=" + noise);

        if(noise == null) {
            noise = "0";
        }
        
        if(dust == null) {
            dust = "0";
        }
        
        Cache.getInstance()
                .addElement(new DataSet<String, Integer, Integer>(Integer.valueOf(mpid),
                        new Triple<String, Integer, Integer>(time,
                                Integer.valueOf(noise), Integer.valueOf(dust))));

    }

    @Path("/geojson")
    @GET
    public Response geoJson(@QueryParam("stime") String stime) {
        int startTime = Integer.parseInt(stime);
        int endTime = TimeUtils.incrementHoursBy(startTime, 2);

        Properties props = getCredentials();

        String json = new GeoJsonGenerator(props.getProperty("host"),
                props.getProperty("user"), props.getProperty("password"))
                        .generateGeoJson(startTime, endTime);

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .entity(json)
                .build();
    }

    @Path("/install")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response installLoc(@FormParam("lat") String lat,
            @FormParam("lng") String lng, @FormParam("id") String id) {
        log.info("Received: lng=" + lng + " lat=" + lat + " id="  + id);

        JsonUtil.addLocation(lat, lng, id);

        return Response.ok()
                .build();
    }

    private Properties getCredentials() {
        Properties props = new Properties();

        try (InputStream in = Files
                .newInputStream(Paths.get("influx-creds.properties")
                        .toAbsolutePath())) {
            props = new Properties();
            props.load(in);

            return props;

        }
        catch (Exception e) {
            System.out.println("influx-creds.properties not found");
            return null;
        }
    }
}
