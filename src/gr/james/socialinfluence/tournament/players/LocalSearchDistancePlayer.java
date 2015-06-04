package gr.james.socialinfluence.tournament.players;

import java.util.HashMap;
import java.util.Map;

import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePoint;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.Dijkstra;
import gr.james.socialinfluence.graph.algorithms.iterators.RandomSurferIterator;
import gr.james.socialinfluence.graph.algorithms.iterators.RandomVertexIterator;
import gr.james.socialinfluence.helper.Finals;
import gr.james.socialinfluence.helper.Helper;

public class LocalSearchDistancePlayer extends
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
		long start = System.currentTimeMillis();

		/*
		 * First, we select an initial move. Afterwards, we will start tweaking
		 * it.
		 */
		m = getRandomMove(g, this.d.getNumOfMoves());
		if (!this.d.getTournament()) {
			Helper.log(this.getClass().getSimpleName() + ": " + m.toString());
		}
		this.movePtr.set(m);

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

		double maxDistance = 0.0;
		/* Keep searching until time is up. */
		while (System.currentTimeMillis() - start < this.d.getExecution()) {
			/* Generate a new, mutated move. */
			m = mutateMove(m);

			/* If the mutated move creates more distance, keep it. */
			double newDistance = getMoveDistance(m, distanceMap);
			if (newDistance > maxDistance) {
				maxDistance = newDistance;
				if (!this.d.getTournament()) {
					Helper.log(this.getClass().getSimpleName() + ": "
							+ m.toString());
				}
				this.movePtr.set(m);
			}
		}
	}

	public static Move mutateMove(Move m) {
		/* Slightly change the vertices in the move. */
		double jump_probability = 0.2;

		Move moves = new Move();

		for (MovePoint mp : m) {
			Vertex v = mp.vertex;
			RandomSurferIterator randomSurfer = new RandomSurferIterator(
					v.getParentGraph(), 0.0, v);
			while (Finals.RANDOM.nextDouble() < jump_probability) {
				v = randomSurfer.next();
			}

			moves.putVertex(v, 1.0);
		}

		if (moves.getVerticesCount() < m.getVerticesCount()) {
			return mutateMove(m);
		} else {
			return moves;
		}
	}

	public static Move getRandomMove(Graph g, int num) {
		/* Return a random move. */
		Move m = new Move();
		RandomVertexIterator rvi = new RandomVertexIterator(g);
		while (m.getVerticesCount() < num) {
			m.putVertex(rvi.next(), 1.0);
		}
		return m;
	}

	public static double getMoveDistance(Move m,
			HashMap<VertexPair, Double> distanceMap) {
		/*
		 * Calculates the product of the distances of every pair of vertices in
		 * the move (aka geometric mean).
		 */
		double distance = 0.0;
		for (MovePoint x : m) {
			for (MovePoint y : m) {
				if (!x.vertex.equals(y.vertex)) {
					distance += Math.log(distanceMap.get(new VertexPair(
							x.vertex, y.vertex)));
				}
			}
		}
		return distance;
	}

}