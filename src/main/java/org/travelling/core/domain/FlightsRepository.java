package org.travelling.core.domain;

import java.util.List;

public interface FlightsRepository {

    List<OneWayFly> flightsFrom(String from, String to);

    List<OneWayFly> flightsFrom(String from);
}
