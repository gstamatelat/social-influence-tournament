package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.util.collections.VertexPair;

import java.util.Map;

public final class DistanceLocalSearchPlayer extends AbstractLocalSearchPlayer {
    /**
     * Calculates the product of the distances of every pair of vertices in the move (aka geometric mean).
     */
    @Override
    public double evaluateMove(Move m, Map<VertexPair, Double> distanceMap) {
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
}
