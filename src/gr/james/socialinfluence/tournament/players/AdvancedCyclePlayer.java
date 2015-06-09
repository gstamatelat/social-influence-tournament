package gr.james.socialinfluence.tournament.players;

import java.util.Set;

import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Edge;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.helper.Helper;

public class AdvancedCyclePlayer extends Player {

	@Override
	public void getMove() {

		if (!this.g.getMeta().startsWith("Path,")
				|| !this.g.getMeta().contains("cycle=true")) {
			return;
		}

		Move m = new Move();

		final double period = (double) this.g.getVerticesCount()
				/ this.d.getNumOfMoves();
		double c = period;

		Vertex v = this.g.getRandomVertex();
		Vertex previous = v;

		m.putVertex(v, 1.0);

		while (m.getVerticesCount() < this.d.getNumOfMoves()) {
			for (int j = 0; j < (int) (c + 0.5); j++) {
				Set<Edge> edges = v.getOutEdges();
				Edge[] edgesArray = edges.toArray(new Edge[0]);
				if (edgesArray[0].getTarget().equals(previous)) {
					previous = v;
					v = edgesArray[1].getTarget();
				} else {
					previous = v;
					v = edgesArray[0].getTarget();
				}
			}
			m.putVertex(v, 1.0);
			c += period - (int) (c + 0.5);
		}

		if (!this.d.getTournament()) {
			Helper.log(this.getClass().getSimpleName() + ": " + m.toString());
		}

		this.movePtr.set(m);
	}

}