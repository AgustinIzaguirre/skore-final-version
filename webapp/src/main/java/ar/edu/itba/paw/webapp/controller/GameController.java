package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.GameService;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Team;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/game")
@Controller
public class GameController {
    @Autowired
    @Qualifier("gameServiceImpl")
    private GameService gameService;

    @RequestMapping(value="/filter", method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody String filterGames(
            @RequestParam final String minStartTime, @RequestParam final String maxStartTime,
            @RequestParam final String minFinishTime, @RequestParam final String maxFinishTime,
            @RequestParam final ArrayList<String> types, @RequestParam final List<String> sportNames,
            @RequestParam final Integer minQuantity, @RequestParam final Integer maxQuantity,
            @RequestParam final List<String> countries, @RequestParam final List<String> states,
            @RequestParam final List<String> cities, @RequestParam final Integer minFreePlaces,
            @RequestParam final Integer maxFreePlaces) throws Exception{

        List<Game> games = gameService.findGames(minStartTime, maxStartTime, minFinishTime,
                maxFinishTime, types, sportNames, minQuantity,  maxQuantity, countries, states,
                cities, minFreePlaces, maxFreePlaces);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(games);
    }
}