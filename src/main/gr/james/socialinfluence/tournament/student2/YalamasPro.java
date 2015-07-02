package gr.james.socialinfluence.tournament.student2;

import gr.james.socialinfluence.collections.GraphState;
import gr.james.socialinfluence.collections.VertexPair;
import gr.james.socialinfluence.game.Game;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePoint;
import gr.james.socialinfluence.game.PlayerEnum;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.Dijkstra;
import gr.james.socialinfluence.graph.algorithms.PageRank;
import gr.james.socialinfluence.graph.algorithms.iterators.PageRankIterator;
import gr.james.socialinfluence.helper.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YalamasPro extends Player {
    // Vertex n = this.g.getVertexFromIndex((int) (c + 0.5));
    // GraphState pr = PageRank.execute(g, .15);
    // double maxD = 0.;
    // Vertex maxV = null;
    /*
     * for (Vertex v: g.getVertices()) {
	 *
	 *
	 *
	 * }
	 *
	 * public static double getVertexDistance(Graph g, Vertex v, List<Vertex>
	 * us, Map<VertexPair, Double> distanceMap) { /* This method returns the
	 * product of distances from all vertices NOT in 'us' to 'v'.
	 */
    public static double getVertexDistance(Graph g, Vertex v, List<Vertex> us,
                                           Map<VertexPair, Double> distanceMap) {
        /*
         * This method returns the product of distances from all vertices NOT in
		 * 'us' to 'v'.
		 */
        double totalDistance = 1.0;

        for (Vertex u : g.getVertices()) {
            if (!us.contains(u)) {
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
        Move m = new Move();

        HashMap<VertexPair, Double> distanceMap = new HashMap<VertexPair, Double>();
        for (Vertex v : g.getVertices()) {
            HashMap<Vertex, Double> temp = Dijkstra.execute(g, v);
            for (Map.Entry<Vertex, Double> e : temp.entrySet()) {
                distanceMap.put(new VertexPair(v, e.getKey()), e.getValue());
            }
        }

        if (this.g.getMeta().startsWith("TwoWheels,")) {
            int maxx = g.getVerticesCount() - 1;
            Vertex center = g.getVertexFromIndex(maxx);

            if (this.d.getNumOfMoves() == 1) {

                m.putVertex(center, 1.0);
                this.movePtr.set(m);
                return;
            } else if (this.d.getNumOfMoves() == 2) {
                m.clear();
                for (Vertex v : g.getVertices()) {
                    if (g.getVerticesCount() == 7) {
                        Vertex right = g.getVertexFromIndex(0);
                        m.putVertex(right, 0.55);
                        m.putVertex(center, 1.45);
                    } else {
                        if ((v.getInDegree() == g.getVerticesCount() / 2)
                                && (!v.equals(center))) {
                            m.putVertex(v, 1.0);
                            // problem gt mporei na parei 2 apo tin idia pleura
                            // gia
                            // 7 vertices
                        }
                    }
                }
                this.movePtr.set(m);
                return;
            } else if (this.d.getNumOfMoves() == 3) {
                m.clear();
                if (g.getVerticesCount() == 9) {
                    Vertex r = g.getVertexFromIndex(1);
                    Vertex l = g.getVertexFromIndex(5);
                    m.putVertex(r, 5.0);
                    m.putVertex(l, 5.0);
                    m.putVertex(center, 8.0);
                } else {
                    double bribe = maxx / 2 + 1;
                    m.putVertex(g.getVertexFromIndex(maxx - 1), bribe);
                    m.putVertex(
                            g.getVertexFromIndex(maxx
                                    - (g.getVerticesCount() / 2) - 1), bribe);
                    m.putVertex(center, 5.0);
                }
				/*
				 * for (Vertex v : g.getVertices()) { if (g.getVerticesCount()
				 * == 7) { if (v.getInDegree() == 6) { m.putVertex(v, 1.3); }
				 * else { if ((m.getVerticesCount() == 0) ||
				 * (m.getVerticesCount() == 2)) { m.putVertex(v, 0.835); } } //
				 * return; if ((v.getInDegree() == g.getVerticesCount() / 2) ||
				 * (v.getInDegree() == 6)) { if (!v.equals(center)) {
				 * m.putVertex(v, 7.0); } else { m.putVertex(v, 5.0); } }
				 *
				 * }
				 */
                this.movePtr.set(m);
                if (!this.d.getTournament()) {
                    Helper.log("YalamasPro: " + m);
                }
                return;

            } else if (this.d.getNumOfMoves() == 4) {
                // todo
                m.clear();
                if (g.getVerticesCount() == 9) {
                    Vertex up_r = g.getVertexFromIndex(0);
                    Vertex down_r = g.getVertexFromIndex(2);
                    Vertex up_l = g.getVertexFromIndex(6);
                    Vertex down_l = g.getVertexFromIndex(4);
                    m.putVertex(up_r, 1.0);
                    m.putVertex(down_r, 1.0);
                    m.putVertex(up_l, 1.0);
                    m.putVertex(down_l, 1.0);
                } else {
                    Move m1 = weightedPageRank();
                    // this.movePtr.set(m1);

                    Move m2 = myCompl(distanceMap);
                    // this.movePtr.set(m1);

                    Game game = new Game(this.g);
                    game.setPlayer(PlayerEnum.A, m1);
                    game.setPlayer(PlayerEnum.B, m2);
                    int gameScore = game.runGame(this.d).score;
                    if (gameScore < 0) {
                        this.movePtr.set(m1);
                    } else {
                        this.movePtr.set(m2);
                    }
                }
                return;
            } else {
                // todo

                Move m1 = weightedPageRank();
                // this.movePtr.set(m1);

                Move m2 = myCompl(distanceMap);
                // this.movePtr.set(m1);

                Game game = new Game(this.g);
                game.setPlayer(PlayerEnum.A, m1);
                game.setPlayer(PlayerEnum.B, m2);
                int gameScore = game.runGame(this.d).score;
                if (gameScore < 0) {
                    this.movePtr.set(m1);
                } else {
                    this.movePtr.set(m2);
                }
            }

		/*		m.clear();
				for (Vertex v : g.getVertices()) {
					if ((v.getInDegree() == g.getVerticesCount() / 2)
							|| (v.getInDegree() == 6)) {
						if (!v.equals(center)) {
							m.putVertex(v, 7.0);
						} else {
							m.putVertex(v, 5.0);
						}
					}
				}


				 * String[] arr = ((String)
				 * g.getMeta()).split("wheelVertices="); int n =
				 * Integer.parseInt(arr[arr.length - 1]); Vertex one =
				 * g.getVertexFromId(n); m.putVertex(one, 2.0); Vertex two =
				 * g.getVertexFromId(n * 2); m.putVertex(two, 2.0);


				while (m.getVerticesCount() < this.d.getNumOfMoves()) {

					 * Select the vertex that creates the minimum sum of squares
					 * of distances from nodes that don't exist in the move.

					double min = Double.POSITIVE_INFINITY;
					Vertex lowest = null;
					for (Vertex v : this.g.getVertices()) {

						 * Candidates are all nodes in the graph except those in
						 * the move already.

						if (!m.containsVertex(v)) {
							List<Vertex> moveList = convertMoveToList(m);
							double c = getVertexDistance(g, v, moveList,
									distanceMap);
							if (c < min) {
								min = c;
								lowest = v;
							}
						}
					}
					m.putVertex(lowest, 2.5);
				}
			}
			this.movePtr.set(m);*/
            return;
            // end of 2 wheels player
        } else if (this.g.getMeta().startsWith("BarabasiAlbert,")) {
            Move m1 = weightedPageRank();
            // this.movePtr.set(m1);

            Move m2 = myCompl(distanceMap);
            // this.movePtr.set(m1);

            Game game = new Game(this.g);
            game.setPlayer(PlayerEnum.A, m1);
            game.setPlayer(PlayerEnum.B, m2);
            int gameScore = game.runGame(this.d).score;
            if (gameScore < 0) {
                this.movePtr.set(m1);
            } else {
                this.movePtr.set(m2);
            }

			/* The case where we have the Barabasi Albert Cluster */
        } else if (this.g.getMeta().startsWith("BarabasiAlbertCluster,")) {

            if (!this.d.getTournament()) {
                Helper.log("YalamasPro: " + m);
            }
            String kapa = this.g.toString();
            String[] parts = kapa.split(",");
            int p = parts.length;
            String wanted = parts[p - 1];
            String wanted1 = parts[2];
            String[] parts1 = wanted.split("=");
            String[] parts2 = wanted1.split("=");
            String str = parts1[1];
            String str1 = parts2[1];
            str = str.replace("}", "");
            int cluster = Integer.parseInt(str);
            int nrVert = Integer.parseInt(str1);
            double c = 0;
            int period = 0;
            int a = 0;
            int roof = 0;
            GraphState pr = PageRank.execute(g, .15);
			/*
			 * if (this.d.getNumOfMoves() > cluster) { roof = cluster; period =
			 * 1; } else {
			 */
            if (this.d.getNumOfMoves() < cluster) {
                roof = this.d.getNumOfMoves();
                period = cluster / this.d.getNumOfMoves();
                // }

                while (a < roof) {
                    int selected = (a * period) + 1;
                    int minLimit = (selected - 1) * nrVert;
                    int maxUp = selected * nrVert - 1;
                    double maxD = 0.;
                    Vertex maxV = null;
                    for (int i = minLimit; i < maxUp; i++) {
                        Vertex n = this.g.getVertexFromIndex(i);
                        if (pr.get(n) > maxD) {
                            maxD = pr.get(n);
                            maxV = n;
                        }
                    }
                    m.putVertex(maxV, maxD);
                    a++;
                }
				/*
				 * if (this.d.getNumOfMoves() > cluster) { PageRankIterator pri
				 * = new PageRankIterator(this.g, 0.15); while
				 * (m.getVerticesCount() < this.d.getNumOfMoves()) { Vertex
				 * current = pri.next(); if (!m.containsVertex(current)) {
				 * m.putVertex(current, current.getInDegree()); } } }
				 */
                this.movePtr.set(m);
            } else {
                Move m1 = weightedPageRank();
                // this.movePtr.set(m1);

                Move m2 = myCompl(distanceMap);
                // this.movePtr.set(m1);

                Game game = new Game(this.g);
                game.setPlayer(PlayerEnum.A, m1);
                game.setPlayer(PlayerEnum.B, m2);
                int gameScore = game.runGame(this.d).score;
                if (gameScore < 0) {
                    this.movePtr.set(m1);
                } else {
                    this.movePtr.set(m2);
                }
            }


        } else {
            //Unknown Graphs
            Move m1 = weightedPageRank();

            Move m2 = myCompl(distanceMap);

            Game game = new Game(this.g);
            game.setPlayer(PlayerEnum.A, m1);
            game.setPlayer(PlayerEnum.B, m2);
            int gameScore = game.runGame(this.d).score;
            if (gameScore < 0) {
                this.movePtr.set(m1);
            } else {
                this.movePtr.set(m2);
            }
        }
    }

    public List<Vertex> convertMoveToList(Move m) {
		/*
		 * This method returns an array that contains the Vertices inside a move
		 * object.
		 */
        List<Vertex> u = new ArrayList<Vertex>();
        for (MovePoint e : m) {
            u.add(e.vertex);
        }
        return u;
    }

    public Move weightedPageRank() {
        Move m = new Move();
        PageRankIterator pri = new PageRankIterator(this.g, 0.15);
        while (m.getVerticesCount() < this.d.getNumOfMoves()) {
            Vertex current = pri.next();
            m.putVertex(current, current.getInDegree());
        }
        return m;
    }

    public Move myCompl(Map<VertexPair, Double> distanceMap) {
        Move m = new Move();
        m.putVertex(new PageRankIterator(g, 0.15).next(), 1.0);

		/* Gradually fill the Move object in a greedy way. */
        while (m.getVerticesCount() < this.d.getNumOfMoves()) {
			/*
			 * Select the vertex that creates the minimum sum of squares of
			 * distances from nodes that don't exist in the move.
			 */
            double min = Double.POSITIVE_INFINITY;
            Vertex lowest = null;
            for (Vertex v : this.g.getVertices()) {
				/*
				 * Candidates are all nodes in the graph except those in the
				 * move already.
				 */
                if (!m.containsVertex(v)) {
                    List<Vertex> moveList = convertMoveToList(m);
                    double c = getVertexDistance(g, v, moveList, distanceMap);
                    if (c < min) {
                        min = c;
                        lowest = v;
                    }
                }
            }
            m.putVertex(lowest, 1.0);
        }
        return m;
    }
}