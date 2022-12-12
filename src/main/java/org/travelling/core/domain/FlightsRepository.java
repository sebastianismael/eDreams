package org.travelling.core.domain;

import java.util.List;

public interface FlightsRepository {

    List<OneWayFly> flightsOf(String from, String to);
}
