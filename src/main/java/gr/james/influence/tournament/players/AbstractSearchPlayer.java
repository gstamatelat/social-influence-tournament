package gr.james.influence.tournament.players;

import gr.james.influence.api.Graph;
import gr.james.influence.game.GameDefinition;
import gr.james.influence.game.Move;
import gr.james.influence.game.MovePointer;
import gr.james.influence.game.Player;
import gr.james.influence.tournament.Utils;

public abstract class AbstractSearchPlayer extends Player {
    /* Returns 0 if moves are equally good, 1 if m1 is better or -1 is m2 is better */
    protected abstract int compareMoves(Move m1, Move m2);

    protected void init(Graph g, GameDefinition d, MovePointer movePtr) {
        // By default this method does nothing, but you can overload it
    }

    protected Move mutateMove(Move m, Graph g) {
        // By default this method invokes Utils.mutateMove, but you can overload it
        return Utils.mutateMove(m, g);
    }

    @Override
    public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        /* Init */
        init(g, d, movePtr);

        /* Tries counter */
        int tries = 0;

        /* Submit a random move just for comparison with the next */
        movePtr.submit(Utils.getRandomMove(g, d.getActions()));

        /* Keep searching until time is up */
        while (!this.isInterrupted()) {
            /* Generate a new, mutated move */
            Move m = mutateMove(movePtr.recall(), g);

            /* If new move is better than the old one, submit it */
            if (compareMoves(m, movePtr.recall()) > 0) {
                log.debug("{}", m);
                movePtr.submit(m);
            }

            /* We have completed a try */
            tries++;
        }

        log.debug("{} tries", tries);
    }
}
