package org.travelling.core.domain;

import java.util.ArrayList;
import java.util.List;

public class Fly {

    private List<OneWayFly> sections = new ArrayList<>();

    public Fly(OneWayFly section) {
        this.sections.add(section);
    }

    public Fly(List<OneWayFly> sections) {
        this.sections.addAll(sections);
    }

    public Fly() {
    }

    public Fly addSection(OneWayFly oneWayFly) {
        sections.add(oneWayFly);
        return this;
    }

    public List<OneWayFly> getSections() {
        return sections;
    }

    public Integer totalDuration(){
        return sections.stream()
                .map(OneWayFly::getDurationInMinutes)
                .reduce(0, Integer::sum);
    }

    public Double getPrice(){
        return sections.stream()
                .map(OneWayFly::getPrice)
                .reduce(0.0, Double::sum);
    }

    public String origin(){
        return !sections.isEmpty() ? sections.get(0).getFlightRoute().getFrom() : "";
    }

    public String destination(){
        return !sections.isEmpty() ? sections.get(sections.size()-1).getFlightRoute().getTo() : "";
    }
}
