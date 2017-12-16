package gr.james.influence.tournament.tournamentplayers;

import gr.james.influence.api.Graph;
import gr.james.influence.game.*;
import gr.james.influence.graph.GraphUtils;
import gr.james.influence.tournament.Utils;

public class ParallelWeightedRandomSearchPlayer extends Player {
    private static final int THREADS = 8;
    private static final double WAIT_FACTOR = 2.5;
    private static final double WAIT_MIN = 10.0;

    private Thread[] threads = new Thread[THREADS];
    private WeightedRandomSearchPlayer[] players = new WeightedRandomSearchPlayer[THREADS];

    @Override
    protected void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        Graph mg = GraphUtils.deepCopy(g);

        for (int i = 0; i < THREADS; i++) {
            WeightedRandomSearchPlayer tt = new WeightedRandomSearchPlayer();
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
            for (WeightedRandomSearchPlayer p : players) {
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
                throw Utils.convertCheckedException(e);
            }

            //log.info("wait: {}", wait);
        }

        for (WeightedRandomSearchPlayer p : players) {
            p.interrupt();
        }
    }
}
