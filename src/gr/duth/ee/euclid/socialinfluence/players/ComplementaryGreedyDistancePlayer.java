package gr.duth.ee.euclid.socialinfluence.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePoint;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.Dijkstra;
import gr.james.socialinfluence.graph.algorithms.iterators.RandomVertexIterator;
import gr.james.socialinfluence.helper.Helper;

public class ComplementaryGreedyDistancePlayer extends
		gr.james.socialinfluence.game.players.Player {

	public static class VertexPair {
		public Vertex source;
		public Vertex target;

		public VertexPair(Vertex source, Vertex target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((source == null) ? 0 : source.hashCode());
			result = prime * result
					+ ((target == null) ? 0 : target.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VertexPair other = (VertexPair) obj;
			if (source == null) {
				if (other.source != null)
					return false;
			} else if (!source.equals(other.source))
				return false;
			if (target == null) {
				if (other.target != null)
					return false;
			} else if (!target.equals(other.target))
				return false;
			return true;
		}
	}

	@Override
	public void getMove() {
		Move m = new Move();

		/*
		 * It is imperative to our strategy to quickly select a move, even a
		 * random one.
		 */
		m = getRandomMove(g, this.d.getNumOfMoves());
		if (!this.d.getTournament()) {
			Helper.log(this.getClass().getSimpleName() + ": " + m.toString());
		}
		this.movePtr.set(m);

		/*
		 * Clear the move for future use. We could also re-instantiate the
		 * object with m = new Move().
		 */
		m.clear();

		/*
		 * Create the distance map. Each key is of the form {source, target} and
		 * the values are the distances.
		 */
		HashMap<VertexPair, Double> distanceMap = new HashMap<VertexPair, Double>();
		for (Vertex v : g.getVertices()) {
			HashMap<Vertex, Double> temp = Dijkstra.execute(g, v);
			for (Map.Entry<Vertex, Double> e : temp.entrySet()) {
				distanceMap.put(new VertexPair(v, e.getKey()), e.getValue());
			}
		}

		/* Start by inserting a random vertex to the Move object. */
		m.putVertex(this.g.getRandomVertex(), 1.0);

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

		if (!this.d.getTournament()) {
			Helper.log(this.getClass().getSimpleName() + ": " + m.toString());
		}
		this.movePtr.set(m);
	}

	public static Move getRandomMove(Graph g, int num) {
		Move m = new Move();
		RandomVertexIterator rvi = new RandomVertexIterator(g);
		while (m.getVerticesCount() < num) {
			m.putVertex(rvi.next(), 1.0);
		}
		return m;
	}

	public static double getVertexDistance(Graph g, Vertex v, List<Vertex> us,
			HashMap<VertexPair, Double> distanceMap) {
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

}