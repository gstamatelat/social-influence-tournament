package gr.james.socialinfluence.tournament;

import aat.AsciiArtTable;
import gr.james.socialinfluence.algorithms.generators.TwoWheelsGenerator;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.Game;
import gr.james.socialinfluence.game.GameDefinition;
import gr.james.socialinfluence.game.GameResult;
import gr.james.socialinfluence.game.Player;
import gr.james.socialinfluence.tournament.players.ComplementaryDistanceGreedyPlayer;
import gr.james.socialinfluence.tournament.players.DistanceLocalSearchPlayer;

public class PlayerDuel {
    public static void main(String[] args) {
        /**
         * The graph object of the game. Use different ones from gr.james.socialinfluence.algorithms.generators.
         *
         * Here is a sample list:
         *
         * Graph g = new PathGenerator(30).create();
         * Graph g = new TwoWheelsGenerator(7).create();
         * Graph g = new TwoWheelsGenerator(13).create();
         * Graph g = new BarabasiAlbertGenerator(25, 2, 1, 1.0).create();
         * Graph g = new BarabasiAlbertGenerator(25, 2, 2, 1.0).create();
         * Graph g = new BarabasiAlbertGenerator(150, 2, 1, 1.0).create();
         * Graph g = new BarabasiAlbertGenerator(150, 2, 2, 1.0).create();
         */
        Graph g = new TwoWheelsGenerator(11).create();

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
        Player p1 = new ComplementaryDistanceGreedyPlayer();
        Player p2 = new DistanceLocalSearchPlayer();

        duel(g, actions, execution, p1, p2);
    }

    public static void duel(Graph g, int actions, long execution, Player p1, Player p2) {
        GameDefinition d = new GameDefinition(actions, (double) actions, execution);

        GameResult gResult = Game.runPlayers(p1, p2, g, d);

        String scoreString;
        if (gResult.score > 0) {
            scoreString = "0 - 1";
        } else if (gResult.score < 0) {
            scoreString = "1 - 0";
        } else {
            scoreString = "0.5 - 0.5";
        }

        AsciiArtTable aat = new AsciiArtTable(3);
        aat.setBorderCharacters("+-++|+-H++++|++++");
        aat.setNoHeaderColumns(2);
        aat.add("Graph", g);
        aat.add("Result", String.format("%s[0] - %s[1]", p1.getClass().getSimpleName(), p2.getClass().getSimpleName()));
        aat.add("", String.format("%s - %s", gResult.m1.deepCopy().normalizeWeights(d.getBudget()),
                gResult.m2.deepCopy().normalizeWeights(d.getBudget())));
        aat.add("", scoreString);
        aat.add("Full State", gResult.fullState);
        aat.add("Average", String.format("%.2f", gResult.fullState.getAverage()));
        aat.print(System.out);

        // ----------------------------------------------------------------------------------------

        /*TableFormatter tf = new SimpleTableFormatter(true)
                .nextRow()
                .nextCell(TableFormatter.ALIGN_RIGHT, TableFormatter.VALIGN_TOP).addLine("   Graph   ")
                .nextCell(TableFormatter.ALIGN_LEFT, TableFormatter.VALIGN_TOP).addLine("   " + g + "   ")

                .nextRow()
                .nextCell(TableFormatter.ALIGN_LEFT, TableFormatter.VALIGN_TOP).addLine("   Full State   ")
                .nextCell(TableFormatter.ALIGN_LEFT, TableFormatter.VALIGN_TOP).addLine("   " + gResult.fullState + "   ")

                .nextRow()
                .nextCell(TableFormatter.ALIGN_LEFT, TableFormatter.VALIGN_TOP).addLine("   Left Top   ")
                .nextCell(TableFormatter.ALIGN_LEFT, TableFormatter.VALIGN_TOP).addLine("   Left Center   ");

        String[] table = tf.getFormattedTable();

        for (String aTable : table) {
            System.out.println(aTable);
        }*/
    }
}
