package gr.james.influence.tournament.players;

import gr.james.influence.algorithms.distance.Dijkstra;
import gr.james.influence.api.Graph;
import gr.james.influence.game.GameDefinition;
import gr.james.influence.game.Move;
import gr.james.influence.game.MovePointer;
import gr.james.influence.graph.Vertex;
import gr.james.influence.util.collections.VertexPair;

import java.util.Map;

public class DistanceSearchPlayer extends AbstractSearchPlayer {
    private Map<VertexPair, Double> distanceMap = null;

    @Override
    protected void init(Graph g, GameDefinition d, MovePointer movePtr) {
        distanceMap = Dijkstra.executeDistanceMap(g);
    }

    /**
     * Returns the product of the distances of every pair of vertices in the move (aka geometric mean).
     */
    private double evaluateMove(Move m) {
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
    protected int compareMoves(Move m1, Move m2) {
        return Double.compare(evaluateMove(m1), evaluateMove(m2));
    }
}
