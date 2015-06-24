package gr.james.socialinfluence.tournament.student;

import gr.james.socialinfluence.collections.VertexPair;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePoint;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.Dijkstra;
import gr.james.socialinfluence.graph.algorithms.iterators.PageRankIterator;
import gr.james.socialinfluence.helper.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YalamasPro extends Player {
    public static double getVertexDistance(Graph g, Vertex v, List<Vertex> us,
                                           HashMap<VertexPair, Double> distanceMap) {
        /*
		 * This method returns the product of distances from all vertices NOT in
		 * 'us' to 'v'.
		 */
        double totalDistance = 1.0;

        for (Vertex u : g.getVertices()) {
            if (!us.contains(u)) {
                double d = distanceMap.get(new VertexPair(u, v));
                if (d != 0.0) {
					/*
					 * totalDistance += Math.pow(distanceMap.get(new
					 * VertexPair(u, v), 2.0));
					 */
                    totalDistance *= d;
                }
            }
        }

        if (totalDistance == 0.0) {
            Helper.logError("totalDistance = 0");
        }

        return totalDistance;
    }

    @Override
    public void getMove() {
        Move m = new Move();

        HashMap<VertexPair, Double> distanceMap = new HashMap<VertexPair, Double>();
        for (Vertex v : g.getVertices()) {
            HashMap<Vertex, Double> temp = Dijkstra.execute(g, v);
            for (Map.Entry<Vertex, Double> e : temp.entrySet()) {
                distanceMap.put(new VertexPair(v, e.getKey()), e.getValue());
            }
        }

        if (this.g.getMeta().startsWith("TwoWheels,")) {
            String[] arr = ((String) g.getMeta()).split("wheelVertices=");
            int n = Integer.parseInt(arr[arr.length - 1]);
            Vertex one = g.getVertexFromId(n);
            m.putVertex(one, 2.0);
            Vertex two = g.getVertexFromId(n * 2);
            m.putVertex(two, 2.0);


            while (m.getVerticesCount() < this.d.getNumOfMoves()) {
    			/*
    			 * Select the vertex that creates the minimum sum of squares of
    			 * distances from nodes that don't exist in the move.
    			 */
                double min = Double.POSITIVE_INFINITY;
                Vertex lowest = null;
                for (Vertex v : this.g.getVertices()) {
    				/*
    				 * Candidates are all nodes in the graph except those in the
    				 * move already.
    				 */
                    if (!m.containsVertex(v)) {
                        List<Vertex> moveList = convertMoveToList(m);
                        double c = getVertexDistance(g, v, moveList, distanceMap);
                        if (c < min) {
                            min = c;
                            lowest = v;
                        }
                    }
                }
                m.putVertex(lowest, 1.0);
            }

        }

        PageRankIterator pri = new PageRankIterator(this.g, 0.15);
        //double br = 1.5;
        int t = 1;
        while (m.getVerticesCount() < this.d.getNumOfMoves()) {
            if (t == 1) {
                m.putVertex(pri.next(), 2.0);
                //br = 1;
            } else if (t == 2) {
                m.putVertex(pri.next(), 1.5);
            } else {
                while (m.getVerticesCount() < this.d.getNumOfMoves()) {
        			/*
        			 * Select the vertex that creates the minimum sum of squares of
        			 * distances from nodes that don't exist in the move.
        			 */
                    double min = Double.POSITIVE_INFINITY;
                    Vertex lowest = null;
                    for (Vertex v : this.g.getVertices()) {
        				/*
        				 * Candidates are all nodes in the graph except those in the
        				 * move already.
        				 */
                        if (!m.containsVertex(v)) {
                            List<Vertex> moveList = convertMoveToList(m);
                            double c = getVertexDistance(g, v, moveList, distanceMap);
                            if (c < min) {
                                min = c;
                                lowest = v;
                            }
                        }
                    }
                    m.putVertex(lowest, 1.0);
                }
            }
            t = t + 1;
        }
        this.movePtr.set(m);
        if (!this.d.getTournament()) {
            Helper.log("YalamasPro: " + m);
        }
    }

    public List<Vertex> convertMoveToList(Move m) {
		/*
		 * This method returns an array that contains the Vertices inside a move
		 * object.
		 */
        List<Vertex> u = new ArrayList<Vertex>();
        for (MovePoint e : m) {
            u.add(e.vertex);
        }
        return u;
    }

}