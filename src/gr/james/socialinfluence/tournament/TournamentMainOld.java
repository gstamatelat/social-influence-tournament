package gr.james.socialinfluence.tournament;

import gr.james.socialinfluence.algorithms.generators.BarabasiAlbertClusterGenerator;
import gr.james.socialinfluence.algorithms.generators.BarabasiAlbertGenerator;
import gr.james.socialinfluence.algorithms.generators.TwoWheelsGenerator;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.Game;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.game.players.GreedyPlayer;
import gr.james.socialinfluence.game.players.MaxPageRankPlayer;
import gr.james.socialinfluence.graph.MemoryGraph;
import gr.james.socialinfluence.graph.io.Csv;
import gr.james.socialinfluence.util.RandomHelper;
import gr.james.socialinfluence.util.collections.Weighted;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;

public class TournamentMainOld {
    public static Graph getGraphFromId(int id) throws IOException {
        switch (id) {
            case 1:
                return new TwoWheelsGenerator<>(MemoryGraph.class, 5).create();
            case 2:
                return new TwoWheelsGenerator<>(MemoryGraph.class, 11).create();
            case 3:
                return new TwoWheelsGenerator<>(MemoryGraph.class, 25).create();
            case 4:
                return new BarabasiAlbertGenerator<>(MemoryGraph.class, 35, 2, 2, 1.0).create();
            case 5:
                return new BarabasiAlbertGenerator<>(MemoryGraph.class, 75, 3, 3, 1.0).create();
            case 6:
                return new BarabasiAlbertGenerator<>(MemoryGraph.class, 250, 5, 2, 1.0).create();
            case 7:
                return new BarabasiAlbertClusterGenerator<>(MemoryGraph.class, 25, 2, 2, 1, 10).create();
            case 8:
                return new Csv().from(new URL("http://loki.ee.duth.gr/school.csv").openStream());
        }
        throw new IllegalArgumentException();
    }

    public static void main(String[] args) throws Exception {
        long seed = Long.parseLong(args[0]);
        RandomHelper.initRandom(seed);

        double precision = Double.parseDouble(args[1]);
        int graphId = Integer.parseInt(args[2]);
        String[] actionsStr = args[3].split(",");
        int[] actions = new int[actionsStr.length];
        for (int i = 0; i < actionsStr.length; i++) {
            actions[i] = Integer.parseInt(actionsStr[i]);
        }

        System.out.println();
        System.out.printf("Using seed on global Random: %d", seed);
        System.out.printf("Using precision: %7.1e", precision);
        System.out.printf("Using graph id: %d, {%s}", graphId, getGraphFromId(graphId));
        System.out.printf("Using move actions count: %s", Arrays.toString(actions));
        System.out.println();

        System.out.print("Press enter to start ... ");
        System.in.read();
        System.out.println();

        int[] maxMoves_t = actions;
        long[] execution_t = {2000L, 10000L}; // CHANGE THIS
        int rounds = 5; // CHANGE THIS (USUALLY NO NEED)

        long now = System.currentTimeMillis();

        for (int maxMoves : maxMoves_t) {
            for (long execution : execution_t) {
                HashMap<Player, Integer> players = new HashMap<>();
                players.put(new MaxPageRankPlayer(), 0);
                players.put(new GreedyPlayer(), 0);

                int max = rounds * players.size() * (players.size() - 1);
                int completed = 0;

                System.out.print(String.format("Actions %d, Execution %d: 0%% ", maxMoves, execution / 1000));

                for (Player p1 : players.keySet()) {
                    for (Player p2 : players.keySet()) {
                        if (p1 != p2) {
                            for (int i = 0; i < rounds; i++) {
                                Graph g = getGraphFromId(graphId);
                                GameDefinition d = new GameDefinition(maxMoves, maxMoves * 1.0, execution);
                                int result = Game.runPlayers(p1, p2, g, d).score;
                                if (result == 0) {
                                    players.put(p1, players.get(p1) + TournamentFinalsOld.DRAW);
                                    players.put(p2, players.get(p2) + TournamentFinalsOld.DRAW);
                                } else if (result == -1) {
                                    players.put(p2, players.get(p2) + TournamentFinalsOld.LOSE);
                                    players.put(p1, players.get(p1) + TournamentFinalsOld.WIN);
                                } else {
                                    players.put(p1, players.get(p1) + TournamentFinalsOld.LOSE);
                                    players.put(p2, players.get(p2) + TournamentFinalsOld.WIN);
                                }
                                System.out.print(String.format("%d%% ", 100 * (++completed) / max));
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
                System.out.print("/");
                for (int i = 0; i < maxLine; i++) {
                    System.out.print("-");
                }
                System.out.println("\\");
                System.out.print(header);
                for (int i = 0; i < maxLine - header.length(); i++) {
                    System.out.print(" ");
                }
                System.out.println(" |");
                System.out.print("|");
                for (int i = 0; i < maxLine; i++) {
                    System.out.print("-");
                }
                System.out.println("|");

                PriorityQueue<Weighted<Player, Integer>> pQueue = new PriorityQueue<>();
                for (Player p : players.keySet()) {
                    pQueue.add(new Weighted<>(p, players.get(p)));
                }

                while (pQueue.size() > 0) {
                    Weighted<Player, Integer> ex = pQueue.poll();
                    String line = String.format("%d %s", ex.getWeight(), ex.getObject().getClass().getSimpleName());
                    System.out.print("| ");
                    System.out.print(line);
                    for (int i = 0; i < maxLine - line.length() - 1; i++) {
                        System.out.print(" ");
                    }
                    System.out.println("|");
                }

                System.out.print("\\");
                for (int i = 0; i < maxLine; i++) {
                    System.out.print("-");
                }
                System.out.println("/");

                int counter = 16;
                System.out.println();
                System.out.print("Continue in ");
                while (--counter > 0) {
                    System.out.printf("%d ", counter);
                    Thread.sleep(1000);
                }
                System.out.print("0");
                System.out.println();
            }
        }

        System.out.printf("Time for this graph: %d sec", Math.round((System.currentTimeMillis() - now) / 1000.));
    }
}
