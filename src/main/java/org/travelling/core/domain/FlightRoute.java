package org.travelling.core.domain;

public class FlightRoute {

    public FlightRoute(String from, String to) {
        this.from = from;
        this.to = to;
    }

    private String from;
    private String to;

    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }

}
