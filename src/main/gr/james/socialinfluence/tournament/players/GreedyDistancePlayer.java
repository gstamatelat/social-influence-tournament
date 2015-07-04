package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.collections.VertexPair;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePoint;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.FloydWarshall;
import gr.james.socialinfluence.graph.algorithms.iterators.RandomVertexIterator;
import gr.james.socialinfluence.helper.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GreedyDistancePlayer extends Player {

    public static Move getRandomMove(Graph g, int num) {
        Move m = new Move();
        RandomVertexIterator rvi = new RandomVertexIterator(g);
        while (m.getVerticesCount() < num) {
            m.putVertex(rvi.next(), 1.0);
        }
        return m;
    }

    public static double getVertexDistance(Graph g, Vertex v, List<Vertex> us,
                                           Map<VertexPair, Double> distanceMap) {
        /*
         * This method returns the product of distances from all vertices in
		 * 'us' to 'v'.
		 */
        double totalDistance = 1.0;

        for (Vertex u : us) {
            totalDistance *= distanceMap.get(new VertexPair(u, v));
        }

        if (totalDistance == 0.0) {
            Helper.logError("totalDistance = 0");
        }

        return totalDistance;
    }

    @Override
    public void getMove() {
        Move m = new Move();

        /*
         * It is imperative to our strategy to quickly select a move, even a
		 * random one.
		 */
        m = getRandomMove(g, this.d.getNumOfMoves());
        if (!this.d.getTournament()) {
            Helper.log(this.getClass().getSimpleName() + ": " + m.toString());
        }
        this.movePtr.set(m);

        /*
         * Clear the move for future use. We could also re-instantiate the
		 * object with m = new Move().
		 */
        m.clear();

        /*
         * Create the distance map. Each key is of the form {source, target} and
		 * the values are the distances.
		 */
        Map<VertexPair, Double> distanceMap = FloydWarshall.execute(g);

        /* Start by inserting a random vertex to the Move object. */
        m.putVertex(this.g.getRandomVertex(), 1.0);

        /* Gradually fill the Move object in a greedy way. */
        while (m.getVerticesCount() < this.d.getNumOfMoves()) {
            /*
             * Check the distance that each vertex in the graph creates from the
			 * nodes that already exist in the move object and select the
			 * highest.
			 */
            double max = .0;
            Vertex highest = null;
            for (Vertex v : this.g.getVertices()) {
                /*
                 * Candidates are all nodes in the graph except those in the
				 * move already.
				 */
                if (!m.containsVertex(v)) {
                    List<Vertex> moveList = convertMoveToList(m);
                    double c = getVertexDistance(g, v, moveList, distanceMap);
                    if (c > max) {
                        max = c;
                        highest = v;
                    }
                }
            }
            m.putVertex(highest, 1.0);
        }

        if (!this.d.getTournament()) {
            Helper.log(this.getClass().getSimpleName() + ": " + m.toString());
        }
        this.movePtr.set(m);
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