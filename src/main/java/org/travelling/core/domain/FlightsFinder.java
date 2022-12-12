package org.travelling.core.domain;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Comparator.comparing;

public class FlightsFinder {

    private FlightsRepository flightsRepository;

    public FlightsFinder(FlightsRepository flightsRepository) {
        this.flightsRepository = flightsRepository;
    }

    public Optional<OneWayFly> findCheapestWithShortDuration(String from, String to) {
        final List<OneWayFly> directFlights = searchDirectFlights(from, to);

        // TODO to eb replaced when implement no direct flights behavior
        if(directFlights.isEmpty()) return Optional.empty();
        return Optional.of(directFlights.get(0));
    }

    private List<OneWayFly> searchDirectFlights(String from, String to) {
        final List<OneWayFly> directFlights = flightsRepository.flightsOf(from, to);
        directFlights.sort(comparing(OneWayFly::getPrice));
        return directFlights;
    }
}
