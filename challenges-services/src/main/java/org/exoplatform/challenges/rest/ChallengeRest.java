package org.exoplatform.challenges.rest;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.challenges.model.Challenge;
import org.exoplatform.challenges.service.ChallengeService;
import org.exoplatform.challenges.utils.Utils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.exception.ObjectNotFoundException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{challengeId}")
  @RolesAllowed("users")
  @ApiOperation(value = "Retrieves a challenge by its id", httpMethod = "GET", response = Response.class, produces = "application/json", notes = "returns selected challenge if exists")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Request fulfilled"),
          @ApiResponse(code = 400, message = "Invalid query input"),
          @ApiResponse(code = 403, message = "Unauthorized operation"),
          @ApiResponse(code = 500, message = "Internal server error") })
  public Response getChallengeById(@ApiParam(value = "Challenge technical id", required = true) @PathParam("challengeId") long challengeId) {
    if (challengeId == 0) {
      LOG.warn("Bad request sent to server with empty challengeId");
      return Response.status(400).build();
    }
    String currentUserId = Utils.getCurrentUser();
    try {
      Challenge challenge = challengeService.getChallengeById(challengeId, currentUserId);
      return Response.ok(challenge).build();
    } catch (Exception e) {
      LOG.error("Error getting challenge", e);
      return Response.status(500).build();
    }
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("updateChallenge")
  @ApiOperation(value = "Updates an existing challenge", httpMethod = "PUT", response = Response.class, consumes = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.NO_CONTENT, message = "Request fulfilled"),
      @ApiResponse(code = HTTPStatus.NOT_FOUND, message = "Object not found"),
      @ApiResponse(code = HTTPStatus.BAD_REQUEST, message = "Invalid query input"),
      @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
      @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response updateChallenge(@ApiParam(value = "challenge object to update", required = true) Challenge challenge) {
    if (challenge == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("challenge object is mandatory").build();
    }
    if (challenge.getId() <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("challenge technical identifier must be positive").build();
    }

    String currentUser = Utils.getCurrentUser();
    try {
      challenge = challengeService.updateChallenge(challenge, currentUser);
      return Response.ok(challenge).build();
    } catch (ObjectNotFoundException e) {
      LOG.debug("User '{}' attempts to update a not existing challenge '{}'", currentUser, e);
      return Response.status(Response.Status.NOT_FOUND).entity("Challenge not found").build();
    } catch (IllegalAccessException e) {
      LOG.error("User '{}' attempts to update a challenge for owner '{}'", currentUser, e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (Exception e) {
      LOG.warn("Error updating a challenge", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("users")
  @Path("allChallenge")
  @ApiOperation(value = "Retrieves the list of challenges available for an owner", httpMethod = "GET", response = Response.class, produces = "application/json")
  @ApiResponses(value = { @ApiResponse(code = HTTPStatus.OK, message = "Request fulfilled"),
  @ApiResponse(code = HTTPStatus.UNAUTHORIZED, message = "Unauthorized operation"),
  @ApiResponse(code = HTTPStatus.INTERNAL_ERROR, message = "Internal server error"), })
  public Response getAllChallengesByUser(@ApiParam(value = "Offset of result", required = false, defaultValue = "0")
                                         @QueryParam("offset")
                                         int offset,
                                         @ApiParam(value = "Limit of result", required = false, defaultValue = "10")
                                         @QueryParam("limit")
                                         int limit) {
    if (offset < 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Offset must be 0 or positive").build();
    }
    if (limit <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Limit must be positive").build();
    }
    String currentUser = Utils.getCurrentUser();
    try {
      List<Challenge> challenges = challengeService.getAllChallengeByUser(offset, limit, currentUser);
      return Response.ok(challenges).build();
    } catch (IllegalAccessException e) {
      LOG.warn("User '{}' attempts to access not authorized challenges with owner Ids", currentUser, e);
      return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
    } catch (Exception e) {
      LOG.warn("Error retrieving list of challenges", e);
      return Response.serverError().entity(e.getMessage()).build();
    }
  }
}
