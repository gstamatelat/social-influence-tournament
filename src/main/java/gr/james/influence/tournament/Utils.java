package gr.james.influence.tournament;

import com.google.common.io.Resources;
import gr.james.influence.algorithms.iterators.RandomSurferIterator;
import gr.james.influence.algorithms.iterators.RandomVertexIterator;
import gr.james.influence.api.Graph;
import gr.james.influence.game.Move;
import gr.james.influence.graph.Vertex;
import gr.james.influence.util.Conditions;
import gr.james.influence.util.RandomHelper;
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
            throw Utils.convertCheckedException(e);
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
        }

        if (options != null && options.has("help")) {
            System.out.println();
            Utils.printHelp(p);
        }

        return options;
    }

    public static String getExceptionString(Throwable e) {
        // TODO: If e.getMessage() is null it becomes ugly
        String exceptionAsString = String.format("\t%s: %s\n", e.getClass().getName(), e.getMessage());
        for (StackTraceElement s : e.getStackTrace()) {
            exceptionAsString += String.format("\t\t%s\n", s);
        }
        return exceptionAsString.substring(0, exceptionAsString.length() - 1);
    }

    public static Move mutateMove(Move m, Graph g) {
        return mutateMove(m, g, 0.2);
    }

    public static Move mutateMove(Move m, Graph g, double jumpProbability) {
        Move moves = new Move();

        for (Vertex v : m) {
            RandomSurferIterator randomSurfer = new RandomSurferIterator(g, 0.0, v);
            while (RandomHelper.getRandom().nextDouble() < jumpProbability) {
                v = randomSurfer.next();
            }

            moves.putVertex(v, 1.0);
        }

        if (moves.getVerticesCount() < m.getVerticesCount()) {
            return mutateMove(m, g);
        } else {
            return moves;
        }
    }

    @SuppressWarnings({"AssertWithSideEffects", "ConstantConditions", "UnusedAssignment"})
    public static boolean isAssertionEnabled() {
        boolean assertsEnabled = false;
        assert assertsEnabled = true;
        return assertsEnabled;
    }

    public static RuntimeException convertCheckedException(Exception e) {
        throw new RuntimeException(String.format("%s: %s", e.getClass().getName(), e.getMessage()));
    }
}
