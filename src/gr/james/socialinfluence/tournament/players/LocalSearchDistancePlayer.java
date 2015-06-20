package gr.james.socialinfluence.tournament.players;

import java.util.Map;

import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.MovePoint;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.FloydWarshall;
import gr.james.socialinfluence.graph.algorithms.iterators.RandomSurferIterator;
import gr.james.socialinfluence.graph.algorithms.iterators.RandomVertexIterator;
import gr.james.socialinfluence.graph.collections.VertexPair;
import gr.james.socialinfluence.helper.Helper;
import gr.james.socialinfluence.helper.RandomHelper;

public class LocalSearchDistancePlayer extends Player {

	@Override
	public void getMove() {
		Move m = new Move();

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
		Map<VertexPair, Double> distanceMap = FloydWarshall.execute(g);

		double maxDistance = 0.0;
		/* Keep searching until time is up. */
		while (!this.isInterrupted()) {
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
			while (RandomHelper.getRandom().nextDouble() < jump_probability) {
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
			Map<VertexPair, Double> distanceMap) {
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