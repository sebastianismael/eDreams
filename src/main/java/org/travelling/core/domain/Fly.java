package org.travelling.core.domain;

import java.util.ArrayList;
import java.util.List;

public class Fly {

    private List<OneWayFly> sections = new ArrayList<>();

    public boolean addSection(OneWayFly oneWayFly) {
        return sections.add(oneWayFly);
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
}
