package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Exceptions.GameNotFoundException;
import ar.edu.itba.paw.interfaces.GameDao;
import ar.edu.itba.paw.interfaces.GameService;
import ar.edu.itba.paw.interfaces.TeamService;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.PremiumUser;
import ar.edu.itba.paw.models.Team;
import ar.edu.itba.paw.models.User;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);


    @Autowired
    private GameDao gameDao;

    @Autowired
    private TeamService teamService;

    public GameServiceImpl() {

    }

    @Override
    public Game create(final String teamName1, final String teamName2, final String startTime,
                       final String finishTime, final String type, final String result,
                       final String country, final String state, final String city,
                       final String street, final String tornamentName, final String description) {
        Optional<Game> game = gameDao.create(teamName1, teamName2, startTime, finishTime, type, result,
                country, state, city, street, tornamentName, description);
        if(!game.isPresent()) {
            LOGGER.error("Could not create this game: {} vs {} |starting at {} |finishing at {}",
                    teamName1, teamName2, startTime, finishTime);
            throw new GameNotFoundException("There is not a game of " + teamName1 + " vs " + teamName2
                    + " starting at " + startTime + "and finishing at " +finishTime);
        }
        return game.get();
    }

    @Override
    public Game createNoTeamGame(final String startTime, final String finishTime,
                                 final String type, final String country,
                                 final String state, final String city,
                                 final String street, final String tornamentName,
                                 final String description, final String creatorName,
                                 final long creatorId, final String sportName) {
        Team team1 = teamService.createTempTeam1(creatorName, creatorId, sportName);
        return create(team1.getName(), null, startTime, finishTime, type, null,
                      country, state, city, street, tornamentName, description);
    }

    /*@Override
    public Game insertUserInGame(final Game game, final long userId, final boolean toTeam1) {
        if(!toTeam1 && game.getTeam2() == null) {

        }
    }*/

    @Override
    public Game findByKey(String teamName1, String startTime, String finishTime) {
        Optional<Game> game = gameDao.findByKey(teamName1, startTime, finishTime);
        if(!game.isPresent()) {
            LOGGER.error("Could not find a game: {} |starting at {} |finishing at {}",
                    teamName1, startTime, finishTime);
            throw new GameNotFoundException("There is not a game of " + teamName1
                    + " starting at " + startTime + "and finishing at " + finishTime);
        }
        return game.get();
    }

    @Override
    public List<Game> findGamesPage(final String minStartTime, final String maxStartTime,
                                    final String minFinishTime, final String maxFinishTime,
                                    final JSONArray types, final JSONArray sportNames,
                                    final Integer minQuantity, final Integer maxQuantity,
                                    final JSONArray countries, final JSONArray states,
                                    final JSONArray cities, final Integer minFreePlaces,
                                    final Integer maxFreePlaces, final int pageNumber) {
        List<Game> games =
                gameDao.findGames(minStartTime, maxStartTime, minFinishTime, maxFinishTime,
                    jsonArrayToList(types), jsonArrayToList(sportNames), minQuantity,  maxQuantity,
                    jsonArrayToList(countries), jsonArrayToList(states), jsonArrayToList(cities),
                    minFreePlaces, maxFreePlaces, null, false);

        int start = ((pageNumber-1)*10 < games.size())?(pageNumber-1)*10:games.size();
        int end = (pageNumber*10 < games.size())?pageNumber*10:games.size();
        return games.subList(start, end);
    }

    @Override
    public List<Game> findGamesPageThatIsAPartOf(final String minStartTime, final String maxStartTime,
                                                 final String minFinishTime, final String maxFinishTime,
                                                 final JSONArray types, final JSONArray sportNames,
                                                 final Integer minQuantity, final Integer maxQuantity,
                                                 final JSONArray countries, final JSONArray states,
                                                 final JSONArray cities, final Integer minFreePlaces,
                                                 final Integer maxFreePlaces, final int pageNumber,
                                                 final PremiumUser user) {
        List<Game> games =
                gameDao.findGames(minStartTime, maxStartTime, minFinishTime, maxFinishTime,
                        jsonArrayToList(types), jsonArrayToList(sportNames), minQuantity,  maxQuantity,
                        jsonArrayToList(countries), jsonArrayToList(states), jsonArrayToList(cities),
                        minFreePlaces, maxFreePlaces, user, true);

        int start = ((pageNumber-1)*10 < games.size())?(pageNumber-1)*10:games.size();
        int end = (pageNumber*10 < games.size())?pageNumber*10:games.size();
        return games.subList(start, end);
    }

    @Override
    public List<Game> findGamesPageThatIsNotAPartOf(final String minStartTime, final String maxStartTime,
                                                    final String minFinishTime, final String maxFinishTime,
                                                    final JSONArray types, final JSONArray sportNames,
                                                    final Integer minQuantity, final Integer maxQuantity,
                                                    final JSONArray countries, final JSONArray states,
                                                    final JSONArray cities, final Integer minFreePlaces,
                                                    final Integer maxFreePlaces, final int pageNumber,
                                                    final PremiumUser user) {
        List<Game> games =
                gameDao.findGames(minStartTime, maxStartTime, minFinishTime, maxFinishTime,
                        jsonArrayToList(types), jsonArrayToList(sportNames), minQuantity,  maxQuantity,
                        jsonArrayToList(countries), jsonArrayToList(states), jsonArrayToList(cities),
                        minFreePlaces, maxFreePlaces, user, false);

        int start = ((pageNumber-1)*10 < games.size())?(pageNumber-1)*10:games.size();
        int end = (pageNumber*10 < games.size())?pageNumber*10:games.size();
        return games.subList(start, end);
    }

    @Override
    public Game modify(final String teamName1, final String teamName2, final String startTime,
                       final String finishTime, final String type, final String result,
                       final String country, final String state, final String city,
                       final String street, final String tornamentName, final String description,
                       final String teamName1Old, final String teamName2Old,
                       final String startTimeOld, final String finishTimeOld) {
        Optional<Game> game = gameDao.modify(teamName1, teamName2, startTime, finishTime, type, result,
                country, state, city, street, tornamentName, description, teamName1Old, teamName2Old,
                startTimeOld, finishTimeOld);
        if(!game.isPresent()) {
            LOGGER.error("Could not modify this game:: {} vs {} |starting at {} |finishing at {}",
                    teamName1Old, teamName2Old, startTimeOld, finishTimeOld);
            throw new GameNotFoundException("There is not a game of " + teamName1 + " vs " + teamName2
                    + " starting at " + startTime + "and finishing at " + finishTime);
        }
        return game.get();
    }

    private List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<String>();
        for (int i=0; i<jsonArray.length(); i++) {
            list.add( jsonArray.getString(i) );
        }
        return list;
    }
}
