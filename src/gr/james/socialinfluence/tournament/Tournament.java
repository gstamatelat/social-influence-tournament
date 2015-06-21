package gr.james.socialinfluence.tournament;

import gr.james.socialinfluence.game.Game;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.PlayerEnum;
import gr.james.socialinfluence.game.players.*;
import gr.james.socialinfluence.graph.generators.*;
import gr.james.socialinfluence.graph.io.Csv;
import gr.james.socialinfluence.graph.Graph;
import gr.james.socialinfluence.helper.Helper;
import gr.james.socialinfluence.helper.RandomHelper;
import gr.james.socialinfluence.tournament.student.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class Tournament {
	public static Graph getGraphFromId(int id) throws MalformedURLException, IOException {
		switch (id) {
		case 1:
			return TwoWheels.generate(5);
		case 2:
			return TwoWheels.generate(11);
		case 3:
			return TwoWheels.generate(25);
		case 4:
			return BarabasiAlbert.generate(35, 2, 2, 1.0);
		case 5:
			return BarabasiAlbert.generate(75, 3, 3, 1.0);
		case 6:
			return BarabasiAlbert.generate(250, 5, 2, 1.0);
		case 7:
			return BarabasiAlbertCluster.generate(25, 2, 2, 1, 10);
		case 8:
			return Csv.from(new URL("http://loki.ee.duth.gr/school.csv").openStream());
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		long seed = Long.parseLong(args[0]);
		RandomHelper.initRandom(seed);
		
		double precision = Double.parseDouble(args[1]);
		int graphId = Integer.parseInt(args[2]);
		String[] actionsStr = args[3].split(",");
		int[] actions = new int[actionsStr.length];
		for (int i = 0; i < actionsStr.length; i++) { actions[i] = Integer.parseInt(actionsStr[i]); }
		
		System.out.println();
		Helper.log("Using seed on global Random: %d", seed);
		Helper.log("Using precision: %7.1e", precision);
		Helper.log("Using graph id: %d, {%s}", graphId, getGraphFromId(graphId).getMeta());
		Helper.log("Using move actions count: %s", Arrays.toString(actions));
		System.out.println();
		
		int[] maxMoves_t = actions;
		long[] execution_t = { 2000L, 10000L }; // CHANGE THIS
		int rounds = 5; // CHANGE THIS (USUALLY NO NEED)

		long now = System.currentTimeMillis();

		for (int maxMoves : maxMoves_t) {
			for (long execution : execution_t) {
				HashMap<Player, Integer> players = new HashMap<Player, Integer>();
				players.put(new DarthVader(), 0);
				players.put(new Obelix(), 0);
				players.put(new VaSot(), 0);
				players.put(new YalamasPro(), 0);
				
				int max = rounds * players.size() * (players.size() - 1);
				int completed = 0;

				System.out.print(String.format("Actions %d, Execution %d: 0%% ", maxMoves, execution / 1000));

				for (Player p1 : players.keySet()) {
					for (Player p2 : players.keySet()) {
						if (p1 != p2) {
							for (int i = 0; i < rounds; i++) {
								Graph g = getGraphFromId(graphId);
								Game game = new Game(g);
								GameDefinition d = new GameDefinition(maxMoves, maxMoves * 1.0, execution, true);
								game.setPlayer(PlayerEnum.A, p1.findMove(g, d));
								game.setPlayer(PlayerEnum.B, p2.findMove(g, d));
								int result = game.runGame(d, precision).score;
								if (result == 0) {
									players.put(p1, players.get(p1) + TournamentFinals.DRAW);
									players.put(p2, players.get(p2) + TournamentFinals.DRAW);
								} else if (result == -1) {
									players.put(p2, players.get(p2) + TournamentFinals.LOSE);
									players.put(p1, players.get(p1)	+ TournamentFinals.WIN);
								} else {
									players.put(p1, players.get(p1)	+ TournamentFinals.LOSE);
									players.put(p2, players.get(p2)	+ TournamentFinals.WIN);
								}
								System.out.print(String.format("%d%% ", 100	* (++completed) / max));
							}
						}
					}
				}

				String header = String.format("| Actions %d, Execution %d", maxMoves, execution / 1000);
				
				int maxLine = header.length();
				for (Player p : players.keySet()) {
					maxLine = Math.max(maxLine, p.getClass().getSimpleName().length() + 6);
				}
				
				System.out.println();
				System.out.print("/"); for (int i = 0; i < maxLine; i++) { System.out.print("-"); }; System.out.println("\\");
				System.out.print(header); for (int i = 0; i < maxLine - header.length(); i++) { System.out.print(" "); }; System.out.println(" |");
				System.out.print("|"); for (int i = 0; i < maxLine; i++) { System.out.print("-"); }; System.out.println("|");
				for (Player p : players.keySet()) {
					String line = String.format("%3d %s", players.get(p), p.getClass().getSimpleName());
					System.out.print("| ");
					System.out.print(line);
					for (int i = 0; i < maxLine - line.length() - 1; i++) { System.out.print(" "); }; System.out.println("|");
				}
				System.out.print("\\"); for (int i = 0; i < maxLine; i++) { System.out.print("-"); }; System.out.println("/");
				/*try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
			}
		}

		Helper.log("Time for this graph: %d sec", Math.round((System.currentTimeMillis() - now) / 1000.));
	}
}