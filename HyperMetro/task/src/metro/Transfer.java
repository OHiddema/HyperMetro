package metro;

public class Transfer {
    private final Route route;
    private final Station station;

    public Transfer(Route route, Station station) {
        this.route = route;
        this.station = station;
    }

    public Route getRoute() {
        return route;
    }

    public Station getStation() {
        return station;
    }
}
