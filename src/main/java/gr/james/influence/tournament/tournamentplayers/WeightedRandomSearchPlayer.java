package gr.james.influence.tournament.tournamentplayers;

import gr.james.influence.algorithms.iterators.GraphStateIterator;
import gr.james.influence.algorithms.scoring.PageRank;
import gr.james.influence.api.Graph;
import gr.james.influence.game.*;
import gr.james.influence.graph.GraphUtils;
import gr.james.influence.graph.Vertex;
import gr.james.influence.tournament.players.AbstractSearchPlayer;
import gr.james.influence.util.Helper;
import gr.james.influence.util.RandomHelper;
import gr.james.influence.util.collections.GraphState;

import java.util.Set;

public class WeightedRandomSearchPlayer extends AbstractSearchPlayer {
    private static final double DAMPING_FACTOR = 0.15;
    private static final double STDEV_RANGE = 0.1;
    private static final double PR_WEIGHT = 1;

    private Graph g;
    private GameDefinition d;
    private MovePointer movePtr;
    private GraphState<Double> pr;

    public MovePointer getMovePtr() {
        return this.movePtr;
    }

    @Override
    protected void init(Graph g, GameDefinition d, MovePointer movePtr) {
        this.g = GraphUtils.deepCopy(g);
        this.d = d;
        this.movePtr = movePtr;
        this.pr = PageRank.execute(g, DAMPING_FACTOR);

        //this.pr.replaceAll((v, x) -> Math.log(x));
        //this.pr.replaceAll((v, x) -> Math.pow(x, 1 / PR_WEIGHT));
    }

    @Override
    protected Move initialMove(Graph g, GameDefinition d) {
        Move m = new Move();
        GraphStateIterator<Double> it = new GraphStateIterator<>(pr);
        while (m.getVerticesCount() < d.getActions()) {
            m.putVertex(it.next().getObject(), 1.0);
        }
        return m;
    }

    private double gaussian() {
        //double stdev = 1 - (Math.log(this.d.getActions()) / Math.log(this.g.getVerticesCount()));
        double stdev = 1;
        double g = RandomHelper.getRandom().nextGaussian() * STDEV_RANGE * stdev + 1;
        if (g <= 0) {
            return gaussian();
        } else {
            return g;
        }
    }

    @Override
    protected Move mutateMove(Move m, Graph g) {
        Set<Vertex> randomVertices = Helper.weightedRandom(pr, m.getVerticesCount());

        Move m1 = new Move();
        for (Vertex v : randomVertices) {
            m1.putVertex(v, gaussian());
        }

        m1.normalizeWeights(m.getVerticesCount()); // Not needed, just for debugging
        return m1;
    }

    @Override
    protected int compareMoves(Move m1, Move m2) {
        GameResult r = Game.runMoves(g, d, m1, m2);
        return -r.score;
    }
}
