package gr.james.socialinfluence.tournament.student;

import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Edge;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.Dijkstra;
import gr.james.socialinfluence.graph.algorithms.iterators.PageRankIterator;
import gr.james.socialinfluence.helper.Helper;

import java.util.*;
import java.util.Map.Entry;

public class DarthVader extends Player {

    public static Move getMinimum(HashMap<Move, Double> treeMoves) {
        Double minSumMove = Double.POSITIVE_INFINITY;
        Move minMove = null;
        for (Map.Entry<Move, Double> e : treeMoves.entrySet()) {
            if (e.getValue() < minSumMove) {
                minSumMove = e.getValue();
                minMove = e.getKey();
            }
        }
        return minMove;
    }

    public static void updateVector(HashMap<Vertex[], Double> distanceMap, HashMap<Vertex, Double> vector, Vertex move) {
        for (Map.Entry<Vertex[], Double> e : distanceMap.entrySet()) {
            Vertex[] tt = e.getKey();
            if (tt[0].equals(move)) {
                vector.put(tt[1], vector.get(tt[1]) * (1 - e.getValue()));
            }
        }
    }

    public static Double vectorSum(HashMap<Vertex, Double> v) {
        Double sum = 0.0;
        for (Map.Entry<Vertex, Double> e : v.entrySet()) {
            sum += e.getValue();
        }
        return sum;
    }

    @Override
    public void getMove() {

        if (this.g.getMeta().startsWith("Path,")
                && this.g.getMeta().contains("cycle=true")) {

            Move m = new Move();
            int period = (int) Math
                    .round(((double) this.g.getVerticesCount() / this.d.getBudget()));

            Vertex v = this.g.getRandomVertex();
            Vertex previous = v;

            m.putVertex(v, 1.0);

            while (m.getVerticesCount() < this.d.getNumOfMoves()) {
                for (int j = 0; j < period; j++) {
                    Set<Edge> edges = v.getOutEdges();
                    Edge[] edgesArray = edges.toArray(new Edge[0]);
                    if (edgesArray[0].getTarget().equals(previous)) {
                        previous = v;
                        v = edgesArray[1].getTarget();
                    } else {
                        previous = v;
                        v = edgesArray[0].getTarget();
                    }
                }
                m.putVertex(v, 1.0);
            }

            if (!this.d.getTournament()) {
                Helper.log(this.getClass().getSimpleName() + ": " + m.toString());
            }

            this.movePtr.set(m);
        } else if (this.g.getMeta().startsWith("TwoWheels")) {
            if (this.g.getVerticesCount() <= 13) {

                HashMap<Vertex[], Double> distanceMap = new HashMap<Vertex[], Double>();
                HashMap<Vertex, Double> vector = new HashMap<Vertex, Double>();

                for (Vertex v : this.g.getVertices()) {
                    HashMap<Vertex, Double> temp = Dijkstra.execute(this.g, v);
                    for (Map.Entry<Vertex, Double> e : temp.entrySet()) {
                        distanceMap.put(new Vertex[]{e.getKey(), v}, e.getValue());
                    }
                }
                for (Map.Entry<Vertex[], Double> e : distanceMap.entrySet()) {
                    e.setValue(1 / Math.exp(e.getValue()));
                }

                HashMap<Move, Double> treeMoves = new HashMap<Move, Double>();

                PageRankIterator pri = new PageRankIterator(this.g, 0.0);
                while (pri.hasNext() && !this.isInterrupted()) {
                    Vertex firstGuess = pri.next();

                    vector.clear();
                    for (Vertex v : this.g.getVertices()) {
                        vector.put(v, 1.0);
                    }

                    Move m = new Move();
                    m.putVertex(firstGuess, 1.0);
                    updateVector(distanceMap, vector, firstGuess);

                    while (m.getVerticesCount() < d.getNumOfMoves()) {
                        HashMap<Vertex, Double> sumMap = new HashMap<Vertex, Double>();
                        for (Vertex v : this.g.getVertices()) {
                            HashMap<Vertex, Double> tmpVector = new HashMap<Vertex, Double>();
                            for (Map.Entry<Vertex, Double> e : vector.entrySet()) {
                                tmpVector.put(e.getKey(), e.getValue());
                            }
                            updateVector(distanceMap, tmpVector, v);
                            sumMap.put(v, vectorSum(tmpVector));
                        }
                        Double minSum = Double.POSITIVE_INFINITY;
                        Vertex minNode = null;
                        for (Map.Entry<Vertex, Double> e : sumMap.entrySet()) {
                            if (e.getValue() < minSum) {
                                minNode = e.getKey();
                                minSum = e.getValue();
                            }
                        }
                        m.putVertex(minNode, 1.0);
                        updateVector(distanceMap, vector, minNode);
                    }

                    m.normalizeWeights(d.getBudget());
                    treeMoves.put(m, vectorSum(vector));

                    Move minMove = getMinimum(treeMoves);
                    this.movePtr.set(minMove);

                    if (!this.d.getTournament()) {
                        Helper.log(firstGuess + " : " + minMove);
                    }
                }

            } else {

                List<Vertex> vertices = new ArrayList<Vertex>();

                Move m = new Move();

                PageRankIterator pri = new PageRankIterator(g, 0.0);
                while (vertices.size() < d.getNumOfMoves()) {
                    vertices.add(pri.next());
                }

                for (Vertex v : vertices) {
                    m.putVertex(v, (double) v.getInDegree());
                }

                m.normalizeWeights(d.getBudget());
                if (!this.d.getTournament()) {
                    Helper.log("DarthVader said: " + m);
                }
                this.movePtr.set(m);

            }
        } else if (this.g.getMeta().startsWith("BarabasiAlbert")) {
            if (this.g.getMeta().contains("stepEdges=1")) {

                Map<Vertex, Integer> graphDegrees;

                Move m = new Move();

                graphDegrees = this.g.getInDegree();

                Map<Vertex, Integer> graphMap = sortByComparator(graphDegrees);
                int moves = this.d.getNumOfMoves();
                double[] values = new double[moves];
                int counter = 0;

                for (Entry<Vertex, Integer> e : graphMap.entrySet()) {
                    values[counter] = e.getValue();
                    counter++;
                    if (counter >= moves)
                        break;
                }

                int sum = 0;
                for (int i = 0; i < moves; i++) {
                    sum += values[i];
                }

                for (Entry<Vertex, Integer> e : graphMap.entrySet()) {
                    m.putVertex(e.getKey(), (double) (moves * e.getValue()) / sum);
                    if (m.getVerticesCount() >= moves)
                        break;
                }

                this.movePtr.set(m);


            } else {

                List<Vertex> vertices = new ArrayList<Vertex>();

                Move m = new Move();

                PageRankIterator pri = new PageRankIterator(g, 0.0);
                while (vertices.size() < d.getNumOfMoves()) {
                    vertices.add(pri.next());
                }

                for (Vertex v : vertices) {
                    m.putVertex(v, (double) v.getInDegree());
                }

                m.normalizeWeights(d.getBudget());
                if (!this.d.getTournament()) {
                    Helper.log("DarthVader said: " + m);
                }
                this.movePtr.set(m);
            }

        } else {

            List<Vertex> vertices = new ArrayList<Vertex>();

            Move m = new Move();

            PageRankIterator pri = new PageRankIterator(g, 0.0);
            while (vertices.size() < d.getNumOfMoves()) {
                vertices.add(pri.next());
            }

            for (Vertex v : vertices) {
                m.putVertex(v, (double) v.getInDegree());
            }

            m.normalizeWeights(d.getBudget());
            if (!this.d.getTournament()) {
                Helper.log("DarthVader said: " + m);
            }
            this.movePtr.set(m);
        }
    }

    private Map<Vertex, Integer> sortByComparator(Map<Vertex, Integer> unsortMap) {

        List<Map.Entry<Vertex, Integer>> list =
                new LinkedList<Map.Entry<Vertex, Integer>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Vertex, Integer>>() {
            public int compare(Map.Entry<Vertex, Integer> o1,
                               Map.Entry<Vertex, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<Vertex, Integer> sortedMap = new LinkedHashMap<Vertex, Integer>();
        for (Iterator<Map.Entry<Vertex, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<Vertex, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
