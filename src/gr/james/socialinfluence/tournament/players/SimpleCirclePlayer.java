package gr.james.socialinfluence.tournament.players;

import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.helper.Helper;

public class SimpleCirclePlayer extends gr.james.socialinfluence.game.players.Player {

	@Override
	/**
	 * g: The graph of the game. You may not change this object.
	 * Get a list of vertices by using g.getVertices().
	 * Each Vertex contains a list of inbound and outbound edges.
	 * 
	 * The object d of type GameDefinition contains 4 fields:
	 * 
	 * 1. numOfMoves: How many nodes you are allowed to influence at most.
	 * The Move object that you return must have at most this amount of nodes.
	 * It is recommended to exhaust this limit.
	 * Players that exceed this have their moves automatically sliced by the engine.
	 * 
	 * 2. budget: Maximum sum of the influenced nodes.
	 * This will most likely always be equal to numOfNodes.
	 * If your player exceeds this amount, the game will automatically normalize your move.
	 * 
	 * 3. execution: Execution of this method will be forcibly terminated
	 * after execution milliseconds. Make sure you update the this.movePtr
	 * field by using this.movePtr.set(). You may want to use execution as part of your strategy.
	 * 
	 * 4. tournament: This flag indicates whether there are tournament settings applied or not.
	 * If this is false, you may print additional messages that may help you debug your player.
	 * If not, consider optimizing for performance and avoid printing anything.
	 */
	public void getMove() {
		
		if (!this.g.getMeta().startsWith("Path,") || !this.g.getMeta().contains("cycle=true")) {
			return;
		}

		int period = (int) Math.round(((double) this.g.getVerticesCount() / this.d.getBudget()));

		Move m = new Move();
		int c = 1;
		for (int i = 0; i < this.d.getNumOfMoves(); i++) {
			Vertex n = this.g.getVertexFromId(c);
			m.putVertex(n, 1.0);
			c += period;
		}

		if (!this.d.getTournament()) {
			Helper.log(this.getClass().getSimpleName() + ": " + m.toString());
		}

		this.movePtr.set(m);
	}

}