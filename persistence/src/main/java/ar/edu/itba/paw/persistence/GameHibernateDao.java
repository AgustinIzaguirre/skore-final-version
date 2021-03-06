package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.notfound.TeamNotFoundException;
import ar.edu.itba.paw.interfaces.GameDao;
import ar.edu.itba.paw.interfaces.TeamDao;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class GameHibernateDao implements GameDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameHibernateDao.class);
    private static final String LIKED_USER_PLAY_GAME_QUERY =
            " AND (EXISTS" +
            "(SELECT f" +
            " FROM Game g, Team t JOIN t.players p, PremiumUser u2 JOIN u2.friends f" +
            " WHERE games = g AND g.primaryKey.team1.teamName = t.teamName AND u2.userName = :username AND f.user.userId = p.userId) " +
            "OR EXISTS" +
            "(SELECT p" +
            " FROM Game g, Team t JOIN t.players p, PremiumUser u2 JOIN u2.friends f" +
            " WHERE games = g AND g.team2.teamName = t.teamName AND u2.userName = :username AND f.user.userId = p.userId))";
    public static final String LIKED_SPORT_OF_GAME_QUERY = " AND (EXISTS" +
            "(SELECT s" +
            " FROM Game g, Team t, PremiumUser u2 JOIN u2.likes s" +
            " WHERE games = g AND g.primaryKey.team1.teamName = t.teamName AND u2.userName = :username AND s.sportName = t.sport.sportName) " +
            "OR EXISTS" +
            "(SELECT s" +
            " FROM Game g, Team t, PremiumUser u2 JOIN u2.likes s" +
            " WHERE games = g AND g.team2.teamName = t.teamName AND u2.userName = :username AND s.sportName = t.sport.sportName))";

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TeamDao teamDao;

    private static final String QUERY_START =
            "SELECT games " +
            "FROM Game as games, Team t " +
            "WHERE teamName1 = t.teamName";
    private static final String GREATER_OR_EQUAL_TO    = ">=";
    private static final String LESS_OR_EQUAL_TO       = "<=";
    private static final String START_TIME_MAX         = "startTimeMax";
    private static final String FINISH_TIME_MIN        = "finishTimeMin";
    private static final String FINISH_TIME_MAX        = "finishTimeMax";
    private static final String QUERY_START_TIME_NAME  = "games.primaryKey.startTime";
    private static final String QUERY_FINISH_TIME_NAME = "games.primaryKey.finishTime";
    private static final String START_TIME_MIN         = "startTimeMin";
    private static final String SPORT_NAME             = "sportName";
    private static final String TYPE                   = "type";
    private static final String QUERY_SPORT_NAME       = "t.sport.sportName";
    private static final String COUNTRY                = "country";
    private static final String STATE                  = "state";
    private static final String CITY                   = "city";
    private static final String QUERY_USER_NAME        = "games.primaryKey.team1.leader.userName";
    private static final String USERNAME_PI            = "usernamePI";
    private static final String USERNAME_PNI           = "usernamePNI";
    private static final String USERNAME_CI            = "usernameCI";
    private static final String USERNAME_CNI           = "usernameCNI";
    private static final String QUERY_QUANTITY         = "games.primaryKey.team1.sport.playerQuantity";
    private static final String MIN_QUANTITY           = "minQuantity";
    private static final String MAX_QUANTITY           = "maxQuantity";
    private static final String QUERY_FREE_QUANTITY    = "(2 * games.primaryKey.team1.sport.playerQuantity - " +
                                "((CASE WHEN teamName2 IS NULL THEN 0 ELSE (SELECT teams.players.size FROM Team as " +
                                "teams WHERE teams.teamName = teamName2) END) + " +
                                "games.primaryKey.team1.players.size))";
    private static final String MIN_FREE_PLACES        = "minFreePlaces";
    private static final String MAX_FREE_PLACES        = "maxFreePlaces";

    @Override
    public Optional<Game> create(final String teamName1, final String teamName2, final LocalDateTime startTime,
                                 final LocalDateTime finishTime, final String type, final String result,
                                 final String country, final String state, final String city,
                                 final String street, final String tornamentName, final String description,
                                 final String title) {
        LOGGER.trace("Try to create game: {} vs {} |starting at {} |finishing at {}",
                teamName1, teamName2, startTime.toString(), finishTime.toString());
        Team team1 = teamDao.findByTeamName(teamName1)
                .orElseThrow(() -> TeamNotFoundException.ofId(teamName1));
        Team team2;
        if(teamName2 != null) {
            team2 = teamDao.findByTeamName(teamName2)
                    .orElseThrow(() -> TeamNotFoundException.ofId(teamName2));
        }
        else {
            team2 = null;
        }
        Game game = new Game(team1, team2, new Place(country, state, city, street), startTime, finishTime,
                type, result, description, title, tornamentName);
        em.persist(game);
        LOGGER.trace("Successfully create game: {} vs {} |starting at {} |finishing at {}",
                teamName1, teamName2, startTime.toString(), finishTime.toString());
        return Optional.of(game);
    }

    @Override
    public Optional<Game> findByKey(String teamName1, LocalDateTime startTime, LocalDateTime finishTime) {
        LOGGER.trace("Try to find game: {} |starting at {} |finishing at {}", teamName1, startTime.toString(),
                finishTime.toString());
        Team team1 = teamDao.findByTeamName(teamName1)
                .orElseThrow(() -> TeamNotFoundException.ofId(teamName1));
        Game game = em.find(Game.class, new GamePK(team1, startTime, finishTime));
        LOGGER.trace("Returning what was find");
        return Optional.ofNullable(game);
    }

    @Override
    public List<Game> findGames(final LocalDateTime minStartTime, final LocalDateTime maxStartTime,
                                final LocalDateTime minFinishTime, final LocalDateTime maxFinishTime,
                                final List<String> types, final List<String> sportNames,
                                final Integer minQuantity, final Integer maxQuantity,
                                final List<String> countries, final List<String> states,
                                final List<String> cities, final Integer minFreePlaces,
                                final Integer maxFreePlaces, final List<String> usernamesPlayersInclude,
                                final List<String> usernamesPlayersNotInclude,
                                final List<String> usernamesCreatorsInclude,
                                final List<String> usernamesCreatorsNotInclude, final GameSort sort,
                                final Boolean onlyWithResults, final String currentUsername, final boolean onlyLikedUsersPlay,
                                final boolean onlyLikedSports) {
        DaoHelper daoHelper = new DaoHelper(QUERY_START);
        daoHelper.addFilter(QUERY_START_TIME_NAME, LESS_OR_EQUAL_TO, START_TIME_MIN, minStartTime);
        daoHelper.addFilter(QUERY_START_TIME_NAME, GREATER_OR_EQUAL_TO, START_TIME_MAX, maxStartTime);
        daoHelper.addFilter(QUERY_FINISH_TIME_NAME, LESS_OR_EQUAL_TO, FINISH_TIME_MIN, minFinishTime);
        daoHelper.addFilter(QUERY_FINISH_TIME_NAME, GREATER_OR_EQUAL_TO, FINISH_TIME_MAX, maxFinishTime);

        daoHelper.addListFilters(true, false, TYPE, TYPE, types);
        daoHelper.addFilter(QUERY_QUANTITY, LESS_OR_EQUAL_TO, MIN_QUANTITY, minQuantity);
        daoHelper.addFilter(QUERY_QUANTITY, GREATER_OR_EQUAL_TO, MAX_QUANTITY, maxQuantity);

        daoHelper.addListFilters(false, false, QUERY_SPORT_NAME, SPORT_NAME, sportNames);
        daoHelper.addListFilters(false, false, COUNTRY, COUNTRY, countries);
        daoHelper.addListFilters(false, false, STATE, STATE, states);
        daoHelper.addListFilters(false, false, CITY, CITY, cities);

        daoHelper.addFilter(QUERY_FREE_QUANTITY, LESS_OR_EQUAL_TO, MIN_FREE_PLACES, minFreePlaces);
        daoHelper.addFilter(QUERY_FREE_QUANTITY, GREATER_OR_EQUAL_TO, MAX_FREE_PLACES, maxFreePlaces);

        daoHelper.addListFilters(true, USERNAME_PI, usernamesPlayersInclude);
        daoHelper.addListFilters(false, USERNAME_PNI, usernamesPlayersNotInclude);

        daoHelper.addListFilters(true, false, QUERY_USER_NAME, USERNAME_CI, usernamesCreatorsInclude);
        daoHelper.addListFilters(true, true, QUERY_USER_NAME, USERNAME_CNI, usernamesCreatorsNotInclude);
        String aux = "";
        if (currentUsername != null) {
            if (onlyLikedUsersPlay) {
                daoHelper.addFilterCustom(LIKED_USER_PLAY_GAME_QUERY);
            }

            if (onlyLikedSports) {
                daoHelper.addFilterCustom(LIKED_SPORT_OF_GAME_QUERY);
            }
        }
        daoHelper.addFilterOnlyFinished(onlyWithResults);

        final TypedQuery<Game> query = em.createQuery(daoHelper.getQuery() + aux +
                (sort != null ? sort.toQuery() : ""), Game.class);
        List<String> valueName = daoHelper.getValueNames();
        List<Object> values    = daoHelper.getValues();

        if (currentUsername != null && (onlyLikedSports || onlyLikedUsersPlay)) {
            query.setParameter("username" , currentUsername);
        }
        for(int i = 0; i < valueName.size(); i++) {
            query.setParameter(valueName.get(i), values.get(i));
        }

        return query.getResultList();
    }

    @Override
    public List<Game> gamesThatAUserPlayInTeam1(final long userId) {
        String queryString =
                "SELECT games " +
                "FROM Game as games " +
                "WHERE :userId IN " +
                            "(SELECT p.userId " +
                            "FROM Game g, Team t JOIN t.players p " +
                            "WHERE games = g AND g.primaryKey.team1.teamName = t.teamName " +
                                "AND g.result IS NOT NULL)";
        final TypedQuery<Game> query = em.createQuery(queryString, Game.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Game> gamesThatAUserPlayInTeam2(final long userId) {
        String queryString =
                "SELECT games " +
                "FROM Game as games " +
                "WHERE :userId IN " +
                            "(SELECT p.userId " +
                            "FROM Game g, Team t JOIN t.players p " +
                            "WHERE games = g AND g.team2.teamName = t.teamName " +
                                "AND g.result IS NOT NULL)";
        final TypedQuery<Game> query = em.createQuery(queryString, Game.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public Optional<Game> modify(final String teamName1, final String teamName2, final LocalDateTime startTime,
                                 final LocalDateTime finishTime, final String type, final String result,
                                 final String country, final String state, final String city,
                                 final String street, final String tornamentName, final String description,
                                 final String title, final String teamName1Old, final LocalDateTime startTimeOld,
                                 final LocalDateTime finishTimeOld) {
        Optional<Team> team1Old = teamDao.findByTeamName(teamName1Old);
        if (!team1Old.isPresent()) {
            return Optional.empty();
        }
        Game game = em.find(Game.class, new GamePK(team1Old.get(), startTimeOld, finishTimeOld));
        LOGGER.trace("Try to modify game: {} |starting at {} |finishing at {}", teamName1Old,
                startTimeOld.toString(), finishTimeOld.toString());
        if(game == null) {
            LOGGER.trace("Fail to modify game: {} |starting at {} |finishing at {}", teamName1Old,
                    startTimeOld.toString(), finishTimeOld.toString());
            return Optional.empty();
        }
        if (teamName1 != null) {
            Team team = teamDao.findByTeamName(teamName1)
                    .orElseThrow(() -> TeamNotFoundException.ofId(teamName1));
            game.setTeam1(team);
        }
        if (teamName1 != null) {
            Team team = teamDao.findByTeamName(teamName2)
                    .orElseThrow(() -> TeamNotFoundException.ofId(teamName2));
            game.setTeam2(team);
        }
        if (startTime != null) {
            game.setStartTime(startTime);
        }
        if (finishTime != null) {
            game.setFinishTime(finishTime);
        }
        if (type != null) {
            game.setType(type);
        }
        if (result != null) {
            game.setResult(result);
        }
        if (country != null || state != null || city != null || street != null) {
            game.setPlace(new Place(country, state, city, street));
        }
        if (result != null) {
            game.setTornament(tornamentName);
        }
        if (result != null) {
            game.setDescription(description);
        }
        if (result != null) {
            game.setTitle(title);
        }

        em.merge(game);
        LOGGER.trace("Successfully modify game: {} |starting at {} |finishing at {}", teamName1Old,
                startTimeOld.toString(), finishTimeOld.toString());
        return Optional.of(game);
    }

    @Override
    public boolean remove(final String teamName1, final LocalDateTime startTime, final LocalDateTime finishTime) {
        LOGGER.trace("Try to delete game: {}|{}|{}", teamName1, startTime.toString(), finishTime.toString());
        Optional<Game> game = findByKey(teamName1, startTime, finishTime);
        boolean ans = false;
        if(game.isPresent()) {
            em.remove(game.get());
            ans = true;
        }
        return ans;
    }
}
