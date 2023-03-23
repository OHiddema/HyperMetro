package metro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Node {
    private static final ArrayList<Node> instances = new ArrayList<>();

    private final Route route;
    private final Station station;
    private final Map<Node, Integer> neighbors;
    private Integer distance = Integer.MAX_VALUE;
    private Node routeToNode = null;
    private boolean visited = false;

    public Node(Route route, Station station) {
        this.station = station;
        this.route = route;
        this.neighbors = new HashMap<>();
        instances.add(this);
    }

    public static ArrayList<Node> getAllInstances() {
        return instances;
    }

    public static Node getNodeFromRouteStation(Route route, Station station) {
        return Node.getAllInstances().stream()
                .filter(e -> e.getRoute().equals(route))
                .filter(e -> e.getStation().equals(station))
                .findFirst().orElseThrow();
    }

    public static Node getNodeFromTransfer(Transfer transfer) {
        return Node.getNodeFromRouteStation(transfer.getRoute(), transfer.getStation());
    }

    public Route getRoute() {
        return route;
    }

    public Station getStation() {
        return station;
    }

    public Map<Node, Integer> getNeighbors() {
        return neighbors;
    }

    public Node getRouteToNode() {
        return routeToNode;
    }

    public void setRouteToNode(Node routeToNode) {
        this.routeToNode = routeToNode;
    }

    public Integer getDistance() {
        return distance;
    }

    public boolean isVisited() {
        return !visited;
    }

    public void setVisited() {
        this.visited = true;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void addNeighbor(Node neighbor, Integer weight) {
        this.neighbors.put(neighbor, weight);
    }
}