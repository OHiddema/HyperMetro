package metro;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Route {
    private static final ArrayList<Route> instances = new ArrayList<>();

    private final String name;
    private final LinkedList<Station> stationList;

    Route(String name) {
        this.name = name;
        this.stationList = new LinkedList<>();
        instances.add(this);
    }

    public static Route getRouteFromName(String name) {
        return Route.getAllInstances().stream().filter(e -> e.getName().equals(name)).findFirst().orElseThrow();
    }

    public static void appendStation(List<String> strings) {
        String time = (strings.size() == 3) ? "0" : strings.get(3);
        Route route = Route.getRouteFromName(strings.get(1));
        route.addLast(new Station(strings.get(2), Integer.parseInt(time)));
    }

    public static void addHeadStation(List<String> strings) {
        String time = (strings.size() == 3) ? "0" : strings.get(3);
        Route route = Route.getRouteFromName(strings.get(1));
        route.addFirst(new Station(strings.get(2), Integer.parseInt(time)));
    }

    public static ArrayList<Route> getAllInstances() {
        return instances;
    }

    public String getName() {
        return this.name;
    }

    public LinkedList<Station> getStationList() {
        return stationList;
    }

    public void addLast(Station station) {
        this.stationList.addLast(station);
    }

    public void addFirst(Station station) {
        this.stationList.addFirst(station);
    }
}
