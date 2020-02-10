
package searchclient;

import java.util.Comparator;
import java.util.ArrayList;

public abstract class Heuristic
        implements Comparator<State>
{
	
	
	public DistanceMap[][] distanceMapArray;
	ArrayList<Triplet> goalList;
	public int row;
	public int col;

	public Heuristic(State initialState)
    {
        // Here's a chance to pre-process the static parts of the level.
    	
		row = State.walls.length;
    	col =  State.walls[0].length;
    	
    	//construct distance map
    	distanceMapArray = new DistanceMap[row][col];
    	
    	for (int i = 0; i < row; i++) {
    		for (int j = 0; j < col; j++) {
    			distanceMapArray[i][j] = new DistanceMap(i, j);
    		}
    	}
    	 	
    	//get list of goals
    	goalList= new ArrayList<Triplet>();
    	
    	//get a list of goals and boxes
    	for (int i = 0; i < row; i++) {
    		for (int j = 0; j < col; j++) {
    			if (State.goals[i][j] != 0) {
    				Triplet goal = new Triplet(State.goals[i][j], i, j);
    				goalList.add(goal);
    			}
    		}
    	}
    }
	
	
	
	

    public int h(State n)
    {	
    	//pair goals with boxes
    	ArrayList<Triplet> boxList = new ArrayList<Triplet>();
    	ArrayList<BoxPair> boxPairList = new ArrayList<BoxPair>();
    	
    	//get a list of goals and boxes
    	for (int i = 0; i < row; i++) {
    		for (int j = 0; j < col; j++) {
    			if (n.boxes[i][j] != 0) {
    				Triplet box = new Triplet(n.boxes[i][j], i, j);
    				boxList.add(box);
    			}
    		}
    	}
    	
    	//pair goals and boxes
    	for (int i = 0; i < goalList.size(); i++) {
    		Triplet box = null;
    		Triplet goal = goalList.get(i);
    		int curDis = Integer.MAX_VALUE;
    		for (int j = 0; j < boxList.size(); j++) {
    			if (goalList.get(i).c == boxList.get(j).c) {
    				if (curDis > distanceMapArray[goalList.get(i).getRowNum()][goalList.get(i).getColNum()].
    						distFromPoint(boxList.get(j).getRowNum(), boxList.get(j).getColNum())){
    					box = boxList.get(j);
    					curDis = distanceMapArray[goalList.get(i).getRowNum()][goalList.get(i).getColNum()].
        						distFromPoint(boxList.get(j).getRowNum(), boxList.get(j).getColNum());
    				}
    			}
    		}
    		boxPairList.add(new BoxPair(box.getColNum(), box.getRowNum(), 
    				box.getChar(), curDis, distanceMapArray[box.getColNum()][box.getRowNum()]));
    		boxList.remove(box);
    		goalList.remove(goal);
    	}	
    	
    	int disTotal = 0;
    	//calculate total distance
    	for (int i = 0; i < boxPairList.size(); i++) {
    		disTotal+= boxPairList.get(i).pairDistance();
    	}
    	
    	return disTotal;
    }
    	

    public abstract int f(State n);

    @Override
    public int compare(State n1, State n2)
    {
        return this.f(n1) - this.f(n2);
    }
}

class HeuristicAStar
        extends Heuristic
{
    public HeuristicAStar(State initialState)
    {
        super(initialState);
    }

    @Override
    public int f(State n)
    {
        return n.g() + this.h(n);
    }

    @Override
    public String toString()
    {
        return "A* evaluation";
    }
}

class HeuristicWeightedAStar
        extends Heuristic
{
    private int w;

    public HeuristicWeightedAStar(State initialState, int w)
    {
        super(initialState);
        this.w = w;
    }

    @Override
    public int f(State n)
    {
        return n.g() + this.w * this.h(n);
    }

    @Override
    public String toString()
    {
        return String.format("WA*(%d) evaluation", this.w);
    }
}

class HeuristicGreedy
        extends Heuristic
{
    public HeuristicGreedy(State initialState)
    {
        super(initialState);
    }

    @Override
    public int f(State n)
    {
        return this.h(n);
    }

    @Override
    public String toString()
    {
        return "greedy evaluation";
    }
}
