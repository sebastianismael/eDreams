package org.travelling.core.infrastructure;

import org.travelling.core.domain.FlightsRepository;
import org.travelling.core.domain.OneWayFly;
import redis.clients.jedis.Jedis;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;

public class FlightsRedisRepository implements FlightsRepository {

    private Jedis jedis;

    public FlightsRedisRepository(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public List<OneWayFly> flightsOf(String from, String to) {

        final Map<String, String> flights = jedis.hgetAll(keyWith(from, to));

        return flights.keySet()
                .stream()
                .map(flyNumber -> toModel(flyNumber, flights.get(flyNumber)))
                .collect(toCollection(LinkedList::new));
    }

    private OneWayFly toModel(String flyNumber, String data){
        OneWayFly oneWayFly = new OneWayFly();
        oneWayFly.setFlyNumber(flyNumber);
        bindWithData(oneWayFly, data);
        return oneWayFly;
    }

    private String keyWith(String from, String to) {
        return "FLIGHTS:" + from + ":" + to;
    }

    private void bindWithData(OneWayFly oneWayFly, String jsonData) {
        // Deserialize json and bind Fly with it
    }
}
