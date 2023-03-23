package metro;

import java.util.*;

public class Dijkstra {
     public static List<Node> getFastestPath(Node startNode, Node targetNode) {
        startNode.setDistance(0);
        while (true) {
            Node currentNode = nodeWithLowestScore();
            currentNode.setVisited();
            for (Map.Entry<Node, Integer> entry : currentNode.getNeighbors().entrySet()) {
                Node nextNode = entry.getKey();
                int distance = entry.getValue();
                if (nextNode.isVisited()) {
                    int newScore = currentNode.getDistance() + distance;
                    if (newScore < nextNode.getDistance()) {
                        nextNode.setDistance(newScore);
                        nextNode.setRouteToNode(currentNode);
                    }
                }
            }
            if (currentNode == targetNode) {
                return buildPath(targetNode);
            }
            if (nodeWithLowestScore().getDistance() == Integer.MAX_VALUE) {
                throw new RuntimeException("No path found!");
            }
        }
    }

    private static List<Node> buildPath(Node targetNode) {
        List<Node> route = new ArrayList<>();
        Node currentNode = targetNode;
        while (currentNode != null) {
            route.add(currentNode);
            currentNode = currentNode.getRouteToNode();
        }
        Collections.reverse(route);
        return route;
    }

    private static Node nodeWithLowestScore() {
        return Node.getAllInstances().stream()
                .filter(Node::isVisited)
                .min(Comparator.comparingInt(Node::getDistance))
                .orElse(null);
    }
}