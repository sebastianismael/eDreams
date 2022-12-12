package org.travelling.core.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlightsFinderTest {

    private static final String MIA = "MIA";
    private static final String ROM = "ROM";
    private FlightsFinder finder;
    private FlightsRepository flightsRepository;
    private List<OneWayFly> listOfFlights;

    @BeforeEach
    public void init(){
        listOfFlights = new LinkedList<>();
        flightsRepository = mock(FlightsRepository.class);
        finder = new FlightsFinder(flightsRepository);
    }

    @Test
    public void notReturnResultsIfRouteDoesntExist(){

        givenNotExistsFlightsFor(MIA, ROM);

        Optional<OneWayFly> found = finder.findCheapestWithShortDuration(MIA, ROM);

        thenThereIsNotFlights(found);
    }

    @Test
    public void returnTheSingleDirectFlyIfThereIsJustOnePrice(){
        givenExists(aFly(MIA, ROM).withDuration(1000).withPrice(200.0));

        Optional<OneWayFly> found = finder.findCheapestWithShortDuration(MIA, ROM);

        thenGetAFlyWithPriceAndDuration(200.0, 1000, found);
    }

    @Test
    public void returnTheCheaperDirectFlyIfThereAreSeveralPrices(){
        givenExists(aFly(MIA, ROM).withDuration(1000).withPrice(200.0),
                    aFly(MIA, ROM).withDuration(1000).withPrice(190.0),
                    aFly(MIA, ROM).withDuration(1000).withPrice(110.0));

        Optional<OneWayFly> found = finder.findCheapestWithShortDuration(MIA, ROM);

        thenGetAFlyWithPriceAndDuration(110.0, 1000, found);
    }

    private void givenNotExistsFlightsFor(String from, String to) {
        when(flightsRepository.flightsOf(from, to)).thenReturn(new LinkedList<>());
    }

    private void givenExists(FlyBuilder... flights){
        for(FlyBuilder each: flights)
            listOfFlights.add(each.fly);

        listOfFlights.sort(comparing(OneWayFly::getDurationInMinutes));
        when(flightsRepository.flightsOf(MIA, ROM)).thenReturn(listOfFlights);
    }

    private FlyBuilder aFly(String from, String to) {
        return new FlyBuilder(new FlightRoute(from, to));
    }

    private void thenGetAFlyWithPriceAndDuration(double price, int duration, Optional<OneWayFly> found) {
        assertThat(found).isPresent();
        assertThat(found.get().getPrice()).isEqualTo(price);
        assertThat(found.get().getDurationInMinutes()).isEqualTo(duration);
    }

    private void thenThereIsNotFlights(Optional<OneWayFly> found) {
        assertThat(found).isNotPresent();
    }

    private class FlyBuilder {
        private FlightRoute route;
        private OneWayFly fly;
        public FlyBuilder(FlightRoute route) {
            this.route = route;
            this.fly = new OneWayFly(route);
        }

        public FlyBuilder withDuration(int duration) {
            fly.setDurationInMinutes(duration);
            return this;
        }

        public FlyBuilder withPrice(double price) {
            this.fly.setPrice(price);
            return this;
        }
    }
}
