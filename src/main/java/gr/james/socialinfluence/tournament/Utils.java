package gr.james.socialinfluence.tournament;

import com.google.common.io.Resources;
import gr.james.socialinfluence.algorithms.iterators.RandomVertexIterator;
import gr.james.socialinfluence.api.Graph;
import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.util.Conditions;
import gr.james.socialinfluence.util.Helper;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.Properties;

public final class Utils {
    /**
     * <p>Return a random move in {@code g} with {@code actions} vertices; each vertex maps to weight {@code 1.0}.</p>
     *
     * @param g       the graph from which to select the move vertices
     * @param actions the amount of vertices to insert in the move
     * @return a random {@code Move} with the specified criteria
     */
    public static Move getRandomMove(Graph g, int actions) {
        Move m = new Move();
        RandomVertexIterator rvi = new RandomVertexIterator(g);
        while (m.getVerticesCount() < actions) {
            m.putVertex(rvi.next(), 1.0);
        }
        return m;
    }

    public static String getAppVersion() {
        Properties props = new Properties();
        try {
            props.load(Resources.getResource("app.properties").openStream());
        } catch (IOException e) {
            throw Helper.convertCheckedException(e);
        }
        return Conditions.requireNonNull(props.getProperty("version"), "version property doesn't exist");
    }

    public static void printHelp(OptionParser p) {
        try {
            p.printHelpOn(System.out);
            System.out.println();
        } catch (IOException f) {
            throw new RuntimeException(f);
        }
    }

    public static OptionSet parseArgs(OptionParser p, String[] args) {
        OptionSet options = null;

        try {
            options = p.parse(args);
        } catch (OptionException e) {
            System.out.printf("%n%s%n%n", e.getMessage());
            Utils.printHelp(p);
            System.exit(0);
        }

        if (options.has("help")) {
            System.out.println();
            Utils.printHelp(p);
            System.exit(0);
        }

        return options;
    }
}
