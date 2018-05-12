package org.jugendhackt.tinnitus.rest.server.resources;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jugendhackt.tinnitus.util.Cache;
import org.jugendhackt.tinnitus.util.DataSet;
import org.jugendhackt.tinnitus.util.Tuple;

@Path("/tinnitus")
public class TinnitusResource {

    @Path("/recvdata")
    @POST
    public String recvData(@FormParam("mpid") String mpid,
            @FormParam("time") String time, @FormParam("data") String data) {
        System.out.println("Time: " + time + " Data: " + data);

        Cache.getInstance()
                .addElement(new DataSet<String, Integer>(Integer.valueOf(mpid),
                        new Tuple<String, Integer>(time,
                                Integer.valueOf(data))));

        return time + " " + data;
    }

}
