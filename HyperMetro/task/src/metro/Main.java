package metro;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static final int TRANSFER_TIME = 5;
    static JsonRoutesConn jsonRoutes;

    public static void main(String[] args) {
        buildFromJson(args[0]);
//        buildFromJson("C:\\Users\\CGstudent\\IdeaProjects\\HyperMetro\\HyperMetro\\task\\src\\metro\\metro.json");
//        buildFromJson("C:\\Users\\CGstudent\\OneDrive\\Bureaublad\\london.json");
        userInteraction();
    }

    private static void buildFromJson(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            Type type = JsonRoutesConn.class.getField("routes").getType();
            jsonRoutes = new JsonRoutesConn(new Gson().fromJson(br, type));
        } catch (IOException e) {
            System.out.println("Error! Such a file doesn't exist!");
            return;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        // First loop -> get all the routes and stations from json and create them
        for (Map.Entry<String, List<LinkedTreeMap<String, Object>>> entry : jsonRoutes.routes.entrySet()) {
            Route route = new Route(entry.getKey());
            for (LinkedTreeMap<String, Object> ltm : entry.getValue()) {
                String stationName = null;
                int stationTime = 0;
                stationName = (String) ltm.get("name");
                if (ltm.get("time") != null) stationTime = ((Double) ltm.get("time")).intValue();
                Station station = new Station(stationName);
                station.setTime(stationTime);
                route.addLast(station);
            }
        }

        // Second loop -> read all transfers from json and create them
        for (Map.Entry<String, List<LinkedTreeMap<String, Object>>> entry : jsonRoutes.routes.entrySet()) {
            Route route = Route.getRouteFromName(entry.getKey());
            for (LinkedTreeMap<String, Object> ltm : entry.getValue()) {
                Station station = Station.getStationFromName(route, (String) ltm.get("name"));
                List<LinkedTreeMap<String, String>> transfers = (List<LinkedTreeMap<String, String>>) ltm.get("transfer");
                for (LinkedTreeMap<String, String> transfer : transfers) {
                    Route transferRoute = Route.getRouteFromName(transfer.get("line"));
                    Station transferStation = Station.getStationFromName(transferRoute, transfer.get("station"));
                    station.addTransfer(transferRoute, transferStation);
                }
            }
        }

        // Create all Nodes
        for (Route route : Route.getAllInstances()) {
            for (Station station : route.getStationList()) {
                new Node(route, station);
            }
        }

        // Set neighbors for all nodes (new)
        for (Map.Entry<String, List<LinkedTreeMap<String, Object>>> entry : jsonRoutes.routes.entrySet()) {
            Route route = Route.getRouteFromName(entry.getKey());
            for (LinkedTreeMap<String, Object> ltm : entry.getValue()) {
                Station station = Station.getStationFromName(route, (String) ltm.get("name"));
                Node node = Node.getNodeFromRouteStation(route, station);

                // all prev stations and their transfers
                List<String> prevStations = (List<String>) ltm.get("prev");
                for (String prevStationName : prevStations) {
                    Station prevStation = Station.getStationFromName(route, prevStationName);
                    Node prevNode = Node.getNodeFromRouteStation(route, prevStation);

                    node.addNeighbor(prevNode, prevStation.getTime());

                    for (Transfer transfer : prevStation.getTransfer()) {
                        Node nn = Node.getNodeFromTransfer(transfer);
                        node.addNeighbor(nn, prevStation.getTime() + TRANSFER_TIME);
                        nn.addNeighbor(node, prevStation.getTime() + TRANSFER_TIME);
                    }
                }

                // all next stations and their transfers
                List<String> nextStations = (List<String>) ltm.get("next");
                for (String nextStationName : nextStations) {
                    Station nextStation = Station.getStationFromName(route, nextStationName);
                    Node nextNode = Node.getNodeFromRouteStation(route, nextStation);

                    node.addNeighbor(nextNode, station.getTime());

                    for (Transfer transfer : nextStation.getTransfer()) {
                        Node nn = Node.getNodeFromTransfer(transfer);
                        node.addNeighbor(nn, station.getTime() + TRANSFER_TIME);
                        nn.addNeighbor(node, station.getTime() + TRANSFER_TIME);
                    }
                }
            }
        }
    }

    private static void userInteraction() {
        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile("\"[^\"]+\"|^[^\\s\"]+|[^\\s\"]+$|(?<=\\s)\\S+(?=\\s)");
        ArrayList<String> params = new ArrayList<>();
        do {
            Matcher matcher = pattern.matcher(scanner.nextLine());
            params.clear();
            while (matcher.find()) {
                params.add(removeQuotes(matcher.group()));
            }
            switch (params.get(0)) {
                case "/append" -> Route.appendStation(params);
                case "/add-head" -> Route.addHeadStation(params);
                case "/remove" -> Station.removeStation(params.get(1), params.get(2));
                case "/output" -> outputLine(params.get(1));
                case "/connect" -> {
                    // connect A to B and vice versa
                    Station.connectStations(params.get(1), params.get(2), params.get(3), params.get(4));
                    Station.connectStations(params.get(3), params.get(4), params.get(1), params.get(2));
                }
                case "/route" -> getRoute("SHORTEST", params.get(1), params.get(2), params.get(3), params.get(4));
                case "/fastest-route" ->
                        getRoute("FASTEST", params.get(1), params.get(2), params.get(3), params.get(4));
                case "/exit" -> {
                    return;
                }
                default -> System.out.println("Invalid command");
            }
        } while (true);
    }

    private static void getRoute(String method, String fromRoute, String fromStation, String toRoute, String toStation) {
        Route fromRt = Route.getRouteFromName(fromRoute);
        Station fromSt = Station.getStationFromName(fromRt, fromStation);
        Node fromNode = Node.getNodeFromRouteStation(fromRt, fromSt);

        Route toRt = Route.getRouteFromName(toRoute);
        Station toSt = Station.getStationFromName(toRt, toStation);
        Node toNode = Node.getNodeFromRouteStation(toRt, toSt);

        List<Node> nodeList = method.equals("FASTEST") ?
                Dijkstra.getFastestPath(fromNode, toNode) :
                getShortestPath(fromNode, toNode);

        //print the route

        String printBuffer = "";
        System.out.println(nodeList.get(0).getStation().getName());
        for (int i = 1; i < nodeList.size(); i++) {
            // are we switching to another route?
            if (!nodeList.get(i - 1).getRoute().equals(nodeList.get(i).getRoute())) {
                // is there a transfer from node(i-1) to the route of node(i)
                Route r = nodeList.get(i).getRoute();
                if (nodeList.get(i - 1).getStation().getTransfer().stream()
                        .anyMatch(e -> e.getRoute().equals(r))) {
                    System.out.println("Transition to line " + nodeList.get(i).getRoute().getName());
                    System.out.println(nodeList.get(i - 1).getStation().getName());
                    System.out.println(nodeList.get(i).getStation().getName());
                    printBuffer = "";
                } else {
                    System.out.println(nodeList.get(i).getStation().getName());
                    if (i < nodeList.size() - 1) {
                        printBuffer = "*** " + "Transition to line " + nodeList.get(i).getRoute().getName() + "\n" +
                                "*** " + nodeList.get(i).getStation().getName();
                    } else {
                        System.out.println("Transition to line " + nodeList.get(i).getRoute().getName());
                        System.out.println(nodeList.get(i).getStation().getName());
                    }
                }
            } else {
                if (!printBuffer.isEmpty()) {
                    System.out.println(printBuffer);
                    printBuffer = "";
                }
                System.out.println(nodeList.get(i).getStation().getName());
            }
        }

        if (method.equals("FASTEST")) {
            System.out.println("Total: " + toNode.getDistance() + " minutes in the way");
        }
    }


    private static String removeQuotes(String group) {
        if (group.startsWith("\"")) {
            group = group.substring(1);
        }
        if (group.endsWith("\"")) {
            group = group.substring(0, group.length() - 1);
        }
        return group;
    }

    private static void outputLine(String lineName) {
        Route route = Route.getRouteFromName(lineName);
        System.out.println("depot");
        for (Station station : route.getStationList()) {
            System.out.println(station.getName() + getTransfers(station));
        }
        System.out.println("depot");
    }

    private static String getTransfers(Station station) {
        StringBuilder sb = new StringBuilder();
        for (Transfer transfer : station.getTransfer()) {
            sb
                    .append(" - ")
                    .append(transfer.getStation().getName())
                    .append(" (")
                    .append(transfer.getRoute().getName())
                    .append(" line)");
        }
        return sb.toString();
    }

    private static List<Node> getShortestPath(Node start, Node end) {
        Map<Node, Node> parentMap = new HashMap<>(); // store the parent node of each visited node
        Queue<Node> queue = new LinkedList<>();
        queue.offer(start);
        parentMap.put(start, null);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            if (currentNode == end) {
                break; // shortest path found
            }
            for (Node neighbor : currentNode.getNeighbors().keySet()) {
                if (!parentMap.containsKey(neighbor)) {
                    parentMap.put(neighbor, currentNode);
                    queue.offer(neighbor);
                }
            }
        }

        List<Node> path = new ArrayList<>();
        Node current = end;
        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }
        Collections.reverse(path);
        return path;
    }
}