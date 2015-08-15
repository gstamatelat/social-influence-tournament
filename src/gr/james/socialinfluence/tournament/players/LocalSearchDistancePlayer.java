package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.algorithms.distance.Dijkstra;
import gr.james.socialinfluence.algorithms.iterators.RandomSurferIterator;
import gr.james.socialinfluence.algorithms.iterators.RandomVertexIterator;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePointer;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.util.RandomHelper;
import gr.james.socialinfluence.util.collections.VertexPair;

import java.util.Map;

public class LocalSearchDistancePlayer extends Player {

    public static Move mutateMove(Move m, Graph g) {
        /* Slightly change the vertices in the move. */
        double jump_probability = 0.2;

        Move moves = new Move();

        for (Vertex v : m) {
            RandomSurferIterator randomSurfer = new RandomSurferIterator(g, 0.0, v);
            while (RandomHelper.getRandom().nextDouble() < jump_probability) {
                v = randomSurfer.next();
            }

            moves.putVertex(v, 1.0);
        }

        if (moves.getVerticesCount() < m.getVerticesCount()) {
            return mutateMove(m, g);
        } else {
            return moves;
        }
    }

    public static Move getRandomMove(Graph g, int num) {
        /* Return a random move. */
        Move m = new Move();
        RandomVertexIterator rvi = new RandomVertexIterator(g);
        while (m.getVerticesCount() < num) {
            m.putVertex(rvi.next(), 1.0);
        }
        return m;
    }

    /**
     * Calculates the product of the distances of every pair of vertices in the move (aka geometric mean).
     */
    public static double getMoveDistance(Move m, Map<VertexPair, Double> distanceMap) {
        double distance = 0.0;
        for (Vertex x : m) {
            for (Vertex y : m) {
                if (!x.equals(y)) {
                    distance += Math.log(distanceMap.get(new VertexPair(x, y)));
                }
            }
        }
        return distance;
    }

    @Override
    public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        Move m = new Move();

        /* First, we select an initial move. Afterwards, we will start tweaking it. */
        m = getRandomMove(g, d.getActions());
        log.info("{}", m);
        movePtr.submit(m);

        /* Create the distance map. Each key is of the form {source, target} and the values are the distances. */
        Map<VertexPair, Double> distanceMap = Dijkstra.executeDistanceMap(g);

        double maxDistance = 0.0;
        /* Keep searching until time is up. */
        while (!this.isInterrupted()) {
            /* Generate a new, mutated move. */
            m = mutateMove(m, g);

            /* If the mutated move creates more distance, keep it. */
            double newDistance = getMoveDistance(m, distanceMap);
            if (newDistance > maxDistance) {
                maxDistance = newDistance;
                log.info("{}", m);
                movePtr.submit(m);
            }
        }
    }
}
