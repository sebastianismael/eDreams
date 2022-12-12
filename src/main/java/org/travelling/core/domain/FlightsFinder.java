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
        final List<OneWayFly> flights = flightsRepository.flightsOf(from, to);

        if(flights.isEmpty()) return Optional.empty();

        SortedSet<OneWayFly> sortByPrice = new TreeSet<>(comparing(OneWayFly::getPrice));
        final Integer quickest = flights.get(0).getDurationInMinutes();

        for(OneWayFly each : flights){
            if(each.getDurationInMinutes().equals(quickest))
                sortByPrice.add(each);
        }
        return Optional.of(sortByPrice.first());
    }
}
