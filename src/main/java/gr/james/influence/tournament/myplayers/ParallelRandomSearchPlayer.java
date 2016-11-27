package gr.james.influence.tournament.myplayers;

import gr.james.influence.algorithms.generators.TwoWheelsGenerator;
import gr.james.influence.api.Graph;
import gr.james.influence.game.*;
import gr.james.influence.graph.GraphUtils;
import gr.james.influence.tournament.PlayerDuel;

public class ParallelRandomSearchPlayer extends Player {
    private static final int THREADS = 8;
    private static final double WAIT_FACTOR = 2.5;
    private static final double WAIT_MIN = 10.0;

    private Thread[] threads = new Thread[THREADS];
    private RandomSearchPlayer[] players = new RandomSearchPlayer[THREADS];

    public static void main(String[] args) {
        //Graph g = new WattsStrogatzGenerator(100, 14, 0.5).generate();
        //Graph g = new BarabasiAlbertGenerator(150, 2, 2, 1.0).generate();
        Graph g = new TwoWheelsGenerator(6).generate();

        Player p1 = new ParallelRandomSearchPlayer();
        //Player p1 = new RandomSearchPlayer();

        //Player p2 = new DistanceGreedyPlayer();
        //Player p2 = new MasterBruteForcePlayer();
        //Player p2 = new MasterGreedyPlayer();
        Player p2 = new RandomSearchPlayer();
        PlayerDuel.duel(g, 3, 0, p1, p2, 1.0e-5);
    }

    @Override
    protected void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        Graph mg = GraphUtils.deepCopy(g);

        for (int i = 0; i < THREADS; i++) {
            RandomSearchPlayer tt = new RandomSearchPlayer();
            players[i] = tt;
            threads[i] = new Thread(() -> {
                tt.suggestMove(GraphUtils.deepCopy(g), d, new MovePointer());
            });
        }

        for (Thread t : threads) {
            t.start();
        }

        double wait = (double) d.getExecution();

        Move m = new Move();
        while (!isInterrupted()) {
            for (RandomSearchPlayer p : players) {
                if (p.getMovePtr() != null && p.getMovePtr().recall().getVerticesCount() > 0) {
                    Move nMove = p.getMovePtr().recall();
                    //log.info("{}, {}", m, nMove);
                    GameResult r = Game.runMoves(mg, d, m, nMove);
                    if (r.score > 0) {
                        m = nMove.deepCopy();
                        movePtr.submit(m);
                        log.info("{}", m);
                    }
                }
            }

            try {
                Thread.sleep((long) (wait = Math.max(wait / WAIT_FACTOR, WAIT_MIN)));
            } catch (InterruptedException e) {
                throw gr.james.influence.util.Helper.convertCheckedException(e);
            }

            //log.info("wait: {}", wait);
        }

        for (RandomSearchPlayer p : players) {
            p.interrupt();
        }
    }
}
