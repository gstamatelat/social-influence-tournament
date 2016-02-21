package gr.james.influence.tournament.tournamentplayers;

import gr.james.influence.algorithms.iterators.GraphStateIterator;
import gr.james.influence.algorithms.scoring.PageRank;
import gr.james.influence.api.Graph;
import gr.james.influence.game.GameDefinition;
import gr.james.influence.game.Move;
import gr.james.influence.game.MovePointer;
import gr.james.influence.game.Player;
import gr.james.influence.util.collections.GraphState;


public class IroGiannisHaris extends Player {
    @Override
    public void suggestMove(Graph g, GameDefinition d, MovePointer movePtr) {
        int N = g.getVerticesCount();
        int step = N / d.getActions();
        int i = 0;

        Move m = new Move();
        GraphStateIterator<Double> it = new GraphStateIterator<>(PageRank.execute(g, 0.00));
        GraphState<Double> r = PageRank.execute(g, 0.00);

        while (m.getVerticesCount() < d.getActions()) {
            m.putVertex(it.next().getObject(), 1.0);


        }
        /*GraphState<Double> r = PageRank.execute(g, 0.0);
        System.out.println(r);
    	System.out.println(r.get(r.keySet().toArray()[0]).getClass().getName());
    	System.out.println(r.get(r.keySet().toArray()[0]));
    	int tt = r.get(r.keySet().toArray()[0]).intValue();
    	
    	while (m.getVerticesCount() < d.getActions()) {
    		m.putVertex(g.getVertexFromIndex(tt), 1.0);
    		System.out.println(g.getVertexFromIndex(i));
    		i += step;
    	}*/
        /*
    	 GraphState<Double> r = PageRank.execute(g, 0.0);

		��� ���������� �� eigenvector centrality ��� ����� ���� ������� ��� g.

		�������, � ������

		r.get(v)
    	 */
        //����������� Eigenvector Centrality ���� ������
        //��������� ���� �/2 ������� �� �� ���������� Eigenvector Centrality
        //������������ ��� �������� ������ ��� ������ ��� ������� ��������
        //���������� ���� ������� �� ��� ���������� �������� ������ ����

        movePtr.submit(m);
    }
}
