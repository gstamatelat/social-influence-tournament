package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.helper.Helper;

public class SimpleCirclePlayer extends Player {

	@Override
	public void getMove() {

		/* This player only works in the circle graph */
		if (!this.g.getMeta().startsWith("Path,")
				|| !this.g.getMeta().contains("cycle=true")) {
			return;
		}

		/* Optimal spreading distance */
		double period = (double) this.g.getVerticesCount()
				/ this.d.getNumOfMoves();

		/* Initialize a new move without any vertices */
		Move m = new Move();

		/* Start with a random ID */
		double c = (double) g.getRandomVertex().getId();

		for (int i = 0; i < this.d.getNumOfMoves(); i++) {
			/* Select the vertex that corresponds to [c mod N] to avoid overflow */
			Vertex n = this.g.getVertexFromId(((int) (c + 0.5) - 1)
					% g.getVerticesCount() + 1);
			
			/* Add the vertex to m */
			m.putVertex(n, 1.0);
			
			/* Advance by period vertices */
			c += period;
		}

		/* We can print stuff if we don't compete in tournament */
		if (!this.d.getTournament()) {
			Helper.log(this.getClass().getSimpleName() + ": " + m.toString());
		}

		/* Submit the move */
		this.movePtr.set(m);
	}
}