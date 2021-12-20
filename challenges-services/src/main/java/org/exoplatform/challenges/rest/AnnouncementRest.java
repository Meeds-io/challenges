package org.exoplatform.challenges.rest;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.challenges.model.Announcement;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.service.AnnouncementService;
import org.exoplatform.challenges.utils.EntityMapper;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/announcement/api")
@Api(value = "/announcement/api", description = "Manages announcement associated to users") // NOSONAR
@RolesAllowed("users")
public class AnnouncementRest implements ResourceContainer {

  private static final Log LOG = ExoLogger.getLogger(AnnouncementRest.class);
  private AnnouncementService  announcementService;

  public AnnouncementRest(AnnouncementService announcementService) { this.announcementService = announcementService; }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("addAnnouncement")
  @ApiOperation(value = "Creates a new Announcement", httpMethod = "POST", response = Response.class, consumes = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
      @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
      @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
      @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response createAnnouncement(@ApiParam(value = "Announcement object to create", required = true)
                                           Announcement announcement) {
    if (announcement == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("challenge object is mandatory").build();
    }

    try {
      Announcement newAnnouncement = announcementService.createAnnouncement(announcement);
      return Response.ok(newAnnouncement).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to create an announcement", e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (Exception e) {
      LOG.warn("Error creating an announcement", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

}
