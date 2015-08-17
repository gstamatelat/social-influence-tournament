package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.algorithms.distance.Dijkstra;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePointer;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.tournament.Utils;
import gr.james.socialinfluence.util.collections.VertexPair;

import java.util.Collection;
import java.util.Map;

public class GreedyDistancePlayer extends Player {
    /**
     * This method returns the product of distances from all vertices in 'us' to 'v'.
     */
    public static double getVertexDistance(Graph g, Vertex v, Collection<Vertex> us,
                                           Map<VertexPair, Double> distanceMap) {
        double totalDistance = us.stream().map(item -> distanceMap.get(new VertexPair(item, v)))
                .reduce((a, b) -> a * b).get();

        if (totalDistance == 0.0) {
            log.error("totalDistance = 0");
        }

        return totalDistance;
    }

    @Override
    public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        Move m;

        /* It is imperative to our strategy to quickly select a move, even a random one. */
        m = Utils.getRandomMove(g, d.getActions());
        log.info("Submitting random move {}", m);
        movePtr.submit(m);

        /* Clear the move for future use. We could also re-instantiate the object with m = new Move(). */
        m.clear();

        /* Create the distance map. Each key is of the form {source, target} and the values are the distances. */
        Map<VertexPair, Double> distanceMap = Dijkstra.executeDistanceMap(g);

        /* Start by inserting a random vertex to the Move object. */
        m.putVertex(g.getRandomVertex(), 1.0);

        /* Gradually fill the Move object in a greedy way. */
        while (m.getVerticesCount() < d.getActions()) {
            /* Check the distance that each vertex in the graph creates from the nodes that already exist in the move object and select the highest. */
            double max = .0;
            Vertex highest = null;
            for (Vertex v : g.getVertices()) {
                /* Candidates are all nodes in the graph except those in the move already. */
                if (!m.containsVertex(v)) {
                    double c = getVertexDistance(g, v, m.vertexSet(), distanceMap);
                    if (c > max) {
                        max = c;
                        highest = v;
                    }
                }
            }
            m.putVertex(highest, 1.0);
        }

        log.debug("{}", m);
        movePtr.submit(m);
    }
}
