package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.algorithms.distance.Dijkstra;
import gr.james.socialinfluence.algorithms.iterators.RandomSurferIterator;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePointer;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.tournament.Utils;
import gr.james.socialinfluence.util.RandomHelper;
import gr.james.socialinfluence.util.collections.VertexPair;

import java.util.Map;

public abstract class AbstractLocalSearchPlayer extends Player {
    /* Slightly change the vertices in the move. */
    public static Move mutateMove(Move m, Graph g) {
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

    public abstract double evaluateMove(Move m, Map<VertexPair, Double> distanceMap);

    @Override
    public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        Move m;

        /* First, we select an initial move. Afterwards, we will start tweaking it. */
        m = Utils.getRandomMove(g, d.getActions());
        log.info("Submitting random move {}", m);
        movePtr.submit(m);

        /* Create the distance map. Each key is of the form {source, target} and the values are the distances. */
        Map<VertexPair, Double> distanceMap = Dijkstra.executeDistanceMap(g);

        double maxDistance = 0.0;
        /* Keep searching until time is up. */
        while (!this.isInterrupted()) {
            /* Generate a new, mutated move. */
            m = mutateMove(m, g);

            /* If the mutated move creates more distance, keep it. */
            double newDistance = evaluateMove(m, distanceMap);
            if (newDistance > maxDistance) {
                maxDistance = newDistance;
                log.debug("{}", m);
                movePtr.submit(m);
            }
        }
    }
}
