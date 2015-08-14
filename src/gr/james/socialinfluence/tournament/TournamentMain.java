package gr.james.socialinfluence.tournament;

import gr.james.socialinfluence.algorithms.generators.BarabasiAlbertGenerator;
import gr.james.socialinfluence.api.GraphGenerator;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.players.GreedyPlayer;
import gr.james.socialinfluence.game.players.MaxDegreePlayer;
import gr.james.socialinfluence.game.players.MaxPageRankPlayer;
import gr.james.socialinfluence.game.players.RandomPlayer;
import gr.james.socialinfluence.game.tournament.Tournament;
import gr.james.socialinfluence.graph.MemoryGraph;
import gr.james.socialinfluence.util.RandomHelper;

public class TournamentMain {
    public static void main(String[] args) {
        /**
         * Set the seed
         */
        RandomHelper.initRandom(3724);

        /**
         * Instantiate a Tournament
         */
        Tournament tournament = new Tournament();

        /**
         * Add the players once
         */
        tournament.addPlayers(new MaxPageRankPlayer(), new MaxDegreePlayer(), new GreedyPlayer(), new RandomPlayer());

        /**
         * Create a generator and a definition
         */
        GraphGenerator generator = new BarabasiAlbertGenerator<>(MemoryGraph.class, 100, 2, 2, 1.0);
        GameDefinition d = new GameDefinition(3, 3.0, 1000L);

        /**
         * Run the tournament
         */
        tournament.run(generator, d);

        /**
         * Print rankings
         */
        System.out.println();
        System.out.printf("GRAPH:      %s\n", generator.create());
        System.out.printf("DEFINITION: %s\n", d);
        System.out.printf("SCORES:     %s\n", tournament.getScores());
    }
}
