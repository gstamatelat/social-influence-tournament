package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.algorithms.distance.Dijkstra;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.util.collections.VertexPair;

import java.util.Map;

public class DistanceLocalSearchPlayer extends AbstractLocalSearchPlayer {
    private Map<VertexPair, Double> distanceMap = null;

    @Override
    protected void init(Graph g, GameDefinition d) {
        distanceMap = Dijkstra.executeDistanceMap(g);
    }

    /**
     * Returns the product of the distances of every pair of vertices in the move (aka geometric mean).
     */
    @Override
    public double moveHeuristic(Move m) {
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
