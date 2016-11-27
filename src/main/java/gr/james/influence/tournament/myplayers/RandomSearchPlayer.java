package gr.james.influence.tournament.myplayers;

import gr.james.influence.algorithms.generators.BarabasiAlbertGenerator;
import gr.james.influence.algorithms.iterators.GraphStateIterator;
import gr.james.influence.algorithms.iterators.RandomVertexIterator;
import gr.james.influence.algorithms.scoring.PageRank;
import gr.james.influence.api.Graph;
import gr.james.influence.game.*;
import gr.james.influence.game.players.MasterGreedyPlayer;
import gr.james.influence.graph.GraphUtils;
import gr.james.influence.tournament.PlayerDuel;
import gr.james.influence.tournament.players.AbstractSearchPlayer;
import gr.james.influence.util.RandomHelper;
import gr.james.influence.util.collections.GraphState;

public class RandomSearchPlayer extends AbstractSearchPlayer {
    private Graph g;
    private GameDefinition d;
    private MovePointer movePtr;
    private GraphState<Double> pr;

    public static void main(String[] args) {
        //Graph g = new WattsStrogatzGenerator(100, 14, 0.5).generate();
        Graph g = new BarabasiAlbertGenerator(50, 2, 2, 1.0).generate();
        Player p1 = new RandomSearchPlayer();
        //Player p2 = new DistanceGreedyPlayer();
        //Player p2 = new MasterBruteForcePlayer();
        Player p2 = new MasterGreedyPlayer();
        PlayerDuel.duel(g, 2, 5000, p1, p2, 1.0e-5);
    }

    public MovePointer getMovePtr() {
        return this.movePtr;
    }

    @Override
    protected void init(Graph g, GameDefinition d, MovePointer movePtr) {
        this.g = GraphUtils.deepCopy(g);
        this.d = d;
        this.movePtr = movePtr;
        this.pr = PageRank.execute(g, 0.15);
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
        double g = RandomHelper.getRandom().nextGaussian() * 0.1 + 1;
        if (g <= 0) {
            return gaussian();
        } else {
            return g;
        }
    }

    @Override
    protected Move mutateMove(Move m, Graph g) {
        //return Utils.mutateMove(m, g);

        Move m1 = new Move();
        RandomVertexIterator it = new RandomVertexIterator(g);
        while (m1.getVerticesCount() < m.getVerticesCount()) {
            m1.putVertex(it.next(), gaussian());
        }
        m1.normalizeWeights(m.getVerticesCount()); // Not needed, just for debugging
        return m1;

        /*Move m1 = m.deepCopy();
        int randomIndex = RandomHelper.getRandom().nextInt(m.getVerticesCount());
        Iterator<Vertex> it = m.iterator();
        for (int i = 0; i < randomIndex; i++) {
            it.next();
        }
        Vertex randomVertex = it.next();
        double randomWeight = m.getWeight(randomVertex);
        m1.removeVertex(randomVertex);
        Vertex newVertex = g.getRandomOutEdge(randomVertex, false);
        m1.putVertex(newVertex, randomWeight);

        if (m1.getVerticesCount() < m.getVerticesCount()) {
            return mutateMove(m, g);
        } else {
            return m1;
        }*/
    }

    @Override
    protected int compareMoves(Move m1, Move m2) {
        GameResult r = Game.runMoves(g, d, m1, m2);
        return -r.score;
    }
}
