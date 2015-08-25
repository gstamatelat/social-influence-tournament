package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePointer;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.tournament.Utils;

public abstract class AbstractLocalSearchPlayer extends Player {
    public abstract double evaluateMove(Move m);

    protected void init(Graph g, GameDefinition d, MovePointer movePtr) {
        // By default this method does nothing, but you can overload it
    }

    @Override
    public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        /* Init */
        init(g, d, movePtr);

        /* Move reference */
        Move m = Utils.getRandomMove(g, d.getActions());

        /* Keep the maximum score to update it each turn */
        double maxScore = Double.NEGATIVE_INFINITY;

        /* Keep searching until time is up */
        while (!this.isInterrupted()) {
            /* Generate a new, mutated move */
            m = Utils.mutateMove(m, g);

            /* If the mutated move creates more distance, keep it */
            double newScore = evaluateMove(m);
            if (newScore > maxScore) {
                maxScore = newScore;
                log.debug("{}", m);
                movePtr.submit(m);
            }
        }
    }
}
