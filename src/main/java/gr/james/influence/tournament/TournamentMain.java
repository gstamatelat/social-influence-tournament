package gr.james.influence.tournament;

import gr.james.influence.algorithms.generators.BarabasiAlbertGenerator;
import gr.james.influence.game.GameDefinition;
import gr.james.influence.game.Player;
import gr.james.influence.game.tournament.Tournament;
import gr.james.influence.game.tournament.TournamentDefinition;
import gr.james.influence.tournament.myplayers.ParallelRandomSearchPlayer;
import gr.james.influence.tournament.myplayers.RandomSearchPlayer;
import gr.james.influence.tournament.myplayers.WeightedRandomSearchPlayer;
import gr.james.influence.util.RandomHelper;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TournamentMain {
    public static void main(String[] args) {
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
                new WeightedRandomSearchPlayer(), new ParallelRandomSearchPlayer(),
                new RandomSearchPlayer()
        );

        /**
         * Create a TournamentDefinition list
         */
        List<TournamentDefinition> rounds = new ArrayList<>();
        /*rounds.add(new TournamentDefinition(
                new BarabasiAlbertGenerator(125, 2, 1, 1.0),
                new GameDefinition(3, 3.0, 5000L, 1.0e-5),
                5,
                true
        ));*/
        rounds.add(new TournamentDefinition(
                new BarabasiAlbertGenerator(125, 2, 2, 1.0),
                new GameDefinition(3, 3.0, 5000L, 1.0e-5),
                10,
                false
        ));
        /*rounds.add(new TournamentDefinition(
                new WattsStrogatzGenerator(100, 14, 0.5),
                new GameDefinition(3, 3.0, 5000L, 1.0e-5),
                5,
                true
        ));*/
        /*rounds.add(new TournamentDefinition(
                new TwoWheelsGenerator(13),
                new GameDefinition(4, 4.0, 5000L, 1.0e-5),
                5,
                true
        ));*/

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
