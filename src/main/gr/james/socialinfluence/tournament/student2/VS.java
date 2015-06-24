package gr.james.socialinfluence.tournament.student2;

import gr.james.socialinfluence.collections.GraphState;
import gr.james.socialinfluence.collections.VertexPair;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.FloydWarshall;
import gr.james.socialinfluence.graph.algorithms.PageRank;
import gr.james.socialinfluence.graph.algorithms.iterators.PageRankIterator;
import gr.james.socialinfluence.helper.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VS extends Player {

    public static double getVertexDistance(Graph g, Vertex v, List<Vertex> us,
                                           Map<VertexPair, Double> distanceMap) {
        /*
		 * This method returns the product of distances from all vertices NOT in
		 * 'us' to 'v'.
		 */
        double totalDistance = 1.0;

        for (Vertex u : g.getVertices()) {
            if (us.contains(u)) {
                double d = distanceMap.get(new VertexPair(u, v));
                if (d != 0.0) {
					/*
					 * totalDistance += Math.pow(distanceMap.get(new
					 * VertexPair(u, v), 2.0));
					 */
                    totalDistance *= d;
                }
            }
        }

        if (totalDistance == 0.0) {
            Helper.logError("totalDistance = 0");
        }

        return totalDistance;
    }

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
        Map<VertexPair, Double> distanceMap = FloydWarshall.execute(g);

        Move mb = new Move();

        int nom = d.getNumOfMoves();
        Vertex[] possible = new Vertex[nom];
        double[] denominator = new double[nom];
        double[] inffact = new double[nom];
        double[] bribes = new double[nom];

        if (this.g.getMeta().contains("TwoWheels")) {
            if (nom == 1) {
                pgi2.next();
                pgi2.next();
                possible[0] = pgi2.next();
                denominator[0] = possible[0].getOutWeightSum();
                inffact[0] = gs.get(possible[0]) / g.getVerticesCount();  //Normalize influence factor of pagerank
                bribes[0] = 0.0;
            } else {
                for (int j = 0; j < nom && j < 3; j++) {
                    possible[j] = pgi2.next();
                    denominator[j] = possible[j].getOutWeightSum();
                    inffact[j] = gs.get(possible[j]) / g.getVerticesCount();  //Normalize influence factor of pagerank
                    bribes[j] = 0.0;
                }
                List<Vertex> chosen_list = new ArrayList<>();
                if (nom > 3)
                    chosen_list = convertArrayToList(possible, 3);
                List<Vertex> left = new ArrayList<Vertex>();
                while (pgi2.hasNext()) {
                    left.add(pgi2.next());
                }
                for (int j = 3; j < nom; j++) {
                    Vertex chosen = left.get(0);
                    double max_dist = -1;
                    for (Vertex v : left) {
                        double tmp = getVertexDistance(this.g, v, chosen_list, distanceMap);
                        double tolerance = 1;
                        if (tmp > max_dist && gs.get(v) > gs.get(chosen) * tolerance) {
                            max_dist = tmp;
                            chosen = v;
                        }
                    }
                    left.remove(chosen);
                    chosen_list.add(chosen);
                    possible[j] = chosen;
                    denominator[j] = possible[j].getOutWeightSum();
                    inffact[j] = gs.get(possible[j]) / g.getVerticesCount();  //Normalize influence factor of pagerank
                    bribes[j] = 0.0;
                }
            }

        } else {
            possible[0] = pgi2.next();
            denominator[0] = possible[0].getOutWeightSum();
            inffact[0] = gs.get(possible[0]) / g.getVerticesCount();
            bribes[0] = 0.0;

            List<Vertex> chosen_list = new ArrayList<>();
            chosen_list.add(possible[0]);
            List<Vertex> left = new ArrayList<Vertex>();
            while (pgi2.hasNext()) {
                left.add(pgi2.next());
            }

            for (int j = 1; j < nom; j++) {
                Vertex chosen = left.get(0);
                double max_dist = -1;
                for (Vertex v : left) {
                    double tmp = getVertexDistance(this.g, v, chosen_list, distanceMap);
                    double tolerance = 0.9;
                    if (tmp > max_dist && gs.get(v) > gs.get(chosen) * tolerance) {
                        max_dist = tmp;
                        chosen = v;
                    }
                }
                left.remove(chosen);
                chosen_list.add(chosen);
                possible[j] = chosen;
                denominator[j] = possible[j].getOutWeightSum();
                inffact[j] = gs.get(possible[j]) / g.getVerticesCount();  //Normalize influence factor of pagerank
                bribes[j] = 0.0;
            }
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

        //Move mb = new Move();

        for (int j = 0; j < nom; j++) {
            if (bribes[j] > 0.0)
                mb.putVertex(possible[j], bribes[j]);
        }

        this.movePtr.set(mb);

    }

    public List<Vertex> convertArrayToList(Vertex m[], int n) {
		/*
		 * This method returns an array that contains the Vertices inside a move
		 * object.
		 */
        List<Vertex> u = new ArrayList<Vertex>();
        for (int i = 0; i < n; i++) {
            u.add(m[i]);
        }
        return u;
    }


}