package org.travelling.core.domain;

import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;

public class FlightsFinder {

    private FlightsRepository flightsRepository;

    public FlightsFinder(FlightsRepository flightsRepository) {
        this.flightsRepository = flightsRepository;
    }

    public Optional<Fly> findCheapestWithShortDuration(String from, String to) {
        final List<OneWayFly> directFlights = searchDirectFlights(from, to);

        if(!directFlights.isEmpty())
            return Optional.of(aFlyWith(directFlights.get(0)));

        return Optional.empty();
    }

    private List<OneWayFly> searchDirectFlights(String from, String to) {
        final List<OneWayFly> directFlights = flightsRepository.flightsOf(from, to);
        directFlights.sort(comparing(OneWayFly::getPrice));
        return directFlights;
    }

    private Fly aFlyWith(OneWayFly... sections){
        Fly fly = new Fly();
        for(OneWayFly section: sections)
            fly.addSection(section);
        return fly;
    }
}
