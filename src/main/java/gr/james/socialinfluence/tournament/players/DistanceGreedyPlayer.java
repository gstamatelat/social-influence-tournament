package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.algorithms.distance.Dijkstra;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.util.Conditions;
import gr.james.socialinfluence.util.collections.VertexPair;

import java.util.Collection;
import java.util.Map;

public class DistanceGreedyPlayer extends AbstractGreedyPlayer {
    private Map<VertexPair, Double> distanceMap = null;

    @Override
    protected void init(Graph g, GameDefinition d) {
        distanceMap = Dijkstra.executeDistanceMap(g);
    }

    /**
     * This method returns the product of distances from all vertices in 'us' to 'v'.
     */
    @Override
    public double vertexHeuristic(Graph g, Vertex v, Collection<Vertex> us) {
        Conditions.requireArgument(!us.contains(v), "v must not be contained in us");

        if (us.isEmpty()) {
            return -1;
        }

        double totalDistance = us.stream().map(item -> distanceMap.get(new VertexPair(item, v)))
                .reduce((x, y) -> x * y).get();

        Conditions.assertion(totalDistance != 0.0);

        return totalDistance;
    }
}
