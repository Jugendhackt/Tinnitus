package org.jugendhackt.tinnitus.rest.server.resources;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jugendhackt.tinnitus.util.Cache;
import org.jugendhackt.tinnitus.util.Tuple;

@Path("/tinnitus")
public class TinnitusResource {

    @Path("/recvdata")
    @POST
    public String recvData(@FormParam("time") String time,
            @FormParam("data") String data) {
        System.out.println("Time: " + time + " Noise: " + data);
        
        Cache.getInstance()
                .addElement(new Tuple<String, Integer>(time, Integer.valueOf(data)));
        
        return time + " " + data;
    }

}
