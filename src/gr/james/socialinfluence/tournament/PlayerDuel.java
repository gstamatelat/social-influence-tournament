package gr.james.socialinfluence.tournament;

import gr.james.socialinfluence.game.Game;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.GameResult;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.PlayerEnum;
import gr.james.socialinfluence.game.players.*;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.graph.generators.*;
import gr.james.socialinfluence.tournament.players.*;

public class PlayerDuel {

	public static void main(String[] args) {
		/*
		 * numOfMoves: How many nodes does a move consist of.
		 * 
		 * execution: Time limit in milliseconds.
		 * 
		 * tournament: Use tournament settings.
		 */
		int numOfMoves = 4;
		long execution = 10000;
		boolean tournament = false;

		GameDefinition d = new GameDefinition(numOfMoves, (double) numOfMoves,
				execution, tournament);

		/* This is your player. Rename accordingly. */
		Player p1 = new GreedyDistancePlayer();

		/*
		 * This is your opponent. Use different default player from
		 * gr.james.socialinfluence.game.players or create your own.
		 * 
		 * Player p2 = new MaxPageRankPlayer().
		 */
		Player p2 = new LocalSearchDistancePlayer();
		// p2.setOption("epsilon", 0.001);
		// p2.setOption("weight_levels", 2);
		// p2.setOption("clever", true);

		/*
		 * The graph object of the game. Use different ones from
		 * gr.james.socialinfluence.graph.generators or create your own.
		 * 
		 * Graph g = Path.generate(30, true);
		 * Graph g = TwoWheels.generate(7);
		 * Graph g = TwoWheels.generate(13);
		 * Graph g = BarabasiAlbert.generate(25, 2, 1, 1.0);
		 * Graph g = BarabasiAlbert.generate(25, 2, 2, 1.0);
		 * Graph g = BarabasiAlbert.generate(150, 2, 1, 1.0);
		 * Graph g = BarabasiAlbert.generate(150, 2, 2, 1.0);
		 */
		Graph g = Path.generate(30, true);

		/* The game execution */
		Game game = new Game(g);
		Move m1 = p1.findMove(g, d);
		Move m2 = p2.findMove(g, d);
		game.setPlayer(PlayerEnum.A, m1);
		game.setPlayer(PlayerEnum.B, m2);

		GameResult gResult = game.runGame(d);

		System.out.println(String.format("Graph: %s", g.getMeta()));
		System.out.println(String.format("%s[0] - %s[1]", p1.getClass()
				.getSimpleName(), p2.getClass().getSimpleName()));

		if (gResult.score > 0) {
			System.out.println("0 - 1");
		} else if (gResult.score < 0) {
			System.out.println("1 - 0");
		} else {
			System.out.println("0.5 - 0.5");
		}

		System.out.println(String.format("%s - %s", m1, m2));

		System.out.println(String.format("Full State: %s", gResult.fullState));
		System.out.println(String.format("Average Full State: %s",
				gResult.fullState.getMean()));
	}
}