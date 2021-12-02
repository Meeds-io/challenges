package org.exoplatform.challenges.rest;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.service.ChallengeService;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/challenge/api")
@Api(value = "/challenge/api", description = "Manages challenge associated to users") // NOSONAR
@RolesAllowed("users")
public class ChallengeRest implements ResourceContainer {

  private static final Log LOG = ExoLogger.getLogger(ChallengeRest.class);

  private ChallengeService challengeService;

  public ChallengeRest(ChallengeService challengeService) {
    this.challengeService = challengeService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("addChallenge")
  @ApiOperation(value = "Creates a new challenge", httpMethod = "POST", response = Response.class, consumes = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
      @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
      @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
      @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response createChallenge(@ApiParam(value = "Challenge object to create", required = true)
  Challenge challenge) {
    if (challenge == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("challenge object is mandatory").build();
    }
    String currentUser = Utils.getCurrentUser();
    if (StringUtils.isBlank(currentUser)) {
      LOG.warn("current User is null");
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      Challenge newChallenge = challengeService.createChallenge(challenge, currentUser);
      return Response.ok(newChallenge).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to create a challenge", e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (Exception e) {
      LOG.warn("Error creating a challenge", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }
}
