package gr.james.socialinfluence.tournament;

import gr.james.socialinfluence.algorithms.generators.TwoWheelsGenerator;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.Game;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.GameResult;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.game.players.MaxPageRankPlayer;
import gr.james.socialinfluence.game.players.RandomPlayer;
import gr.james.socialinfluence.graph.MemoryGraph;

public class PlayerDuel {
    public static void main(String[] args) {
        /**
         * The graph object of the game. Use different ones from gr.james.socialinfluence.algorithms.generators.
         *
         * Here is a sample list:
         *
         * Graph g = new PathGenerator<>(MemoryGraph.class, 30).create();
         * Graph g = new TwoWheelsGenerator<>(MemoryGraph.class, 7).create();
         * Graph g = new TwoWheelsGenerator<>(MemoryGraph.class, 13).create();
         * Graph g = new BarabasiAlbertGenerator<>(MemoryGraph.class, 25, 2, 1, 1.0).create();
         * Graph g = new BarabasiAlbertGenerator<>(MemoryGraph.class, 25, 2, 2, 1.0).create();
         * Graph g = new BarabasiAlbertGenerator<>(MemoryGraph.class, 150, 2, 1, 1.0).create();
         * Graph g = new BarabasiAlbertGenerator<>(MemoryGraph.class, 150, 2, 2, 1.0).create();
         */
        Graph g = new TwoWheelsGenerator<>(MemoryGraph.class, 11).create();

        /**
         * Action count
         */
        int actions = 4;

        /**
         * Time to execute, in milliseconds; use 0 for unlimited time
         */
        long execution = 10000;

        /**
         * Players
         */
        Player p1 = new MaxPageRankPlayer();
        Player p2 = new RandomPlayer();

        duel(g, actions, execution, p1, p2);
    }

    public static void duel(Graph g, int actions, long execution, Player p1, Player p2) {
        GameDefinition d = new GameDefinition(actions, (double) actions, execution);

        GameResult gResult = Game.runPlayers(p1, p2, g, d);

        System.out.println(String.format("Graph: %s", g));
        System.out.println(String.format("%s[0] - %s[1]", p1.getClass().getSimpleName(), p2.getClass().getSimpleName()));

        if (gResult.score > 0) {
            System.out.println("0 - 1");
        } else if (gResult.score < 0) {
            System.out.println("1 - 0");
        } else {
            System.out.println("0.5 - 0.5");
        }

        System.out.println(String.format("%s - %s", gResult.m1.deepCopy()
                .normalizeWeights(d.getBudget()), gResult.m2.deepCopy()
                .normalizeWeights(d.getBudget())));

        System.out.println(String.format("Full State: %s", gResult.fullState));
        System.out.println(String.format("Average Full State w/o Stubborn Agents: %s",
                gResult.fullState.getMean(g.getVertices())));
    }
}
