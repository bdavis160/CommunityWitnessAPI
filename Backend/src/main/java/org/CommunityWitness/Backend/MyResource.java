/**
 * This is the default file that was generated with the project, 
 * with some extra examples added,
 * for more information check https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/getting-started.html
 */

package org.CommunityWitness.Backend;

import jakarta.ws.rs.GET;	
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    

    /**
     * This is the general format for serving objects as json to clients,
     * taken from https://github.com/eclipse-ee4j/jersey/tree/3.x/examples/json-moxy
     */
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public SomeType serveSomeType() {
//    	return new SomeType();
//    }
}
