package gr.james.influence.tournament.tournamentplayers;

import gr.james.influence.algorithms.distance.Dijkstra;
import gr.james.influence.api.Graph;
import gr.james.influence.game.GameDefinition;
import gr.james.influence.game.MovePointer;
import gr.james.influence.graph.Vertex;
import gr.james.influence.util.Conditions;
import gr.james.influence.util.collections.VertexPair;

import java.util.Collection;
import java.util.Map;

public class FinalPlayer extends AbstractFinalPlayer {
    private Map<VertexPair, Double> distanceMap = null;

    @Override
    protected void init(Graph g, GameDefinition d, MovePointer movePtr) {
        distanceMap = Dijkstra.executeDistanceMap(g);
    }

    /**
     * This method returns the reciprocal product of distances from all vertices NOT in 'us' to 'v'.
     */
    @Override
    public double evaluateVertex(Graph g, Vertex v, Collection<Vertex> us) {
        Conditions.requireArgument(!us.contains(v), "v must not be contained in us");

        double totalDistance = g.getVertices().stream().filter(i -> !us.contains(i) && !v.equals(i))
                .map(i -> distanceMap.get(new VertexPair(i, v))).reduce((x, y) -> x * y).get();

        Conditions.assertion(totalDistance != 0.0);

        return 1.0 / totalDistance;
    }
}
