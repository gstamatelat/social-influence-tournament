package gr.james.influence.tournament.players;

import gr.james.influence.algorithms.distance.Dijkstra;
import gr.james.influence.algorithms.generators.WattsStrogatzGenerator;
import gr.james.influence.algorithms.scoring.PageRank;
import gr.james.influence.api.Graph;
import gr.james.influence.game.*;
import gr.james.influence.graph.Vertex;
import gr.james.influence.util.Conditions;
import gr.james.influence.util.collections.GraphState;
import gr.james.influence.util.collections.VertexPair;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class HybridGreedyPlayer extends AbstractGreedyPlayer {
    private double lambda = 0.5;
    private Map<VertexPair, Double> distanceMap = null;
    private GraphState<Double> pr = null;

    public HybridGreedyPlayer(double lambda) {
        Conditions.requireArgument(lambda >= 0 && lambda <= 1, "lambda must be in [0,1]");
        this.lambda = lambda;
    }

    public static void main(String[] args) {
        //Graph g = new BarabasiAlbertGenerator(150, 2, 2, 1.0).generate(3724);
        Graph g = new WattsStrogatzGenerator(100, 8, 0.1).generate(3724);

        GameDefinition d = new GameDefinition(3, 3.0, 0, 0.0);

        Move otherMove = new Move(g.getVertexFromIndex(0), g.getVertexFromIndex(1), g.getVertexFromIndex(2));

        Map<Double, Double> scores = new TreeMap<>();

        for (double lambda = 0.0; lambda <= 1.0; lambda += 0.005) {
            HybridGreedyPlayer p = new HybridGreedyPlayer(lambda);
            Move hybridMove = p.getMove(g, d);
            GameResult r = Game.runMoves(g, d, otherMove, hybridMove);
            scores.put(lambda, r.fullState.getAverage());
        }

        for (Double e : scores.keySet()) {
            System.out.println(scores.get(e));
        }
    }

    @Override
    protected void init(Graph g, GameDefinition d, MovePointer movePtr) {
        distanceMap = Dijkstra.executeDistanceMap(g);
        pr = PageRank.execute(g, 0.15);
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

        double h1 = 1.0 / Math.pow(totalDistance, 1.0 / (g.getVerticesCount() - us.size() - 1));
        double h2 = pr.get(v);

        return lambda * h1 + (1 - lambda) * h2;
    }
}
