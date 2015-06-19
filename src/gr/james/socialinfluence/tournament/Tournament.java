package gr.james.socialinfluence.tournament;

import gr.james.socialinfluence.game.Game;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.PlayerEnum;
import gr.james.socialinfluence.game.players.*;
import gr.james.socialinfluence.graph.generators.*;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.helper.Helper;
import gr.james.socialinfluence.tournament.student.*;

import java.util.HashMap;

public class Tournament {
	public static void main(String[] args) throws Exception {
		int[] maxMoves_t = { 2, 3, 5 }; // CHANGE THIS
		long[] execution_t = { 5000L }; // CHANGE THIS
		int rounds = 5; // CHANGE THIS (USUALLY NO NEED)

		long now = System.currentTimeMillis();

		for (int maxMoves : maxMoves_t) {
			for (long execution : execution_t) {
				HashMap<Player, Double> players = new HashMap<Player, Double>();
				players.put(new DarthVader(), 0.0);
				players.put(new Obelix(), 0.0);
				players.put(new VaSot(), 0.0);
				players.put(new YalamasPro(), 0.0);
				
				int max = rounds * players.size() * (players.size() - 1);
				int completed = 0;

				System.out.print(String.format("Moves %d: 0%% ", maxMoves));

				for (Player p1 : players.keySet()) {
					for (Player p2 : players.keySet()) {
						if (p1 != p2) {
							for (int i = 0; i < rounds; i++) {
								Graph g = TwoWheels.generate(6); // CHANGE THIS
								Game game = new Game(g);
								GameDefinition d = new GameDefinition(maxMoves,
										maxMoves * 1.0, execution, true);
								game.setPlayer(PlayerEnum.A, p1.findMove(g, d));
								game.setPlayer(PlayerEnum.B, p2.findMove(g, d));
								int result = game.runGame(d).score;
								if (result == 0) {
									players.put(p1, players.get(p1)
											+ TournamentFinals.DRAW);
									players.put(p2, players.get(p2)
											+ TournamentFinals.DRAW);
								} else if (result == -1) {
									players.put(p2, players.get(p2)
											+ TournamentFinals.LOSE);
									players.put(p1, players.get(p1)
											+ TournamentFinals.WIN);
								} else {
									players.put(p1, players.get(p1)
											+ TournamentFinals.LOSE);
									players.put(p2, players.get(p2)
											+ TournamentFinals.WIN);
								}
								System.out.print(String.format("%d%% ", 100
										* (++completed) / max));
							}
						}
					}
				}

				System.out.println();
				System.out.println("Moves " + maxMoves + ", Execution "
						+ execution / 1000);
				for (Player p : players.keySet()) {
					System.out.println(p.getClass().getSimpleName() + " "
							+ players.get(p));
				}
				System.out.println("-------------");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		Helper.log("Time for this graph: %d",
				Math.round((System.currentTimeMillis() - now) / 1000.));
	}
}