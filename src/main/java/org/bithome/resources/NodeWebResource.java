package org.bithome.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableList;
import org.bithome.api.node.nodes.Node;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Mike Kurdziel on 5/19/14.
 */
@Path("/nodes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NodeWebResource {

    @GET
    @Timed
    public ImmutableList<Node> getNodes() {
        return ImmutableList.of();
    }
}
