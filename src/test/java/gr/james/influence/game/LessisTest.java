package gr.james.influence.game;

import gr.james.influence.algorithms.generators.basic.CycleGenerator;
import gr.james.influence.api.Graph;
import org.junit.Assert;
import org.junit.Test;

public class LessisTest {
    @Test
    public void lessisTest() {
        Graph g = new CycleGenerator(3).generate();

        Move m1 = new Move();
        m1.putVertex(g.getVertexFromIndex(0), 1.5);
        m1.putVertex(g.getVertexFromIndex(1), 1.5);

        Move m2 = new Move();
        m2.putVertex(g.getVertexFromIndex(0), 0.5);
        m2.putVertex(g.getVertexFromIndex(1), 0.5);
        m2.putVertex(g.getVertexFromIndex(2), 2.0);

        GameResult r = Game.runMoves(g, new GameDefinition(3, 3.0, 50000L, 0.0), m1, m2);
        Assert.assertEquals("lessisTest", 0, r.score);
    }
}
