package gr.james.influence.tournament.players;

import gr.james.influence.api.Graph;
import gr.james.influence.game.GameDefinition;
import gr.james.influence.game.Move;
import gr.james.influence.game.MovePointer;
import gr.james.influence.game.Player;
import gr.james.influence.graph.Vertex;

public class AdvancedCyclePlayer extends Player {
    @Override
    public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        if (!g.getGraphType().equals("Cycle")) {
            log.warn("Graph {} is not a cycle. AdvancedCyclePlayer only works for cycles. Aborting now.", g);
            return;
        }

        Move m = new Move();

        final double period = (double) g.getVerticesCount() / d.getActions();
        double c = period;

        Vertex v = g.getRandomVertex();
        Vertex previous = v;

        m.putVertex(v, 1.0);

        while (m.getVerticesCount() < d.getActions()) {
            for (int j = 0; j < (int) (c + 0.5); j++) {
                Vertex[] verticesArray = g.getOutEdges(v).keySet().toArray(new Vertex[2]);
                if (verticesArray[0].equals(previous)) {
                    previous = v;
                    v = verticesArray[1];
                } else {
                    previous = v;
                    v = verticesArray[0];
                }
            }
            m.putVertex(v, 1.0);
            c += period - (int) (c + 0.5);
        }

        log.debug("{}", m);
        movePtr.submit(m);
    }
}
