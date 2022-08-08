import java.util.*;
public class Graph {

    private LinkedList<Edge>[] adjLists;
    private int vertexCount;
    private int length;


    @SuppressWarnings("unchecked")
    public Graph(int numVertices) {
        adjLists = (LinkedList<Edge>[]) new LinkedList[numVertices];
        for (int k = 0; k < numVertices; k++) {
            adjLists[k] = new LinkedList<Edge>();
        }
        vertexCount = numVertices;
    }


    public void addEdge(int v1, int v2, int edgeWeight) {
        if (!isAdjacent(v1, v2)) {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            v1Neighbors.add(new Edge(v1, v2, edgeWeight));
        } else {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            for (Edge e : v1Neighbors) {
                if (e.to() == v2) {
                    e.edgeWeight = edgeWeight;
                }
            }
        }
    }


    public void addUndirectedEdge(int v1, int v2, int edgeWeight) {
        addEdge(v1, v2, edgeWeight);
        addEdge(v2, v1, edgeWeight);
    }


    public boolean isAdjacent(int from, int to) {
        for (Edge e : adjLists[from]) {
            if (e.to() == to) {
                return true;
            }
        }
        return false;
    }


    public List<Integer> neighbors(int vertex) {
        ArrayList<Integer> neighbors = new ArrayList<>();
        for (Edge e : adjLists[vertex]) {
            neighbors.add(e.to());
        }
        return neighbors;
    }

    public int[] dijkstras(int startVertex) {
        int[] dist = new int[vertexCount];
        int[] back = new int[vertexCount];
        PriorityQueue<Integer> fringe = new PriorityQueue<Integer>(vertexCount, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (dist[o1] <= dist[o2]) {
                    return o1;
                }
                return o2;
            }
        });

        for (int k = 0; k < vertexCount; k++) {
            dist[k] = Integer.MAX_VALUE;
            fringe.add(k);
        }
        dist[startVertex]=0;

        while (!fringe.isEmpty()) {
            length = dist.length - fringe.size();
            int v = fringe.poll();
            for (int vertex : neighbors(v)) {
                if (dist[v] + getEdge(v, vertex).edgeWeight < dist[vertex]) {
                    dist[vertex] = dist[v] + getEdge(v, vertex).edgeWeight;
                    back[vertex] = v;
                    fringe.add(vertex);
                }
            }
        }
        return dist;
    }

    private Edge getEdge(int v1, int v2) {
        LinkedList<Edge> v1Neighbors = adjLists[v1];
        for (Edge e : v1Neighbors) {
            if (e.to() == v2) {
                return e;
            }
        }
        return null;
    }

    private class Edge {

        private int from;
        private int to;
        private int edgeWeight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.edgeWeight = weight;
        }

        public int to() {
            return to;
        }

        public int info() {
            return edgeWeight;
        }

        public String toString() {
            return "(" + from + "," + to + ",dist=" + edgeWeight + ")";
        }

    }


    public static void main(String[] args) {
        // Put some tests here!

        Graph g1 = new Graph(5);
        g1.addEdge(0, 1, 1);
        g1.addEdge(0, 2, 1);
        g1.addEdge(0, 4, 1);
        g1.addEdge(1, 2, 1);
        g1.addEdge(2, 0, 1);
        g1.addEdge(2, 3, 1);
        g1.addEdge(4, 3, 1);

        Graph g2 = new Graph(5);
        g2.addEdge(0, 1, 1);
        g2.addEdge(0, 2, 1);
        g2.addEdge(0, 4, 1);
        g2.addEdge(1, 2, 1);
        g2.addEdge(2, 3, 1);
        g2.addEdge(4, 3, 1);
    }
}
