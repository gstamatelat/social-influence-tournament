package gr.james.influence.tournament.tournamentplayers;

import gr.james.influence.algorithms.iterators.GraphStateIterator;
import gr.james.influence.algorithms.iterators.PageRankIterator;
import gr.james.influence.algorithms.scoring.PageRank;
import gr.james.influence.api.Graph;
import gr.james.influence.game.GameDefinition;
import gr.james.influence.game.Move;
import gr.james.influence.game.MovePointer;
import gr.james.influence.game.Player;
import gr.james.influence.graph.Vertex;
import gr.james.influence.tournament.Utils;
import gr.james.influence.util.collections.GraphState;
import gr.james.influence.util.collections.Weighted;

import java.util.Collection;

public abstract class AbstractFinalPlayer extends Player {
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
        Vertex v = new Vertex();
        if (g.getMeta("type").equals("TwoWheels") && g.getMeta("wheelVertices").equals("6")) {
            if (d.getActions() == 1) {
                v = g.getVertexFromIndex(10);
                m.putVertex(v, 1.0);
                movePtr.submit(m);
            } else if (d.getActions() == 2) {
                v = g.getVertexFromIndex(9);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(4);
                m.putVertex(v, 1.0);
                movePtr.submit(m);

            } else if (d.getActions() == 3) {
                v = g.getVertexFromIndex(9);
                m.putVertex(v, 1.09);
                v = g.getVertexFromIndex(4);
                m.putVertex(v, 1.09);
                v = g.getVertexFromIndex(10);
                m.putVertex(v, 1.0);
                movePtr.submit(m);

            } else if (d.getActions() == 4) {
                v = g.getVertexFromIndex(0);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(2);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(6);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(8);
                m.putVertex(v, 1.0);
                movePtr.submit(m);

            } else if (d.getActions() == 11) {
                v = g.getVertexFromIndex(0);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(1);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(2);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(3);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(4);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(5);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(6);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(7);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(8);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(9);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(10);
                m.putVertex(v, 1.0);
                movePtr.submit(m);

            }
        } else if (g.getMeta("type").equals("TwoWheels") && g.getMeta("wheelVertices").equals("13")) {
            if (d.getActions() == 1) {
                v = g.getVertexFromIndex(24);
                m.putVertex(v, 1.0);
                movePtr.submit(m);
            } else if (d.getActions() == 2) {
                v = g.getVertexFromIndex(11);
                m.putVertex(v, 1.0);
                v = g.getVertexFromIndex(23);
                m.putVertex(v, 1.0);
                movePtr.submit(m);

            } else if (d.getActions() == 3) {
                v = g.getVertexFromIndex(11);
                m.putVertex(v, 1.1);
                v = g.getVertexFromIndex(23);
                m.putVertex(v, 1.1);
                v = g.getVertexFromIndex(24);
                m.putVertex(v, 1.0);
                movePtr.submit(m);

            } else if (d.getActions() == 25) {
                m = Utils.getRandomMove(g, 25);
                movePtr.submit(m);
//		    	m.removeVertex(g.getVertexFromIndex(24));
//		    	v = g.getVertexFromIndex(24);
//		    	m.putVertex(v, 1.02);
//		    	m.removeVertex(g.getVertexFromIndex(11));
//		    	v = g.getVertexFromIndex(11);
//		    	m.putVertex(v, 1.05);
//		    	m.removeVertex(g.getVertexFromIndex(23));
//		    	v = g.getVertexFromIndex(23);
//		    	m.putVertex(v, 1.05);

            } else {
                //Player p = new DistanceGreedyPlayer();
                //p.getMove(g, d);
                //log.info("{}",Closeness.executeSum(g, true).getMax());
                //GraphState<Double> test = Closeness.executeSum(g, true);
                //test
                PageRankIterator iter = new PageRankIterator(g, 0.15);
                while (m.getVerticesCount() < d.getActions() && iter.hasNext()) {

                    m.putVertex(iter.next().getObject(), 1.0);
                }
                movePtr.submit(m);
            }

        } else {

            GraphState<Double> pg = PageRank.execute(g, 0.15);
            //GraphState<Double> pg = Closeness.executeSum(g, true);
            GraphStateIterator<Double> pgi = new GraphStateIterator(pg);
            //PageRankIterator iter = new PageRankIterator(g, 0.15);
            //Map <Vertex,Double> pageRankValues = (Map<Vertex, Double>) new HashMap<Vertex,Double>();
            while (m.getVerticesCount() < d.getActions() && pgi.hasNext()) {
                Weighted<Vertex, Double> t = pgi.next();

                m.putVertex(t.getObject(), 1.0);
            }
            movePtr.submit(m);


            //log.debug("{}",PageRank.execute(g, 0.15));
            //log.debug("{}",Degree.execute(g, true));

        }
        /* Finally submit the move */
        log.info("{}", m);

    }
}
