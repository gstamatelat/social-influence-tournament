package gr.james.socialinfluence.tournament;

import gr.james.socialinfluence.algorithms.generators.BarabasiAlbertGenerator;
import gr.james.socialinfluence.algorithms.generators.TwoWheelsGenerator;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.game.players.GreedyPlayer;
import gr.james.socialinfluence.game.players.MaxDegreePlayer;
import gr.james.socialinfluence.game.players.MaxPageRankPlayer;
import gr.james.socialinfluence.game.players.RandomPlayer;
import gr.james.socialinfluence.game.tournament.Tournament;
import gr.james.socialinfluence.game.tournament.TournamentDefinition;
import gr.james.socialinfluence.graph.MemoryGraph;
import gr.james.socialinfluence.util.RandomHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TournamentMain {
    public static void main(String[] args) {
        /**
         * Set the seed
         */
        if (!RandomHelper.initRandom(3724)) {
            throw new RuntimeException();
        }

        /**
         * Instantiate a Tournament
         */
        Tournament tournament = new Tournament(
                new MaxPageRankPlayer(), new MaxDegreePlayer(), new GreedyPlayer(), new RandomPlayer()
        );

        /**
         * Create a TournamentDefinition list
         */
        List<TournamentDefinition> rounds = new ArrayList<>();
        rounds.add(new TournamentDefinition(
                new TwoWheelsGenerator<>(MemoryGraph.class, 11),
                new GameDefinition(1, 1.0, 2000L),
                2
        ));
        rounds.add(new TournamentDefinition(
                new TwoWheelsGenerator<>(MemoryGraph.class, 11),
                new GameDefinition(2, 2.0, 2000L),
                2
        ));
        rounds.add(new TournamentDefinition(
                new BarabasiAlbertGenerator<>(MemoryGraph.class, 125, 2, 2, 1.0),
                new GameDefinition(3, 3.0, 5000L),
                5
        ));

        /**
         * Execute each scenario
         */
        for (TournamentDefinition t : rounds) {
            /**
             * Run the tournament
             */
            Map<Player, Integer> score = tournament.run(t.getGenerator(), t.getDefinition(), t.getRounds());

            /**
             * Print current rankings
             */
            System.out.println();
            System.out.printf("%10s : %s%n", "GRAPH", t.getGenerator().create());
            System.out.printf("%10s : %s%n", "DEFINITION", t.getDefinition());
            System.out.printf("%10s : %s%n", "SCORES", score.entrySet().stream()
                    .sorted((o1, o2) -> -o1.getValue().compareTo(o2.getValue()))
                    .map(item -> String.format("%2s %s", item.getValue(), item.getKey()))
                    .collect(Collectors.joining(String.format("%n%13s", ""))));
        }

        /**
         * Print CSV
         */
        System.out.println();
        System.out.println(tournament.getAllScoresInCsv());
    }
}
