package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.algorithms.distance.Dijkstra;
import gr.james.socialinfluence.algorithms.iterators.RandomVertexIterator;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePointer;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.util.collections.VertexPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComplementaryGreedyDistancePlayer extends Player {

    public static Move getRandomMove(Graph g, int num) {
        Move m = new Move();
        RandomVertexIterator rvi = new RandomVertexIterator(g);
        while (m.getVerticesCount() < num) {
            m.putVertex(rvi.next(), 1.0);
        }
        return m;
    }

    public double getVertexDistance(Graph g, Vertex v, List<Vertex> us, Map<VertexPair, Double> distanceMap) {
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
            log.error("totalDistance = 0");
        }

        return totalDistance;
    }

    @Override
    public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        Move m = new Move();

        /* It is imperative to our strategy to quickly select a move, even a random one. */
        m = getRandomMove(g, d.getActions());
        log.info("{}", m);
        movePtr.submit(m);

        /* Clear the move for future use. We could also re-instantiate the object with m = new Move(). */
        m.clear();

        /* Create the distance map. Each key is of the form {source, target} and the values are the distances. */
        Map<VertexPair, Double> distanceMap = Dijkstra.executeDistanceMap(g);

        /* Start by inserting a random vertex to the Move object. */
        m.putVertex(g.getRandomVertex(), 1.0);

        /* Gradually fill the Move object in a greedy way. */
        while (m.getVerticesCount() < d.getActions()) {
            /*
             * Select the vertex that creates the minimum sum of squares of
			 * distances from nodes that don't exist in the move.
			 */
            double min = Double.POSITIVE_INFINITY;
            Vertex lowest = null;
            for (Vertex v : g.getVertices()) {
                /* Candidates are all nodes in the graph except those in the move already. */
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

        log.info("{}", m);
        movePtr.submit(m);
    }

    /**
     * <p>This method returns a list that contains the Vertices inside a move object.</p>
     *
     * @param m the Move instance to convert to a list
     * @return the list view of the move
     */
    public List<Vertex> convertMoveToList(Move m) {
        List<Vertex> u = new ArrayList<>();
        for (Vertex e : m) {
            u.add(e);
        }
        return u;
    }
}
