package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Exceptions.SportNotFoundException;
import ar.edu.itba.paw.interfaces.SportDao;
import ar.edu.itba.paw.interfaces.SportService;
import ar.edu.itba.paw.models.Sport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SportServiceImpl implements SportService {

    @Autowired
    private SportDao sportDao;

    public Sport findByName(final String sportName) {
        Optional<Sport> sport = sportDao.findByName(sportName);
        if(sport.isPresent()) {
            return sport.get();
        }
        throw new SportNotFoundException("Can't find sport with name: " + sportName);
    }


    public Sport create(final String sportName, final int playerQuantity, final String displayName) {
        Optional<Sport> sport = sportDao.create(sportName, playerQuantity, displayName);
        if(sport.isPresent()) {
            return sport.get();
        }
        throw new SportNotFoundException("Can't find sport with name: " + sportName);

    }
    public boolean remove(final String sportName) {
        return sportDao.remove(sportName);
    }


    public List<Sport> getAllSports() {
        return sportDao.getAllSports();
    }

}