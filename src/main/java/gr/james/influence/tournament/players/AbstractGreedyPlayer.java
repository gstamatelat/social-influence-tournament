package gr.james.influence.tournament.players;

import gr.james.influence.api.Graph;
import gr.james.influence.game.GameDefinition;
import gr.james.influence.game.Move;
import gr.james.influence.game.MovePointer;
import gr.james.influence.game.Player;
import gr.james.influence.graph.Vertex;
import gr.james.influence.util.collections.Weighted;

import java.util.Collection;

public abstract class AbstractGreedyPlayer extends Player {
    protected abstract double evaluateVertex(Graph g,
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
                    .map(i -> new Weighted<>(i, evaluateVertex(g, i, m.vertexSet())))
                    .max(Weighted::compareTo).get().getObject();
            m.putVertex(best, 1.0);
        }

        /* Finally submit the move */
        log.info("{}", m);
        movePtr.submit(m);
    }
}
