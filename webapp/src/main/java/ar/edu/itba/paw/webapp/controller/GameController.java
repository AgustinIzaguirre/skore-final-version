package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.notfound.GameNotFoundException;
import ar.edu.itba.paw.interfaces.GameService;
import ar.edu.itba.paw.interfaces.TeamService;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.GameSort;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.Place;
import ar.edu.itba.paw.models.QueryList;
import ar.edu.itba.paw.models.Team;
import ar.edu.itba.paw.webapp.constants.URLConstants;
import ar.edu.itba.paw.webapp.dto.DateDto;
import ar.edu.itba.paw.webapp.dto.GameDto;
import ar.edu.itba.paw.webapp.dto.GamePageDto;
import ar.edu.itba.paw.webapp.dto.PlaceDto;
import ar.edu.itba.paw.webapp.dto.ResultDto;
import ar.edu.itba.paw.webapp.dto.TeamDto;
import ar.edu.itba.paw.webapp.dto.TeamPlayerDto;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import ar.edu.itba.paw.webapp.utils.JSONUtils;
import ar.edu.itba.paw.webapp.utils.LocaleUtils;
import ar.edu.itba.paw.webapp.utils.QueryParamsUtils;
import ar.edu.itba.paw.webapp.validators.GameValidators;
import ar.edu.itba.paw.webapp.validators.PlayerValidators;
import ar.edu.itba.paw.webapp.validators.ResultValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.constants.HeaderConstants.CODE_HEADER;
import static ar.edu.itba.paw.webapp.controller.GameController.BASE_PATH;

@Controller
@Path(BASE_PATH)
@Produces({MediaType.APPLICATION_JSON})
public class GameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    public static final String BASE_PATH = "matches";

    @Autowired
    @Qualifier("gameServiceImpl")
    private GameService gameService;

    @Autowired
    @Qualifier("teamServiceImpl")
    private TeamService teamService;

    public static String getGameEndpoint(final String gameId) {
        return URLConstants.getApiBaseUrlBuilder().path(BASE_PATH).path(gameId).toTemplate();
    }

    @GET
    public Response getGames(@QueryParam("minStartTime") String minStartTime,
                             @QueryParam("maxStartTime") String maxStartTime,
                             @QueryParam("minFinishTime") String minFinishTime,
                             @QueryParam("maxFinishTime") String maxFinishTime,
                             @QueryParam("minQuantity") String minQuantity,
                             @QueryParam("maxQuantity") String maxQuantity,
                             @QueryParam("minFreePlaces") String minFreePlaces,
                             @QueryParam("maxFreePlaces") String maxFreePlaces,
                             @QueryParam("country") QueryList countries,
                             @QueryParam("state") QueryList states,
                             @QueryParam("city") QueryList cities,
                             @QueryParam("sport") QueryList sports,
                             @QueryParam("type") QueryList types,
                             @QueryParam("withPlayers") QueryList usernamesPlayersInclude,
                             @QueryParam("withoutPlayers") QueryList usernamesPlayersNotInclude,
                             @QueryParam("createdBy") QueryList usernamesCreatorsInclude,
                             @QueryParam("notCreatedBy") QueryList usernamesCreatorsNotInclude,
                             @QueryParam("limit") String limit, @QueryParam("offset") String offset,
                             @QueryParam("sortBy") GameSort sort, @Context UriInfo uriInfo,
                             @QueryParam("hasResult") String hasResult, @QueryParam("onlyLikedUsers") String onlyLikedUsers,
                             @QueryParam("onlyLikedSports") String onlyLikedSports) {
        Page<GameDto> page = gameService.findGamesPage(QueryParamsUtils.localDateTimeOrNull(minStartTime),
                QueryParamsUtils.localDateTimeOrNull(maxStartTime), QueryParamsUtils.localDateTimeOrNull(minFinishTime),
                QueryParamsUtils.localDateTimeOrNull(maxFinishTime), QueryParamsUtils.getQueryListOrNull(types),
                QueryParamsUtils.getQueryListOrNull(sports), QueryParamsUtils.positiveIntegerOrNull(minQuantity),
                QueryParamsUtils.positiveIntegerOrNull(maxQuantity), QueryParamsUtils.getQueryListOrNull(countries),
                QueryParamsUtils.getQueryListOrNull(states), QueryParamsUtils.getQueryListOrNull(cities),
                QueryParamsUtils.positiveIntegerOrNull(minFreePlaces), QueryParamsUtils.positiveIntegerOrNull(maxFreePlaces),
                QueryParamsUtils.getQueryListOrNull(usernamesPlayersInclude), QueryParamsUtils.getQueryListOrNull(usernamesPlayersNotInclude),
                QueryParamsUtils.getQueryListOrNull(usernamesCreatorsInclude), QueryParamsUtils.getQueryListOrNull(usernamesCreatorsNotInclude),
                QueryParamsUtils.positiveIntegerOrNull(limit), QueryParamsUtils.positiveIntegerOrNull(offset), sort,
                QueryParamsUtils.booleanOrNull(hasResult), QueryParamsUtils.booleanOrElse(onlyLikedUsers, false),
                QueryParamsUtils.booleanOrElse(onlyLikedSports, false))
                .map((game) ->GameDto.from(game, getTeam(game.getTeam1()), getTeam(game.getTeam2())));

        LOGGER.trace("Matches successfully gotten");
        return Response.ok().entity(GamePageDto.from(page, uriInfo)).build();
    }

    @POST
    public Response createGame(@RequestBody final String requestBody) {
        GameValidators.creationValidatorOf("Match creation fails, invalid creation JSON")
                .validate(JSONUtils.jsonObjectFrom(requestBody));
        final GameDto gameDto = JSONUtils.jsonToObject(requestBody, GameDto.class);
        PlaceDto location = gameDto.getLocation().orElse(PlaceDto.from(new Place(null, null, null, null)));
        Game game = gameService.create(gameDto.getTeamName1(), gameDto.getTeamName2(),
                    getStartTimeFrom(gameDto), gameDto.getMinutesOfDuration(), gameDto.getCompetitive(),
                    gameDto.isIndividual(),  location.getCountry(), location.getState(), location.getCity(),
                    location.getStreet(), gameDto.getTornamentName(), gameDto.getDescription(), gameDto.getTitle(),
                    gameDto.getSport());
        return Response.status(HttpStatus.CREATED.value())
                .entity(GameDto.from(game, getTeam(game.getTeam1()), getTeam(game.getTeam2())))
                .build();
    }

    @GET
    @Path("/{key}")
    public Response getGame(@PathParam("key") String key) {
        GameValidators.keyValidator("Invalid '" + key + "' key for a game").validate(key);
        Game game = gameService.findByKey(key).orElseThrow(() -> {
            LOGGER.error("Get game failed because there is no game '{}'", key);
            return GameNotFoundException.ofKey(key);
        });
        LOGGER.trace("Match '{}' founded successfully", key);
        return Response.ok(GameDto.from(game, getTeam(game.getTeam1()), getTeam(game.getTeam2()))).build();
    }

    @DELETE
    @Path("/{key}")
    public Response deleteGame(@PathParam("key") String key) {
        GameValidators.keyValidator("Invalid '" + key + "' key for a match").validate(key);
        gameService.remove(key);
        LOGGER.trace("Match '{}' deleted successfully", key);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{key}")
    public Response updateGame(@PathParam("key") String key, @RequestBody final String requestBody) {
        GameValidators.keyValidator("Invalid '" + key + "' key for a game").validate(key);
        GameValidators.updateValidatorOf("Match update fails, invalid creation JSON")
                .validate(JSONUtils.jsonObjectFrom(requestBody));
        final GameDto gameDto = JSONUtils.jsonToObject(requestBody, GameDto.class);
        PlaceDto location = gameDto.getLocation().orElse(PlaceDto.from(new Place(null, null, null, null)));
        Game newGame = gameService.modify(gameDto.getTeamName1(), gameDto.getTeamName2(), getStartTimeFrom(gameDto),
                gameDto.getMinutesOfDuration(), null, null, location.getCountry(), location.getState(),
                location.getCity(), location.getStreet(), null, gameDto.getDescription(),
                gameDto.getTitle(), key);
        LOGGER.trace("Match '{}' modified successfully", key);
        return Response.ok(GameDto.from(newGame, getTeam(newGame.getTeam1()),
                getTeam(newGame.getTeam2()))).build();
    }

    @POST
    @Path("/{key}/players/requestToJoin")
    public Response createTemporalUser(@PathParam("key") String key, @RequestBody final String requestBody,
                                       @Context HttpServletRequest request) {
        GameValidators.keyValidator("Invalid '" + key + "' key for a game").validate(key);
        PlayerValidators.createValidatorOf("Temporal user creation fails, invalid JSON")
                .validate(JSONUtils.jsonObjectFrom(requestBody));
        final TeamPlayerDto teamPlayerDto = JSONUtils.jsonToObject(requestBody, TeamPlayerDto.class);
        Locale locale = LocaleUtils.validateLocale(request.getLocales());
        gameService.createRequestToJoin(key, teamPlayerDto.getFirstName(), teamPlayerDto.getLastName(),
                teamPlayerDto.getEmail(), locale);
        return Response.status(HttpStatus.CREATED.value()).header(HttpHeaders.ACCEPT_LANGUAGE, locale.toString()).build();
    }

    @POST
    @Path("/{key}/players")
    public Response addUserToGame(@PathParam("key") String key, @RequestBody final String requestBody,
                                  @Context HttpServletRequest request) {
        GameValidators.keyValidator("Invalid '" + key + "' key for a game").validate(key);
        PlayerValidators.updateValidatorOf("Add player to match fails, invalid creation JSON")
                .validate(JSONUtils.jsonObjectFrom(requestBody));
        final TeamPlayerDto playerDto = JSONUtils.jsonToObject(requestBody, TeamPlayerDto.class);
        Locale locale = LocaleUtils.validateLocale(request.getLocales());
        Game game = gameService.insertPlayerInGame(key, playerDto.getUserId(), request.getHeader(CODE_HEADER), locale);
        LOGGER.trace("User '{}' added successfully to match '{}'", playerDto.getUserId(), key);
        Response.ResponseBuilder response = Response.ok(GameDto.from(game, getTeam(game.getTeam1()), getTeam(game.getTeam2())));
        if (playerDto.getUsername() == null) {
            response = response.header(HttpHeaders.ACCEPT_LANGUAGE, locale.toString());
        }
        return response.build();
    }

    @DELETE
    @Path("/{key}/players/{id}")
    public Response removeUserByIdFromGame(@PathParam("key") String key, @PathParam("id") long userId,
                                           @Context HttpServletRequest request) {
        GameValidators.keyValidator("Invalid '" + key + "' key for a game").validate(key);
        gameService.deleteUserInGameWithCode(key, userId, request.getHeader(CODE_HEADER));
        LOGGER.trace("User with id '{}' in match '{}' deleted successfully", userId, key);
        return Response.noContent().build();
    }

    @POST
    @Path("/{key}/result")
    public Response addResultToAGame(@PathParam("key") String key, @RequestBody final String requestBody) {
        GameValidators.keyValidator("Invalid '" + key + "' key for a game").validate(key);
        ResultValidators.creationValidatorOf("Add result to match fails, invalid creation JSON")
                .validate(JSONUtils.jsonObjectFrom(requestBody));
        final ResultDto resultDto = JSONUtils.jsonToObject(requestBody, ResultDto.class);
        Game game = gameService.updateResultOfGame(key, resultDto.getScoreTeam1(),
                resultDto.getScoreTeam2());
        LOGGER.trace("Match '{}' result added successfully", key);
        return Response.ok(GameDto.from(game, getTeam(game.getTeam1()), getTeam(game.getTeam2()))).build();
    }

    private LocalDateTime getStartTimeFrom(GameDto gameDto) {
        if (!gameDto.getDate().isPresent() || !gameDto.getTime().isPresent()) {
            return null;
        }
        DateDto dateDto = gameDto.getDate().get();
        TimeDto timeDto = gameDto.getTime().get();
        return LocalDateTime.of(dateDto.getYear(), dateDto.getMonthNumber(),
                dateDto.getDayOfMonth(), timeDto.getHour(), timeDto.getMinute(), 0);
    }

    private TeamDto getTeam(Team team) {
        return Optional.ofNullable(team)
                .map(it -> TeamDto.from(teamService.getAccountsMap(it), it))
                .orElse(null);
    }
}
