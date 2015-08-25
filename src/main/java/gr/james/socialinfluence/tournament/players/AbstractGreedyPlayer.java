package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePointer;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.util.collections.Weighted;

import java.util.Collection;

public abstract class AbstractGreedyPlayer extends Player {
    protected abstract double vertexHeuristic(Graph g,
                                              Vertex v,
                                              Collection<Vertex> us);

    protected void init(Graph g, GameDefinition d, MovePointer movePtr) {
        // By default this method does nothing, but you can overload it
    }

    @Override
    public final void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        /* Init */
        init(g, d, movePtr);

        /* New move */
        Move m = new Move();

        /* Gradually fill the Move object in a greedy way */
        while (m.getVerticesCount() < d.getActions()) {
            Vertex best = g.getVertices().stream().filter(i -> !m.containsVertex(i))
                    .map(i -> new Weighted<>(i, vertexHeuristic(g, i, m.vertexSet())))
                    .max(Weighted::compareTo).get().getObject();
            m.putVertex(best, 1.0);
        }

        /* Finally submit the move */
        log.info("{}", m);
        movePtr.submit(m);
    }
}
