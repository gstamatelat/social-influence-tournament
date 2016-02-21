package gr.james.influence.tournament;

import gr.james.influence.algorithms.generators.BarabasiAlbertGenerator;
import gr.james.influence.algorithms.generators.TwoWheelsGenerator;
import gr.james.influence.algorithms.generators.WattsStrogatzGenerator;
import gr.james.influence.api.GraphGenerator;
import gr.james.influence.game.GameDefinition;
import gr.james.influence.game.Player;
import gr.james.influence.game.players.MasterGreedyPlayer;
import gr.james.influence.game.tournament.Tournament;
import gr.james.influence.game.tournament.TournamentDefinition;
import gr.james.influence.graph.io.Csv;
import gr.james.influence.graph.io.Edges;
import gr.james.influence.tournament.tournamentplayers.FinalPlayer;
import gr.james.influence.tournament.tournamentplayers.ParallelWeightedRandomSearchPlayer;
import gr.james.influence.util.RandomHelper;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TournamentMain {
    private static final double PRECISION = 1.0e-5;

    public static void main(String[] args) throws IOException {
        /**
         * Command line parameters
         */
        OptionParser parser = new OptionParser() {
            {
                acceptsAll(Arrays.asList("s", "seed"), "Tournament seed").withRequiredArg().required().ofType(Long.class);
                acceptsAll(Arrays.asList("?", "h", "help"), "Show help").forHelp();
            }
        };

        OptionSet options = Utils.parseArgs(parser, args);

        if (options == null) {
            return;
        }

        /**
         * Version
         */
        System.out.printf("social-influence-tournament v%s%n%n", Utils.getAppVersion());

        /**
         * Set the seed
         */
        if (!RandomHelper.initRandom((long) options.valueOf("seed"))) {
            throw new RuntimeException();
        }

        /**
         * Instantiate a Tournament
         */
        Tournament tournament = new Tournament(
                new ParallelWeightedRandomSearchPlayer(),
                new FinalPlayer(),
                new MasterGreedyPlayer()
        );

        /**
         * Create a TournamentDefinition list
         */
        List<TournamentDefinition> rounds = new ArrayList<>();

        for (int i : new int[]{1, 2, 3, 4, 11}) {
            rounds.add(new TournamentDefinition(
                    new TwoWheelsGenerator(6),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{1, 2, 3, 4, 25}) {
            rounds.add(new TournamentDefinition(
                    new TwoWheelsGenerator(13),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 3, 7, 25, 50}) {
            rounds.add(new TournamentDefinition(
                    new BarabasiAlbertGenerator(50, 2, 1, 1.0),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 3, 7, 25, 50}) {
            rounds.add(new TournamentDefinition(
                    new BarabasiAlbertGenerator(50, 2, 2, 1.0),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 3, 7, 75, 150}) {
            rounds.add(new TournamentDefinition(
                    new BarabasiAlbertGenerator(150, 2, 1, 1.0),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 3, 7, 75, 150}) {
            rounds.add(new TournamentDefinition(
                    new BarabasiAlbertGenerator(150, 2, 2, 1.0),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 3, 7, 15, 30}) {
            rounds.add(new TournamentDefinition(
                    new WattsStrogatzGenerator(30, 6, 0.2),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 3, 7, 15, 30}) {
            rounds.add(new TournamentDefinition(
                    new WattsStrogatzGenerator(30, 6, 0.5),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 3, 7, 50, 100}) {
            rounds.add(new TournamentDefinition(
                    new WattsStrogatzGenerator(100, 14, 0.2),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 3, 7, 50, 100}) {
            rounds.add(new TournamentDefinition(
                    new WattsStrogatzGenerator(100, 14, 0.5),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 5, 50}) {
            rounds.add(new TournamentDefinition(
                    GraphGenerator.decorate(new Csv().from(new URL("https://euclid.ee.duth.gr:25312/index.php/s/wXmIirefZKmv95w/download?path=%2F&files=school-2.csv"))),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        for (int i : new int[]{2, 5, 50}) {
            rounds.add(new TournamentDefinition(
                    GraphGenerator.decorate(new Edges(" ").from(new URL("https://euclid.ee.duth.gr:25312/index.php/s/wXmIirefZKmv95w/download?path=%2F&files=twitter.edges"))),
                    new GameDefinition(i, i, 5000L, PRECISION),
                    5,
                    true
            ));
        }

        /**
         * Execute each scenario
         */
        for (TournamentDefinition t : rounds) {
            /**
             * Run the tournament
             */
            Map<Player, Integer> score = tournament.run(t.getGenerator(), t.getDefinition(),
                    t.getRounds(), t.getOneGraphPerRound());

            /**
             * Print current rankings
             */
            System.out.println();
            System.out.printf("%10s : %s%n", "GRAPH", t.getGenerator().generate());
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
