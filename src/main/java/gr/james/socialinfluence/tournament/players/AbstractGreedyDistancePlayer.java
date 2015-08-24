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
import gr.james.socialinfluence.util.collections.Weighted;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractGreedyDistancePlayer extends Player {
    protected abstract double vertexHeuristic(Graph g,
                                              Vertex v,
                                              Collection<Vertex> us,
                                              Map<VertexPair, Double> distanceMap);

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
            Vertex best = g.getVertices().stream().filter(i -> !m.containsVertex(i))
                    .map(i -> new Weighted<>(i, vertexHeuristic(g, i, m.vertexSet(), distanceMap)))
                    .max(Weighted::compareTo).get().getObject();
            m.putVertex(best, 1.0);
        }

        log.info("{}", m);
        movePtr.submit(m);
    }
}
