package org.travelling.core.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlightsFinderTest {

    private static final String MIA = "MIA";
    private static final String ROM = "ROM";
    private static final String MAD = "MAD";
    private FlightsFinder finder;
    private FlightsRepository flightsRepository;
    private List<OneWayFly> listOfDirectFlights;

    @BeforeEach
    public void init(){
        listOfDirectFlights = new LinkedList<>();
        flightsRepository = mock(FlightsRepository.class);
        finder = new FlightsFinder(flightsRepository);
    }

    @Test
    public void notReturnResultsIfRouteDoesntExist(){

        givenNotExistsFlightsFor(MIA, ROM);

        Optional<Fly> found = finder.findCheapestWithShortDuration(MIA, ROM);

        thenThereIsNotFlights(found);
    }

    @Test
    public void returnTheSingleDirectFlyIfThereIsJustOnePrice(){
        givenExistsDirectFlights(MIA, ROM,
                aFly(MIA, ROM).withDuration(1000).withPrice(200.0));

        Optional<Fly> found = finder.findCheapestWithShortDuration(MIA, ROM);

        thenGetAFlyWithPriceAndDuration(200.0, 1000, found);
    }

    @Test
    public void returnTheCheaperDirectFlyIfThereAreSeveralPrices(){
        givenExistsDirectFlights(MIA, ROM,
                    aFly(MIA, ROM).withDuration(1000).withPrice(200.0),
                    aFly(MIA, ROM).withDuration(1000).withPrice(190.0),
                    aFly(MIA, ROM).withDuration(1000).withPrice(110.0)
        );

        Optional<Fly> found = finder.findCheapestWithShortDuration(MIA, ROM);

        thenGetAFlyWithPriceAndDuration(110.0, 1000, found);
    }

    @Test
    public void returnTheCheaperDirectFlyIfThereAreNoJustDirectFlights(){
        givenExistsDirectFlights(MIA, ROM,
                    aFly(MIA, ROM).withDuration(1000).withPrice(200.0),
                    aFly(MIA, ROM).withDuration(1000).withPrice(190.0),
                    aFly(MIA, ROM).withDuration(1000).withPrice(110.0)
        );

        givenExistsFlights(MIA,
                aFly(MIA, MAD).withDuration(600).withPrice(190.0));

        givenExistsFlights(MAD,
                aFly(MAD, ROM).withDuration(1200).withPrice(110.0));

        Optional<Fly> found = finder.findCheapestWithShortDuration(MIA, ROM);

        thenGetAFlyWithPriceAndDuration(110.0, 1000, found);
    }

    @Test
    public void returnCheaperNoDirectFlyIfThereAreNotDirects(){
        givenNotExistsFlightsFor(MIA, ROM);
        givenExistsFlights(MIA,
                aFly(MIA, MAD).withDuration(600).withPrice(190.0));
        givenExistsFlights(MAD,
                aFly(MAD, ROM).withDuration(200).withPrice(110.0),
                aFly(MAD, ROM).withDuration(300).withPrice(50.0));

        Optional<Fly> found = finder.findCheapestWithShortDuration(MIA, ROM);

        thenGetAFlyWithPriceAndDuration(300.0, 800, found);
    }

    @Test
    public void returnCheaperIfThereAreNotDirectsWithTwoSameDuration(){
        givenNotExistsFlightsFor(MIA, ROM);
        givenExistsFlights(MIA,
                aFly(MIA, MAD).withDuration(600).withPrice(190.0));
        givenExistsFlights(MAD,
                aFly(MAD, ROM).withDuration(300).withPrice(110.0),
                aFly(MAD, ROM).withDuration(300).withPrice(50.0));

        Optional<Fly> found = finder.findCheapestWithShortDuration(MIA, ROM);

        thenGetAFlyWithPriceAndDuration(240.0, 900, found);
    }


    private void givenNotExistsFlightsFor(String from, String to) {
        when(flightsRepository.flightsFrom(from, to)).thenReturn(new LinkedList<>());
    }

    private void givenExistsDirectFlights(String from, String to, FlyBuilder... flights){
        for(FlyBuilder each: flights)
            listOfDirectFlights.add(each.fly);

        when(flightsRepository.flightsFrom(from, to)).thenReturn(listOfDirectFlights);
    }

    private void givenExistsFlights(String from, FlyBuilder... flights) {
        List<OneWayFly> listOfFlights = new LinkedList<>();
        for(FlyBuilder each: flights)
            listOfFlights.add(each.fly);

        when(flightsRepository.flightsFrom(from)).thenReturn(listOfFlights);
    }

    private FlyBuilder aFly(String from, String to) {
        return new FlyBuilder(new FlightRoute(from, to));
    }

    private void thenGetAFlyWithPriceAndDuration(double price, int duration, Optional<Fly> found) {
        assertThat(found).isPresent();
        assertThat(found.get().getPrice()).isEqualTo(price);
        assertThat(found.get().totalDuration()).isEqualTo(duration);
    }

    private void thenThereIsNotFlights(Optional<Fly> found) {
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
