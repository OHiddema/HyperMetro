package metro;

import java.util.ArrayList;

public class Station {

    private final String name;
    private int time;
    private final ArrayList<Transfer> transfer;

    Station(String name) {
        this.name = name;
        this.time = 0;
        this.transfer = new ArrayList<>();
    }

    Station(String name, int time) {
        this.name = name;
        this.time = time;
        this.transfer = new ArrayList<>();
    }

    public static Station getStationFromName(Route route, String name) {
        return route.getStationList().stream().filter(e -> e.getName().equals(name)).findFirst().orElseThrow();
    }

    public static void removeStation(String routeName, String stationName) {
        Route route = Route.getRouteFromName(routeName);
        Station station = Station.getStationFromName(route, stationName);
        route.getStationList().remove(station);
    }

    public static void connectStations(String route1, String station1, String route2, String station2) {
        Route routeFrom = Route.getRouteFromName(route1);
        Station stationFrom = Station.getStationFromName(routeFrom, station1);
        Route routeTo = Route.getRouteFromName(route2);
        Station stationTo = Station.getStationFromName(routeTo, station2);
        stationFrom.addTransfer(routeTo, stationTo);
    }
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Transfer> getTransfer() {
        return transfer;
    }

    public void addTransfer(Route route, Station station) {
        this.transfer.add(new Transfer(route, station));
    }
}
