package gr.james.socialinfluence.tournament.student;

import java.util.Set;

import gr.james.socialinfluence.game.Move;
import gr.james.socialinfluence.game.players.Player;
import gr.james.socialinfluence.graph.Edge;
import gr.james.socialinfluence.graph.Vertex;
import gr.james.socialinfluence.graph.algorithms.Degree;
import gr.james.socialinfluence.graph.algorithms.PageRank;
import gr.james.socialinfluence.collections.GraphState;

public class Obelix extends Player {

	@Override
	public void getMove() {
		
		long startTime = System.currentTimeMillis();
		String graphType = this.g.getMeta();
		
		if (graphType.startsWith("TwoWheels")){
			//num of moves o arithmos twn epitreptwn kinhsewn
			int numOfMoves = this.d.getNumOfMoves();
			// numOfVertices panta monos arithmos
			int numOfVertices = this.g.getVerticesCount();
			// to seed sto generate()
			int n = (numOfVertices + 1)/2;
			
			GraphState degrees = Degree.execute(g, true);
			
	        int[] largestDegrees = new int[3];
	        Vertex[] largestDegreeVertices = new Vertex[3];
	        for (int i = 0; i < 3; i ++ ){
	        	largestDegrees[i] = 0;
	        	largestDegreeVertices[i] = this.g.getVertexFromId(2);
	        }
	        
	        //find the 3 largest values and their vertex position
	        for (Vertex v : degrees.keySet()) {
	        	Double val = new Double(degrees.get(v).doubleValue());
	        	if (val.intValue() > largestDegrees[0] ){
	        		largestDegrees[2] = largestDegrees[1];
	        		largestDegrees[1] = largestDegrees[0];
	        		largestDegrees[0] = val.intValue();
	        		largestDegreeVertices[2] = largestDegreeVertices[1];
	        		largestDegreeVertices[1] = largestDegreeVertices[0];
	        		largestDegreeVertices[0] = v;
	        	} else if (val.intValue() > largestDegrees[1] ){
	        		largestDegrees[2] = largestDegrees[1];
	            	largestDegrees[1] = val.intValue();
	            	largestDegreeVertices[2] = largestDegreeVertices[1];
	            	largestDegreeVertices[1] = v;
	        	} else if (val.intValue() > largestDegrees[2] ){
	        		largestDegrees[2] = val.intValue();
	        		largestDegreeVertices[2] = v;
	        	}
	        }
	        Vertex cg = null, cw1 = null, cw2 = null;
	        if (n < 7){
	        	cg = largestDegreeVertices[0];
	        	cw1 = largestDegreeVertices[1];
	        	cw2 = largestDegreeVertices[2];
	        } else if (n > 7) {
	        	cg = largestDegreeVertices[2];
	        	cw1 = largestDegreeVertices[0];
	        	cw2 = largestDegreeVertices[1];
	        } else {
	    		Set<Edge> edges = largestDegreeVertices[0].getOutEdges();
	    		boolean found1 = false, found2 = false;
	    		for (Edge e: edges) {
	    			if (e.getTarget() == largestDegreeVertices[1])
	    				found1 = true;
	    			else if (e.getTarget() == largestDegreeVertices[2])
	    				found2 = true;
	    			if (found1&&found2) break;
	    		}
	    		if (found1 && found2){
	            	cg = largestDegreeVertices[0];
	            	cw1 = largestDegreeVertices[1];
	            	cw2 = largestDegreeVertices[2];
	    		}
	    		else if (found1) {
	            	cg = largestDegreeVertices[1];
	            	cw1 = largestDegreeVertices[0];
	            	cw2 = largestDegreeVertices[2];
	    		}else if (found2){
	            	cg = largestDegreeVertices[2];
	            	cw1 = largestDegreeVertices[0];
	            	cw2 = largestDegreeVertices[1];
	    		}
	        }
	        
	    	double cgBudget =(double) 1;
	    	double cwBudget =((double) (n - 1) / 3.883495146 ) * cgBudget;
	        
			Move m = new Move();
	        switch (numOfMoves) {
	        case 1: 
	        	m.putVertex(cg, 1); 
	        	break;
	        case 2: 
	        	m.putVertex(cw1, 0.5);
	        	m.putVertex(cw2, 0.5);
	        	break;
	        case 3:
	        	m.putVertex(cg, cgBudget);
	        	m.putVertex(cw1, cwBudget);
	        	m.putVertex(cw2, cwBudget);
	        	break;
	        default:
	            /*
	             * movesLeft is the number of moves that need to be defined for each half circle
	             * after the first 3 moves are defined
	             */
	            int nodesOnEachSide = n - 2;
	            int movesLeft = numOfMoves-3;
	            int movesLeft1 = 0, movesLeft2 = 0;
	            int stepsTNP1 = 0, stepsTNP2 = 0;
	            if (movesLeft % 2 == 0){
	            	movesLeft1 = movesLeft /2;
	            	movesLeft2 = movesLeft1;
	            	stepsTNP1 = (int) (nodesOnEachSide / (movesLeft1+1)) + 1;
	            	stepsTNP2 = stepsTNP1;
	            } else {
	            	movesLeft1 = (movesLeft + 1)/2;
	            	movesLeft2 = movesLeft1 - 1;
	            	stepsTNP1 = (int) (nodesOnEachSide / (movesLeft1 + 1)) + 1;
	            	stepsTNP2 = (int) (nodesOnEachSide / (movesLeft2 + 1)) + 1;
	            }
	            
	            double nBudget = (double) 1 / movesLeft;
	            cgBudget = cgBudget * 2 * nBudget;
	            cwBudget = ((double) (n - 1) / 3.883495146 ) * cgBudget;
	            
	        	m.putVertex(cg, cgBudget);
	        	m.putVertex(cw1, cwBudget);
	        	m.putVertex(cw2, cwBudget);
	            
	            Vertex source = cg;
	            Vertex previous = cw2;
	            Vertex ppt = null;
	            for (int i = 0; i < movesLeft1; i++) {
	            	for (int j = 0; j < stepsTNP1; j++){
	            		ppt = chooseVertex(source, previous, cw1);
	            		previous = source;
	            		source = ppt;
	            	}
	        		m.putVertex(ppt, nBudget);
	            }
	            source = cg;
	            previous = cw1;
	            ppt = null;
	            for (int i = 0; i < movesLeft2; i++) {
	            	for (int j = 0; j < stepsTNP2; j++){
	            		ppt = chooseVertex(source, previous, cw2);
	            		previous = source;
	            		source = ppt;
	            	}
	            	m.putVertex(ppt, nBudget);
	            }
	            break;
	        }
	        //System.out.println("End time wheels: " + (System.currentTimeMillis()-startTime));       
			this.movePtr.set(m);
		} else {
			int numOfMoves = this.d.getNumOfMoves();
			
			//TODO: use ratio to determine weights
			float ratio = numOfMoves/this.g.getVerticesCount(); // 10-35%
			
			GraphState pagerank = PageRank.execute(g, 0.0);
//			GraphState degree = Degree.execute(g, true);

	     	Double[] largestPagerank = new Double[numOfMoves];
	     	for (int i = 0;i<numOfMoves;i++)
	     		largestPagerank[i] = (Double) 0.0;
	     	Vertex [] largestPagerankVertices = new Vertex[numOfMoves];
			for (Vertex v : pagerank.keySet()) {
		       	Double val = new Double(pagerank.get(v).doubleValue());
		        
		       	for (int i = 0; i < numOfMoves; i++){
		       		if (val > largestPagerank[i]){
		       			for (int j = numOfMoves-1; j > i ; j--) {
		       				largestPagerank[j] = largestPagerank[j-1];
		       				largestPagerankVertices[j] = largestPagerankVertices[j-1];
		       			}
		       			largestPagerank[i] = val;
		       			largestPagerankVertices[i] = v;
			       		break;
		       		}
		       	}
			} 		
			Move m = new Move();
			for (int i = 0; i < numOfMoves; i++){
//				m.putVertex(largestPagerankVertices[i], 1);
				m.putVertex(largestPagerankVertices[i], largestPagerank[i]);
			}
			
	        //System.out.println("BarabasiPlayer EXECUTION TIME(ms): " + ((long) System.currentTimeMillis()-startTime));
			this.movePtr.set(m);
			
		}
		//TODO: create else for random graphs
	}
	
	private Vertex chooseVertex(Vertex source, Vertex previous, Vertex circleCenter) {
		Set<Edge> edges = source.getOutEdges();
		for (Edge e: edges) {
			if ((e.getTarget() != previous) && (e.getTarget() != circleCenter) && (hasEdge(e.getTarget(),circleCenter)))
				return e.getTarget();
		}
		return null;
	}
	
	private boolean hasEdge(Vertex source, Vertex target) {
		for (Edge e: source.getOutEdges()) {
			if (e.getTarget() == target)
				return true;
		}
		return false;
	}

}
