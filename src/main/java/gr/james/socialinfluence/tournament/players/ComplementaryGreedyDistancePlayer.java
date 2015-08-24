package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.util.Conditions;
import gr.james.socialinfluence.util.collections.VertexPair;

import java.util.Collection;
import java.util.Map;

public final class ComplementaryGreedyDistancePlayer extends AbstractGreedyDistancePlayer {
    /**
     * This method returns the reciprocal product of distances from all vertices NOT in 'us' to 'v'.
     */
    @Override
    public double vertexHeuristic(Graph g, Vertex v, Collection<Vertex> us, Map<VertexPair, Double> distanceMap) {
        Conditions.requireArgument(!us.contains(v), "v must not be contained in us");

        double totalDistance = g.getVertices().stream().filter(i -> !us.contains(i) && !v.equals(i))
                .map(i -> distanceMap.get(new VertexPair(i, v))).reduce((x, y) -> x * y).get();

        Conditions.assertion(totalDistance != 0.0);

        return 1.0 / totalDistance;
    }
}
