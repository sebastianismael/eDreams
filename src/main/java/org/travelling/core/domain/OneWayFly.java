package org.travelling.core.domain;

import java.time.LocalTime;

public class OneWayFly implements Comparable<OneWayFly>{

    private String flyNumber;
    private FlightRoute flightRoute;
    private Double price;
    private Integer durationInMinutes;
    private LocalTime departureHour;
    private LocalTime arrivalHour;

    public OneWayFly(){
    }

    public OneWayFly(FlightRoute route) {
        flightRoute = route;
    }

    public String getFlyNumber() {
        return flyNumber;
    }

    public void setFlyNumber(String flyNumber) {
        this.flyNumber = flyNumber;
    }

    public void setFlightRoute(FlightRoute flightRoute) {
        this.flightRoute = flightRoute;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public void setDepartureHour(LocalTime departureHour) {
        this.departureHour = departureHour;
    }

    public void setArrivalHour(LocalTime arrivalHour) {
        this.arrivalHour = arrivalHour;
    }

    public FlightRoute getFlightRoute() {
        return flightRoute;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public LocalTime getDepartureHour() {
        return departureHour;
    }

    public LocalTime getArrivalHour() {
        return arrivalHour;
    }

    @Override
    public int compareTo(OneWayFly o) {
        return this.durationInMinutes.compareTo(o.durationInMinutes);
    }
}
