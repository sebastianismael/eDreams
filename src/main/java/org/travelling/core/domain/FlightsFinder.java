package org.travelling.core.domain;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.travelling.core.domain.FlyCompletion.COMPLETED;
import static org.travelling.core.domain.FlyCompletion.NOT_COMPLETED;

public class FlightsFinder {

    private FlightsRepository flightsRepository;

    public FlightsFinder(FlightsRepository flightsRepository) {
        this.flightsRepository = flightsRepository;
    }

    public Optional<Fly> findCheapestWithShortDuration(String from, String to) {
        final List<OneWayFly> directFlights = searchDirectFlights(from, to);

        if (!directFlights.isEmpty())
            return Optional.of(aFlyWith(directFlights.get(0)));


        List<Fly> completedSortedFlights;
        List<Fly> notCompletedFlights = searchOneWayFlightsWithOrigin(from);
        Optional<Fly> shortest = Optional.empty();

        do {
            notCompletedFlights = addASectionTo(notCompletedFlights);

            final Map<FlyCompletion, List<Fly>> split = splitCompleted(notCompletedFlights, to);
            completedSortedFlights = split.get(COMPLETED);
            notCompletedFlights = split.get(NOT_COMPLETED);

            if (!completedSortedFlights.isEmpty())
                shortest = Optional.of(cheapestOf(completedSortedFlights));
            notCompletedFlights = keepFastestNotCompleted(completedSortedFlights, notCompletedFlights);

        } while (!notCompletedFlights.isEmpty());

        return shortest;
    }

    private List<Fly> searchOneWayFlightsWithOrigin(String from) {
        return flightsRepository.flightsFrom(from).stream().map(this::flyWith).collect(toList());
    }

    private List<OneWayFly> searchDirectFlights(String from, String to) {
        final List<OneWayFly> directFlights = flightsRepository.flightsFrom(from, to);
        directFlights.sort(comparing(OneWayFly::getPrice));
        return directFlights;
    }

    private Fly cheapestOf(List<Fly> completedSorted) {
        Fly cheapest = completedSorted.get(0);
        for (Fly fly : completedSorted) {
            if (haveSameDuration(cheapest, fly) && isFlyCheaperThan(cheapest, fly))
                cheapest = fly;
        }
        return cheapest;
    }

    private static boolean isFlyCheaperThan(Fly cheapest, Fly fly) {
        return fly.getPrice() < cheapest.getPrice();
    }

    private static boolean haveSameDuration(Fly cheapest, Fly fly) {
        return fly.totalDuration().equals(cheapest.totalDuration());
    }

    private List<Fly> keepFastestNotCompleted(List<Fly> completedFlight, List<Fly> incompleteFlights) {
        if (completedFlight.isEmpty()) return completedFlight;
        return incompleteFlights.stream()
                .filter(it -> it.totalDuration() < completedFlight.get(0).totalDuration())
                .collect(toList());
    }

    private List<Fly> addASectionTo(List<Fly> flights) {
        List<Fly> result = new LinkedList<>();
        for (Fly fly : flights) {
            result.addAll(composeSections(fly));
        }
        return result;
    }

    private List<Fly> composeSections(Fly flyToComplete) {
        List<Fly> result = new LinkedList<>();
        final List<OneWayFly> sectionsToAdd = flightsRepository.flightsFrom(flyToComplete.destination());
        for (OneWayFly section : sectionsToAdd) {
            Fly fly = new Fly(flyToComplete.getSections());
            fly.addSection(section);
            result.add(fly);
        }
        return result;
    }

    private Map<FlyCompletion, List<Fly>> splitCompleted(List<Fly> candidates, String finalDestination) {
        List<List<Fly>> splitFlights = new ArrayList<>(
                candidates.stream()
                        .collect(Collectors.partitioningBy(
                                fly -> fly.destination().equals(finalDestination))
                        ).values()
        );
        Map<FlyCompletion, List<Fly>> results = new HashMap<>();
        results.put(NOT_COMPLETED, splitFlights.get(0));
        results.put(COMPLETED, sortByDuration(splitFlights.get(1)));
        return results;
    }

    private List<Fly> sortByDuration(List<Fly> toBeSorted) {
        return toBeSorted.stream().sorted(comparing(Fly::totalDuration)).collect(toList());
    }

    private Fly flyWith(OneWayFly section) {
        return new Fly(section);
    }

    private Fly aFlyWith(OneWayFly... sections) {
        Fly fly = new Fly();
        for (OneWayFly section : sections)
            fly.addSection(section);
        return fly;
    }
}

enum FlyCompletion {
    COMPLETED, NOT_COMPLETED;
}
