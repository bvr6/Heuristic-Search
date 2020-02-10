
package searchclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

public class SearchClient
{
    public static State parseLevel(BufferedReader serverMessages)
    throws IOException
    {
        // We can assume that the level file is conforming to specification, since the server verifies this.
        // Read domain.
        serverMessages.readLine(); // #domain
        serverMessages.readLine(); // hospital

        // Read Level name.
        serverMessages.readLine(); // #levelname
        serverMessages.readLine(); // <name>

        // Read colors.
        serverMessages.readLine(); // #colors
   
        //set marker
        serverMessages.mark(20);       
        String line = serverMessages.readLine();
        
        int ac_init = 0;
        int bc_init = 0;
        
        while (!line.startsWith("#"))
        {
            String[] split = line.split(":");
            Color color = Color.fromString(split[0].strip());
            String[] entities = split[1].split(",");
            
            for (String entity : entities)
            {
                char c = entity.strip().charAt(0);
                if ('0' <= c && c <= '9')
                {
                	ac_init++;
                }
                else if ('A' <= c && c <= 'Z')
                {
                	bc_init++;
                }
            }
            line = serverMessages.readLine();
        }
        
        
       serverMessages.reset();
       line = serverMessages.readLine();
       
       //init color arrays
       Color[] agentColors = new Color[ac_init];
       Color[] boxColors = new Color[bc_init];
        
        
        
        while (!line.startsWith("#"))
        {
            String[] split = line.split(":");
            Color color = Color.fromString(split[0].strip());
            String[] entities = split[1].split(",");
            
            for (String entity : entities)
            {
                char c = entity.strip().charAt(0);
                if ('0' <= c && c <= '9')
                {
                    agentColors[c - '0'] = color;
                    //System.err.println(color);
                }
                else if ('A' <= c && c <= 'Z')
                {
                    boxColors[c - 'A'] = color;
                    //System.err.println(color);
                }
            }
            line = serverMessages.readLine();
        }
        

        // Read initial state.
        // line is currently "#initial".
        int numAgents = 0;
        int[] agentRows = new int[10];
        int[] agentCols = new int[10];
        
        //set bufferedreader marker
        serverMessages.mark(130);
        line = serverMessages.readLine();
        int row = 0;
        
        //find rows and columns
        int row_init = 0;
        int col_init = line.length();
        
        while (!line.startsWith("#"))
        {
        	if (line.length()>col_init) {
        		col_init = line.length();
        	}
            ++row_init;
            line = serverMessages.readLine();
        }
        
        System.out.println("row: " + row_init + " col: " + col_init); 
        
        //init arrays
        boolean[][] walls = new boolean[row_init][col_init];
        char[][] boxes = new char[row_init][col_init];
        
        //set back to beginning line
        serverMessages.reset();
        line = serverMessages.readLine();
        
        
        while (!line.startsWith("#"))
        {
            for (int col = 0; col < line.length(); ++col)
            {
                char c = line.charAt(col);

                if ('0' <= c && c <= '9')
                {
                    agentRows[c - '0'] = row;
                    agentCols[c - '0'] = col;
                    ++numAgents;
                }
                else if ('A' <= c && c <= 'Z')
                {
                    boxes[row][col] = c;
                }
                else if (c == '+')
                {
                    walls[row][col] = true;
                }
            }

            ++row;
            line = serverMessages.readLine();
        }
        agentRows = Arrays.copyOf(agentRows, numAgents);
        agentCols = Arrays.copyOf(agentCols, numAgents);
        

        // Read goal state.
        // line is currently "#goal".
        char[][] goals = new char[130][130];
        line = serverMessages.readLine();
        row = 0;
        int col = 0;
        while (!line.startsWith("#"))
        {
            for (col = 0; col < line.length(); ++col)
            {
                char c = line.charAt(col);

                if (('0' <= c && c <= '9') || ('A' <= c && c <= 'Z'))
                {
                    goals[row][col] = c;
                }
            }

            ++row;
            line = serverMessages.readLine();
        }

        // End.
        // line is currently "#end".
        

        return new State(agentRows, agentCols, agentColors, walls, boxes, boxColors, goals);
        
    }

    /**
     * Implements the Graph-Search algorithm from R&N figure 3.7.
     */
    public static Action[][] search(State initialState, Frontier frontier)
    {
        long startTime = System.nanoTime();
        int iterations = 0;

        System.err.format("Starting %s.\n", frontier.getName());

        frontier.add(initialState);
        HashSet<State> explored = new HashSet<>(65536);

        while (true)
        {
            if (iterations == 10000)
            {
                printSearchStatus(startTime, explored, frontier);
                iterations = 0;
            }

            if (frontier.isEmpty())
            {
                printSearchStatus(startTime, explored, frontier);
                return null;
            }

            State leafState = frontier.pop();

            if (leafState.isGoalState())
            {
                printSearchStatus(startTime, explored, frontier);
                return leafState.extractPlan();
            }

            explored.add(leafState);
            for (State s : leafState.getExpandedStates())
            {
                if (!explored.contains(s) && !frontier.contains(s))
                {
                    frontier.add(s);
                }
            }

            ++iterations;
        }
    }

    private static void printSearchStatus(long startTime, HashSet<State> explored, Frontier frontier)
    {
        String statusTemplate = "#Explored: %,8d, #Frontier: %,8d, #Generated: %,8d, Time: %3.3f s\n%s\n";
        double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000d;
        System.err.format(statusTemplate, explored.size(), frontier.size(), explored.size() + frontier.size(),
                          elapsedTime, Memory.stringRep());
    }

    public static void main(String[] args)
    throws IOException
    {
        // Use stderr to print to the console.
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        // Send client name to server.
        System.out.println("SearchClient");

        // We can also print comments to stdout by prefixing with a #.
        System.out.println("#This is a comment.");

        // Parse the level.
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.US_ASCII));
        State initialState = SearchClient.parseLevel(serverMessages);
        
        
        
        
        
        
        
        DistanceMap test = new DistanceMap(1, 1);
        test.printDist();
        
        int row = State.walls.length;
    	int col =  State.walls[0].length;
    	
    	DistanceMap[][] distanceMapArray = new DistanceMap[row][col];

        
        ArrayList<Triplet> boxList = new ArrayList<Triplet>();
    	ArrayList<Triplet> goalList= new ArrayList<Triplet>();
    	ArrayList<BoxPair> boxPairList = new ArrayList<BoxPair>();
    	
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
    		boxPairList.add(new BoxPair(box.getColNum(), box.getRowNum(), box.getChar(), curDis, distanceMapArray[box.getColNum()][box.getRowNum()]));
            System.err.println(boxList.toString());
            System.err.println(goalList);
    		boxList.remove(box);
    		goalList.remove(goal);
    	}
    	
        
        
        
        
        
        // Select search strategy.
        Frontier frontier;
        if (args.length > 0)
        {
            switch (args[0].toLowerCase(Locale.ROOT))
            {
                case "-bfs":
                    frontier = new FrontierBFS();
                    break;
                case "-dfs":
                    frontier = new FrontierDFS();
                    break;
                case "-astar":
                    frontier = new FrontierBestFirst(new HeuristicAStar(initialState));
                    break;
                case "-wastar":
                    int w = 5;
                    if (args.length > 1)
                    {
                        try
                        {
                            w = Integer.parseUnsignedInt(args[1]);
                        }
                        catch (NumberFormatException e)
                        {
                            System.err.println("Couldn't parse weight argument to -wastar as integer, using default.");
                        }
                    }
                    frontier = new FrontierBestFirst(new HeuristicWeightedAStar(initialState, w));
                    break;
                case "-greedy":
                    frontier = new FrontierBestFirst(new HeuristicGreedy(initialState));
                    break;
                default:
                    frontier = new FrontierBFS();
                    System.err.println("Defaulting to BFS search. Use arguments -bfs, -dfs, -astar, -wastar, or " +
                                       "-greedy to set the search strategy.");
            }
        }
        else
        {
            frontier = new FrontierBFS();
            System.err.println("Defaulting to BFS search. Use arguments -bfs, -dfs, -astar, -wastar, or -greedy to " +
                               "set the search strategy.");
        }

        // Search for a plan.
        Action[][] plan;
        try
        {
            plan = SearchClient.search(initialState, frontier);
        }
        catch (OutOfMemoryError ex)
        {
            System.err.println("Maximum memory usage exceeded.");
            plan = null;
        }

        // Print plan to server.
        if (plan == null)
        {
            System.err.println("Unable to solve level.");
            System.exit(0);
        }
        else
        {
            System.err.format("Found solution of length %d.\n", plan.length);

            for (Action[] jointAction : plan)
            {
                System.out.print(jointAction[0].name);
                for (int action = 1; action < jointAction.length; ++action)
                {
                    System.out.print(";");
                    System.out.print(jointAction[action].name);
                }
                System.out.println();
                // We must read the server's response to not fill up the stdin buffer and block the server.
                serverMessages.readLine();
            }
        }
    }
    
}
