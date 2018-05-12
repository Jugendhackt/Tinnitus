package org.jugendhackt.tinnitus.rest.server.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jugendhackt.tinnitus.frontend.GeoJsonGenerator;
import org.jugendhackt.tinnitus.util.Cache;
import org.jugendhackt.tinnitus.util.DataSet;
import org.jugendhackt.tinnitus.util.TimeUtils;
import org.jugendhackt.tinnitus.util.Tuple;


@Path("/nw")
public class TinnitusResource {

    private Logger log = Logger.getLogger(this.getClass()
            .getName());

    @Path("/recvdata")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void recvData(@FormParam("mpid") String mpid,
            @FormParam("time") String time, @FormParam("data") String data) {

        log.info("Received: MPID=" + mpid + " TIME=" + time + " DATA=" + data);

        Cache.getInstance()
                .addElement(new DataSet<String, Integer>(Integer.valueOf(mpid),
                        new Tuple<String, Integer>(time,
                                Integer.valueOf(data))));

    }
    
    @Path("/geojson")
    @GET
    public Response geoJson(@QueryParam("stime") String stime) {
        int startTime = Integer.parseInt(stime);
        int endTime = TimeUtils.incrementHoursBy(startTime, 2);
        
        // TODO CHANGE
        String json = new GeoJsonGenerator("http://p3h3.de:2985", "tinnitus", "ilovetinnitus").generateGeoJson(startTime, endTime);
        
        return Response.ok().header("Access-Control-Allow-Origin", "*").entity(json).build();
    }
}
