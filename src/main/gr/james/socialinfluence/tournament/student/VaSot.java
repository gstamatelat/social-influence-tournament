package gr.james.socialinfluence.tournament.student;

import gr.james.socialinfluence.collections.GraphState;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.PageRank;
import gr.james.socialinfluence.graph.algorithms.iterators.PageRankIterator;

public class VaSot extends Player {

    @Override
    public void getMove() {

		/*First move, using pagerank */
        Move m = new Move();

        PageRankIterator pgi = new PageRankIterator(g, 0.15);

        for (int i = 0; i < d.getNumOfMoves(); i++)
            m.putVertex(pgi.next(), 1);

        this.movePtr.set(m);

		/*More complex move*/
        GraphState gs = PageRank.execute(this.g, 0.15);
        PageRankIterator pgi2 = new PageRankIterator(g, 0.15);

		/*Arrays used to calculate optimum weights assigned to vertices*/
        int nom = d.getNumOfMoves();
        Vertex[] possible = new Vertex[nom];
        double[] denominator = new double[nom];
        double[] inffact = new double[nom];
        double[] bribes = new double[nom];

		/*Initialize arrays*/
        for (int j = 0; j < nom; j++) {
            possible[j] = pgi2.next();
            denominator[j] = possible[j].getOutWeightSum();
            inffact[j] = gs.get(possible[j]) / g.getVerticesCount();  //Normalize influence factor of pagerank
            bribes[j] = 0.0;
        }

		/*Determine which vertex should get each penny of the bribe*/
        for (int j = 0; j < nom * 100; j++) {
            double infl_incr = 0.0;
            int infl_incr_ind = 0;
            for (int z = 0; z < nom; z++) {
                double temp_infl_incr = inffact[z] * (bribes[z] + 0.01) / (denominator[z] + bribes[z] + 0.01);  //Influence if a penny used
                double diff = temp_infl_incr - inffact[z] * (bribes[z]) / (denominator[z] + bribes[z]);  //Difference of influence
                if (diff > infl_incr) {
                    infl_incr = diff;
                    infl_incr_ind = z;
                }
            }
            bribes[infl_incr_ind] += 0.01; //Bribe vertex with a penny
        }

        Move mb = new Move();

        for (int j = 0; j < nom; j++) {
            if (bribes[j] > 0.0)
                mb.putVertex(possible[j], bribes[j]);
        }

        this.movePtr.set(mb);

    }

}