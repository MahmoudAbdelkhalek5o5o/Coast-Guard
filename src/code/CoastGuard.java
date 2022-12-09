package code;

import java.lang.Math;
import java.util.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class CoastGuard extends SearchProblem{
    static int grid_min = 2;
    static int grid_max = 2;
    static int agent_min = 30;
    static int agent_max = 100;
    static int passenger_min = 1; //why not 0 as in the description?
    static int passenger_max = 10;
    static int black_box_life = 20;
    Queue<StateNode> searchQueue;
	Stack<StateNode> searchStack;
    Map <String, ArrayList<StateNode>> isVisitedDict = new Hashtable<String, ArrayList<StateNode>>();
    int initialPassengers;
    int expandedNodes;
    static int width;
    static int height;
    static ArrayList<int[]>ships_positions;
    static ArrayList<int[]>stations_positions;
    static OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    static double cpu = bean.getProcessCpuLoad();
    static boolean cpu_read_once_flag = true;
    public CoastGuard(Object [] info){
    	Cell [][]initialGrid = ((Cell [][])(info[0]));
    	Agent agent = ((Agent)(info[1]));
        this.operators = new String[]{"up","down","right","left","pickup","drop","retrieve"};
        searchQueue = new LinkedList<StateNode>();
		searchStack = new Stack<StateNode>();
        this.initial_state = new StateNode(initialGrid,agent, null,null,0,0);
        searchQueue.add(initial_state);
		searchStack.push(initial_state);
        insertInsideVisitedIfNotThere(agent.getAgentInfoString(),initial_state);
        height = initialGrid.length;
        width = initialGrid[0].length;
        // pass grid copy as input
        //do search queue here and the visited DS
        //how to check if we visited some node before? implement equals for grid?
        //make each state node check if it has passed its specific goal
        //if yes initialize the queue again and reset the visited DS
        this.state_space = new StateNode[2*initialGrid.length];
        
        // initialize problem variables
        initialPassengers = countPassengers(initial_state);
        
        expandedNodes = 1;
    }
    //takes a node and the agent position and inserts the Statenode inside the arraylist of statenodes at that position if possible
    //the method return true if the node has been visited before, false otherwise
    
    public static int countPassengers(StateNode node) {
    	Cell [][] grid = node.getGrid();
    	int count = 0;
    	for (int i = 0; i < grid.length; i++) {
    		for (int j = 0; j < grid[i].length; j++) {
    			if(grid[i][j] instanceof Ship) {
    				count = count + ((Ship) grid[i][j]).getNoOfPassengers();
    			}
    		}
		}
    	return count;
    }
    public boolean insertInsideVisitedIfNotThere(String pos, StateNode node) {
    	ArrayList<StateNode> nodes = isVisitedDict.get(pos);
    	if(nodes ==null) {
    		
    		nodes = new ArrayList<StateNode>();
    		nodes.add(node);
    		isVisitedDict.put(pos, nodes);
    	}
    	else {
    		for(int i=0; i<nodes.size();i++) {
        		if(node.sameAs(nodes.get(i))) {
        			return true;
        		}
        	}
        	nodes.add(node);
        	isVisitedDict.put(pos, nodes);
    	}
    	return false;	
    }
    


	public static  int GenerateRandomNumber( int max , int min ){
        return (int)(Math.random()*(max-min+1)+min);
    }
    public boolean goal_test(StateNode node){
        return true;
    }
    public static String genGrid(){
        // Initialize The size of the grid
        int m = GenerateRandomNumber( grid_max , grid_min );
        int n = GenerateRandomNumber( grid_max , grid_min );
        int cells_number = m * n;
        int [][] grid = new int [m][n];
        System.out.println("Cells Number: "+ cells_number+ " with dimenstions "+ m+" x "+ n);

        // Generate the capacity of coast guard ship
        int agent_capacity = GenerateRandomNumber( agent_max , agent_min );
        System.out.println("agent_capacity: "+ agent_capacity);

        // Generate the location of the coast guard ship
        int agent_row_loc = GenerateRandomNumber( m-1 , 0 );
        int agent_col_loc = GenerateRandomNumber( n-1 , 0 );
        grid[agent_row_loc][agent_col_loc]= -1;
        System.out.println("Agent_position: "+ agent_row_loc +" "+ agent_col_loc);
        // Generate the number of the ships at least one ship, at most cells-2 ships leaving 2 places for a station and agent
        int ship_number = GenerateRandomNumber( cells_number-2 , 1 );
        System.out.println("ship_number: "+ ship_number);
        // Generate the number of the stations ( The remaining number after ships and agent), at least 1 station, at most cells-2
        //leaving 2 places, one for the ship and the other for the agent
        int remain_locations = cells_number - ship_number-1 ;
        int station_number = GenerateRandomNumber( remain_locations , 1 );
        System.out.println("station_number: "+ station_number);
        
        // assigning ships to positions
        for ( int i = 0; i < ship_number; i++){
            int passenger_number = GenerateRandomNumber( passenger_max , passenger_min );
            boolean found_empty_cell = false;
            while (!found_empty_cell) {
                int row = GenerateRandomNumber(m - 1, 0);
                int col = GenerateRandomNumber(n - 1, 0);
                if ( grid[row][col] == 0){
                    grid[row][col] = passenger_number;
                    found_empty_cell= true;
                }
            }

        }
        
        // assigning stations to positions
        for ( int i = 0; i < station_number; i++){
            boolean found_empty_cell = false;
            while (!found_empty_cell) {
                int row = GenerateRandomNumber(m - 1, 0);
                int col = GenerateRandomNumber(n - 1, 0);
                if ( grid[row][col] == 0){
                    grid[row][col] = -55; //troll //yes very troll
                    found_empty_cell = true;
                }
            }

        }
        // XXX I changed the variables names to be more clear
        String str_agent = m+","+n+";"+ agent_capacity + ";" + agent_row_loc + "," + agent_col_loc + ";"; 
        String str_stations = "";
        String str_ships = "";
        for ( int i = 0; i<m; i++){
            for ( int j = 0; j<n; j++){
                if ( grid[i][j] > 0){
                	str_ships += i+"," +j+ "," + grid[i][j] + "," ;
                }
                else if ( grid[i][j] == -55 ){
                	str_stations += i + "," + j + "," ;
                }
            }
        }
        str_stations = str_stations.substring(0,str_stations.length()-1)+";";
        str_ships = str_ships.substring(0,str_ships.length()-1)+";";
        String result = str_agent + str_stations + str_ships;
        System.out.println(result);
        for (int [] row: grid) {
            for (int col: row) {
            	
            	String col_string = pad_string(col);
                System.out.print(col_string + "|");
            }
            System.out.println("");
        }
        return result;
    }

    //pads the cell value to have spaces around it so that all cells occupy the same size, making the map look cleaner
    private static String pad_string(int col) {
    	String padded="";
    	switch((""+col).length()) {
    	case 1:
    		padded = "  "+ col+"  ";
    		break;
    	case 2:
    		padded = " "+ col+"  ";
    		break;
    	case 3:
    		padded = " "+ col+" ";
    	}
		return padded;
	}
    
    private static String pad_string(String str) {
    	String padded="";
    	switch((str).length()) {
    	case 1:
    		padded = "  "+ str+"  ";
    		break;
    	case 2:
    		padded = " "+ str+"  ";
    		break;
    	case 3:
    		padded = " "+ str+" ";
    		break;
    	case 4:
    		padded = ""+ str+" ";
    		break;
    	}
		return padded;
	}
	public static Object[] instantiateGrid(String grid_string){
        String [] grid_split = grid_string.split(";");
        // Initiate grid dimensions
        String [] dimensions = grid_split[0].split(",");
        //XXX changed the assignment of the width and height to match the project desctiption
        Cell [][] grid = new Cell[Integer.parseInt(dimensions[1])][Integer.parseInt(dimensions[0])];


        // creating and localizing agent
        int agent_capacity = Integer.parseInt(grid_split[1]);
        String [] agent_position = grid_split[2].split(",");
        Agent agent = new Agent(Integer.parseInt(agent_position[0]), Integer.parseInt(agent_position[1]), agent_capacity);


        // creating and localizing Stations
        String [] stations_position_string = grid_split[3].split(",");
        //XXX changed the condition by -1
        stations_positions = new ArrayList<int[]>();
        for(int i =0;i<stations_position_string.length-1;i=i+2){
//        	System.out.println(stations_position_string[i+1]);
            grid[Integer.parseInt(stations_position_string[i])][Integer.parseInt(stations_position_string[i+1])] =
                    new Station(Integer.parseInt(stations_position_string[i]), Integer.parseInt(stations_position_string[i+1]));
            stations_positions.add(new int[] {Integer.parseInt(stations_position_string[i]), Integer.parseInt(stations_position_string[i+1])});
        }
        // creating and localizing ships
        String [] ships_position_string = grid_split[4].split(",");
        ships_positions = new ArrayList<int[]>();
        //XXX changed the condition by -2
        for(int i =0;i<ships_position_string.length-2;i=i+3){
            grid[Integer.parseInt(ships_position_string[i])][Integer.parseInt(ships_position_string[i+1])] =
                    new Ship(Integer.parseInt(ships_position_string[i]), Integer.parseInt(ships_position_string[i+1]), Integer.parseInt(ships_position_string[i+2]));
            ships_positions.add(new int[] {Integer.parseInt(ships_position_string[i]), Integer.parseInt(ships_position_string[i+1])});
        }
        
        // Initializing Empty cells
        for(int i=0;i<grid.length;i++){
            for(int j=0;j<grid[i].length;j++){
                if(grid[i][j]==null)grid[i][j] = new Cell(i,j);
            }
        }
        // print
//        System.out.println("------------------------------------------------------------------");
        

        return new Object [] {grid,agent};
    }
	public static void visualizeGrid(Cell [][] grid, Agent agent) {
		int col_index=0;
		System.out.print(pad_string(-1)+ "|");
		for(int row_index=0;row_index<grid[0].length;row_index++){
			System.out.print(pad_string(row_index)+"|");
		}
        System.out.println();

		for(int i=0;i<grid.length;i++){

			System.out.print(pad_string(col_index++)+"|");
            for(int j=0;j<grid[i].length;j++){
            	if(agent.getI()== i && agent.getJ() ==j){
            		if(grid[i][j] instanceof Ship) {
            			System.out.print(pad_string(((Ship)(grid[i][j])).getNoOfPassengers()+"*") + "|");
            		}
            		else {
            			System.out.print(pad_string(grid[i][j] + "*")+"|");
            		}
            	}
            	else{
            		if(grid[i][j] instanceof Ship) {
            			System.out.print(pad_string(((Ship)(grid[i][j])).getNoOfPassengers()) + "|");
            		}
            		else {
            			System.out.print(pad_string(grid[i][j]+"")+"|");
            		}	
            	}
            }
            System.out.println();
        }
//		System.out.println("-----------------------------------------------------------------");
	}
	public static void visualizeGrid(StateNode state) {
		Cell[][] grid = state.getGrid();
		Agent agent = state.getAgent();
		int col_index=0;

		System.out.print(pad_string(-1)+ "|");
		for(int row_index=0;row_index<grid[0].length;row_index++){

			System.out.print(pad_string(row_index )+"|");
		}
        System.out.println();

		for(int i=0;i<grid.length;i++){

			System.out.print(pad_string(col_index++)+"|");
            for(int j=0;j<grid[i].length;j++){
            	if(agent.getI()== i && agent.getJ() ==j){
            		if(grid[i][j] instanceof Ship ) {
            			Ship curr_ship = ((Ship)grid[i][j]);
            			if(curr_ship.getNoOfPassengers()>0)
            				System.out.print(pad_string(curr_ship.getNoOfPassengers()+"*") + "|");
            			else
            				System.out.print(pad_string(-curr_ship.getBlackBoxDamage()+"*") + "|");
            		}
            		else {
            			System.out.print(pad_string(grid[i][j] + "*")+"|");
            		}
            		
            	}
            	else{
            		if(grid[i][j] instanceof Ship ) {
            			Ship curr_ship = ((Ship)grid[i][j]);
            			if(curr_ship.getNoOfPassengers()>0)
            				System.out.print(pad_string(curr_ship.getNoOfPassengers()+"") + "|");
            			else
            				System.out.print(pad_string(-curr_ship.getBlackBoxDamage()+"") + "|");
            		}
            		else {
            			System.out.print(pad_string(grid[i][j]+"")+"|");
            		}
            	}
            }
            System.out.println();
        }
		System.out.println("--------------------"+"depth:"+ state.depth+", cost:"+ state.path_cost+
				",operator: " + state.operator + ",capacity:"+ agent.getRemainingCapacity()+ "--------------------");
	}
	public void printQueue() {
    	System.out.println(searchQueue.size());
    }
	public void printStack() {
		System.out.println(searchStack.size());
	}

	public static void visualizePlan(StateNode node){
		if(node!=null){
			visualizePlan(node.parent);
			visualizeGrid(node);
		}
	}
    public static String solve(String grid_string, String strategy, boolean visualize){
        CoastGuard problem = new CoastGuard(instantiateGrid(grid_string));
		String result = "";
        switch(strategy){
            case "BF": result = problem.solveBFS(visualize);
                break;
            case "DF": result = problem.solveDFS(visualize);
                break;
            case "ID": result = problem.solveID(visualize);
                break;
            case "GR1": result = problem.solveGR1(visualize);
                break;
            case "GR2": result = problem.solveGR2(visualize);
            break;
            case "AS1": result = problem.solveAS1(visualize);
                break;
            case "AS2": result = problem.solveAS2(visualize);
            break;
        }
        return result.substring(1,result.length())+";"+ countRetrieve(result) +";"+problem.expandedNodes;
    }
    
    public String solveBFS(boolean visualize) {
		String result = "";
    	while(!searchQueue.isEmpty()) {

    		StateNode peek = searchQueue.remove();
    		if(peek.isGoal()) {
				result = peek.printPath("");
				int deaths = initialPassengers - peek.getAgent().getSavedPassengers();
				result = result + ";" + deaths;
				if(visualize){
					visualizePlan(peek);
				}
    			break;
    		}
    		else {
    			ArrayList<StateNode> nextNodes = getNextStates(peek);
    			for (int i = 0; i < nextNodes.size(); i++) {
    				if(!insertInsideVisitedIfNotThere(nextNodes.get(i).getAgent().getAgentInfoString(), nextNodes.get(i))) {
    					searchQueue.add(nextNodes.get(i));
    					expandedNodes++;
    				}
				}
    		}
    	}
		return result;
    }

	public String solveDFS(boolean visualize) {
		String result = "";
		while(!searchStack.isEmpty()) {
			StateNode peek = searchStack.pop();
			if(peek.isGoal()) {
				result = peek.printPath("");
				int deaths = initialPassengers - peek.getAgent().getSavedPassengers();
				result = result + ";" + deaths;
				if(visualize){
					visualizePlan(peek);
				}
				break;
			}
			else {
				ArrayList<StateNode> nextNodes = getNextStates(peek);
				for (int i = 0; i < nextNodes.size(); i++) {
					if(!insertInsideVisitedIfNotThere(nextNodes.get(i).getAgent().getAgentInfoString(), nextNodes.get(i))) {
						searchStack.push(nextNodes.get(i));
						expandedNodes++;
    				}
				}
			}
		}
		return result;
	}
	
	public String solveID(boolean visualize){
		return solveIDHelper(0,visualize);
	}
	public String solveIDHelper(int iterative, boolean visualize) {

		String result = "";
		if (searchStack.isEmpty())
			searchStack.push(initial_state);
		while(!searchStack.isEmpty()) {
			StateNode peek = searchStack.pop();
			if(peek.isGoal()) {
				result = peek.printPath("");
				int deaths = initialPassengers - peek.getAgent().getSavedPassengers();
				result = result + ";" + deaths;
				if(visualize){
					visualizePlan(peek);
				}
				return  result;
			}
			else {
				if ( (iterative - peek.depth) >= 0){
					ArrayList<StateNode> nextNodes = getNextStates(peek);
					for (int i = 0; i < nextNodes.size(); i++) {
						if(!insertInsideVisitedIfNotThere(nextNodes.get(i).getAgent().getAgentInfoString(), nextNodes.get(i))) {
							searchStack.push(nextNodes.get(i));
							expandedNodes++;
						}
					}
				}

			}
		}

		isVisitedDict.clear();
		return solveIDHelper(++ iterative, visualize);
	}
	public String solveGR1(boolean visualize) {
		String result = "";
		PriorityQueue<StateNode> priorityQueue = new
	             PriorityQueue<StateNode>(new StateNodeComparator(ships_positions, stations_positions,"GR1"));
		priorityQueue.add(initial_state);
    	while(!priorityQueue.isEmpty()) {
    		
    		StateNode peek = priorityQueue.remove();
    		if(peek.isGoal()) {
				result = peek.printPath("");
				int deaths = initialPassengers - peek.getAgent().getSavedPassengers();
				result = result + ";" + deaths;
				if(visualize){
					visualizePlan(peek);
				}
    			break;
    		}
    		else {
    			ArrayList<StateNode> nextNodes = getNextStates(peek);
    			for (int i = 0; i < nextNodes.size(); i++) {
    				if(nextNodes.get(i).getOperator().equals("retrieve")  && !nextNodes.get(i).shipsWrecked(ships_positions))
    					continue;
    				if(!insertInsideVisitedIfNotThere(nextNodes.get(i).getAgent().getAgentInfoString(), nextNodes.get(i))) {
    					priorityQueue.add(nextNodes.get(i));
    					expandedNodes++;
    				}
				}
    		}
    	}
		return result;
    }
	
	public String solveGR2(boolean visualize) {
		String result = "";
		PriorityQueue<StateNode> priorityQueue = new
	             PriorityQueue<StateNode>(new StateNodeComparator(ships_positions, stations_positions,"GR2"));
		priorityQueue.add(initial_state);
    	while(!priorityQueue.isEmpty()) {
    		
    		StateNode peek = priorityQueue.remove();
    		if(peek.isGoal()) {
				result = peek.printPath("");
				int deaths = initialPassengers - peek.getAgent().getSavedPassengers();
				result = result + ";" + deaths;
				if(visualize){
					visualizePlan(peek);
				}
    			break;
    		}
    		else {
    			ArrayList<StateNode> nextNodes = getNextStates(peek);
    			for (int i = 0; i < nextNodes.size(); i++) {
    				if(nextNodes.get(i).getOperator().equals("retrieve")  && !nextNodes.get(i).shipsWrecked(ships_positions))
    					continue;
    				if(!insertInsideVisitedIfNotThere(nextNodes.get(i).getAgent().getAgentInfoString(), nextNodes.get(i))) {
    					priorityQueue.add(nextNodes.get(i));
    					expandedNodes++;
    				}
				}
    		}
    	}
		return result;
    }
	public String solveAS1(boolean visualize) {
		String result = "";
		PriorityQueue<StateNode> priorityQueue = new
	             PriorityQueue<StateNode>(new StateNodeComparator(ships_positions, stations_positions,"AS1"));
		priorityQueue.add(initial_state);
    	while(!priorityQueue.isEmpty()) {
    		
    		StateNode peek = priorityQueue.remove();
    		if(peek.isGoal()) {
				result = peek.printPath("");
				int deaths = initialPassengers - peek.getAgent().getSavedPassengers();
				result = result + ";" + deaths;
				if(visualize){
					visualizePlan(peek);
				}
    			break;
    		}
    		else {
    			ArrayList<StateNode> nextNodes = getNextStates(peek);
    			for (int i = 0; i < nextNodes.size(); i++) {
    				if(nextNodes.get(i).getOperator().equals("retrieve")  && !nextNodes.get(i).shipsWrecked(ships_positions))
    					continue;
    				if(!insertInsideVisitedIfNotThere(nextNodes.get(i).getAgent().getAgentInfoString(), nextNodes.get(i))) {
    					priorityQueue.add(nextNodes.get(i));
    					expandedNodes++;
    				}
				}
    		}
    	}
		return result;
    }
	
	public String solveAS2(boolean visualize) {
		String result = "";
		PriorityQueue<StateNode> priorityQueue = new
	             PriorityQueue<StateNode>(new StateNodeComparator(ships_positions, stations_positions,"AS2"));
		priorityQueue.add(initial_state);
    	while(!priorityQueue.isEmpty()) {
    		
    		StateNode peek = priorityQueue.remove();
    		if(peek.isGoal()) {
				result = peek.printPath("");
				int deaths = initialPassengers - peek.getAgent().getSavedPassengers();
				result = result + ";" + deaths;
				if(visualize){
					visualizePlan(peek);
				}
    			break;
    		}
    		else {
    			ArrayList<StateNode> nextNodes = getNextStates(peek);
    			for (int i = 0; i < nextNodes.size(); i++) {
    				if(nextNodes.get(i).getOperator().equals("retrieve")  && !nextNodes.get(i).shipsWrecked(ships_positions))
    					continue;
    				if(!insertInsideVisitedIfNotThere(nextNodes.get(i).getAgent().getAgentInfoString(), nextNodes.get(i))) {
    					priorityQueue.add(nextNodes.get(i));
    					expandedNodes++;
    				}
				}
    		}
    	}
		return result;
    }
	
    
    public static boolean repeatedAncestorsAgentPositions(StateNode node, int i, int j) {
    	if (node.parent == null) {
    		return false;
    	}

    	else if (node.operator=="up" || node.operator=="right" || node.operator=="left" || node.operator=="down"){
    		StateNode parent = node.parent;
    		if(parent.agent.getI()==i && parent.agent.getJ() == j) {
    			return true;
    		}
    		else {
    			return repeatedAncestorsAgentPositions(parent,i,j);
    		}
    	}
    	else {
    		return false;
    	}
    }
    
    public static ArrayList<StateNode> getNextStates(StateNode parent) {
    	ArrayList<StateNode> neighbors= new ArrayList<StateNode>();
    	if(cpu_read_once_flag) {
	    	while( cpu <= 0){
				cpu = bean.getProcessCpuLoad();
			}
	    	cpu_read_once_flag = false;
    	}
    	// update parent state
    	Agent agent = parent.agent;
    	Cell[][] newGrid= generalUpdateState(cloneGrid(parent.getGrid()));
    	int pathCostAddition = getPathCostDiff(parent.getGrid());//calculates the number of people who'd have normally died anyway
    	// moving down node
    	if(agent.getI()<height-1 && !repeatedAncestorsAgentPositions(parent, agent.getI() +1, agent.getJ())) {
    		Agent newAgent = agent.copyAgentWithModification(agent.getI() +1, agent.getJ());
    		neighbors.add(new StateNode(newGrid,newAgent,parent,"down",parent.depth+1,parent.path_cost+pathCostAddition));
    	}
    	// moving up node
    	if(agent.getI()>0 && !repeatedAncestorsAgentPositions(parent, agent.getI() - 1, agent.getJ())) {
    		Agent newAgent = agent.copyAgentWithModification(agent.getI() -1, agent.getJ());
    		neighbors.add(new StateNode(newGrid,newAgent,parent,"up",parent.depth+1,parent.path_cost+pathCostAddition));
    	}
    	// moving right node
    	if(agent.getJ()<width-1 && !repeatedAncestorsAgentPositions(parent, agent.getI(), agent.getJ() +1)) {
    		Agent newAgent = agent.copyAgentWithModification(agent.getI(), agent.getJ()+1);
    		neighbors.add(new StateNode(newGrid,newAgent,parent, "right",parent.depth+1,parent.path_cost+pathCostAddition));
    	}
    	// moving left node
    	if(agent.getJ()>0 && !repeatedAncestorsAgentPositions(parent, agent.getI(), agent.getJ() -1)) {
    		Agent newAgent = agent.copyAgentWithModification(agent.getI(), agent.getJ()-1);
    		neighbors.add(new StateNode(newGrid, newAgent, parent, "left",parent.depth+1,parent.path_cost+pathCostAddition));
    	}
    	Cell currentCell = parent.getGrid()[agent.getI()][agent.getJ()];
		// pickup
    	if(currentCell instanceof Ship && !((Ship)(currentCell)).isWreck() && agent.getRemainingCapacity()>0) {
    		Cell [][]pickupGrid = cloneGrid(parent.getGrid());
    		Ship ship = ((Ship)(pickupGrid[agent.getI()][agent.getJ()]));
    		int retrievablePeople= Math.min(agent.getRemainingCapacity(), ship.getNoOfPassengers());
			Agent newAgent = agent.clone();
			newAgent.setRemainingCapacity(newAgent.getRemainingCapacity()-retrievablePeople);
    		// update passengers on board the ship of the new Grid
    		ship.setNoOfPassengers(ship.getNoOfPassengers()-retrievablePeople);
    		newAgent.setSavedPassengers(newAgent.getSavedPassengers()+retrievablePeople);
    		pathCostAddition -= retrievablePeople*223 ;
        	neighbors.add(new StateNode(generalUpdateState(pickupGrid), newAgent , parent,"pickup",parent.depth+1,parent.path_cost+pathCostAddition));
    	}
		// retrieve
    	else if(currentCell instanceof Ship && ((Ship)(currentCell)).isWreck() && ((Ship)(currentCell)).hasBlackBox()) {
    		Cell [][]retrieveGrid = cloneGrid(parent.getGrid());
    		Ship ship = ((Ship)(retrieveGrid[agent.getI()][agent.getJ()]));
			Agent newAgent = agent.clone();
    		newAgent.setBlackBoxes(newAgent.getBlackBoxes()+1);
    		ship.setBlackBoxDamage(20);
    		pathCostAddition -=1;
    		// update the box on the ship
        	neighbors.add(new StateNode(generalUpdateState(retrieveGrid), newAgent , parent,"retrieve",parent.depth+1,parent.path_cost+pathCostAddition));
    	}
		// drop
    	if(currentCell instanceof Station && agent.getPassengersOnBoard()>0) {
    		Cell [][]dropGrid = cloneGrid(parent.getGrid());
			Agent newAgent = agent.clone();
    		newAgent.setPassengersOnBoard(0);
    		newAgent.setBlackBoxes(0);
        	neighbors.add(new StateNode(generalUpdateState(dropGrid), newAgent , parent,"drop",parent.depth+1,parent.path_cost+pathCostAddition));

    	}
    	return neighbors;
    }
	private static int getPathCostDiff(Cell[][] grid) {
    	//we use the people deaths as path cost
    	int deaths =0;
    	int blackBoxes = 0;
		for (int i = 0; i < ships_positions.size(); i++) {
			Ship ship = (Ship) grid[ships_positions.get(i)[0]][ships_positions.get(i)[1]];
			if(ship.getNoOfPassengers()>=1)
				deaths += 1;
			else if(ship.hasBlackBox()) {
				blackBoxes++;
			}
		}
		return deaths*223 + blackBoxes ;
	}

	public static Cell[][]cloneGrid(Cell[][]grid){
    	Cell [][] newGrid = new Cell [grid.length][grid[0].length];
		for (int i = 0; i < newGrid.length; i++) {
			for (int j = 0; j < newGrid[i].length; j++) {
				newGrid[i][j] = grid[i][j].clone();
			}
		}
		return newGrid;
    }
    public static Cell[][] generalUpdateState(Cell [][]parent) {
    	//passengers.blackbox_health,has_blackbox
    	// ships decrement no of passengers
    	for (int i = 0; i < ships_positions.size(); i++) {
    		int row = ships_positions.get(i)[0];
    		int col = ships_positions.get(i)[1];
    		Cell [][] newGrid = parent;
    		Ship ship= (Ship)(newGrid[row][col]);
    		ship.decrement();
		}
    	return parent;
    }
    public static int countRetrieve(String plan) {
    	String [] actions = plan.split(",");
    	int count = 0;
    	for (int i = 0; i < actions.length; i++) {
			if (actions[i].equals("retrieve")) count++;
		}
    	return count;
    }
    public static int countDeaths(String plan, StateNode initialState) {
    	String [] actions = plan.split(",");
    	int count = 0;
    	for (int i = 0; i < actions.length; i++) {
			
		}
    	return count;
    }
    public static void main(String[] args) {

    	String grid_str = "8,5;60;4,6;2,7;3,4,37,3,5,93,4,0,40;";
    	//String grid_str = "2,2;60;0,0;0,1;1,0,80,1,1,100";
    	//grid_str = genGrid();
	
		Runtime rt = Runtime.getRuntime();

		long old_used_mem = rt.totalMemory() - rt.freeMemory();
		long startTime = System.nanoTime();
		
		// SOLVE GET HERE
    	String res = solve(grid_str,"GR1",true);
    	System.out.println(res);
		
		long elapsedTime = System.nanoTime() - startTime;
		long new_used_mem = rt.totalMemory() - rt.freeMemory();
		System.out.println( "Total memory used:  " + ((new_used_mem - old_used_mem)/1000000) + " MegaByte");
		
		System.out.println("Cpu while the code is running: "+ ((cpu)*100) +"%");
		cpu = bean.getProcessCpuLoad();
		while( cpu <= 0){
			cpu = bean.getProcessCpuLoad();
		}
		System.out.println("Cpu usage after the code is finished: "+ ((cpu)*100) +"%");
		System.out.println("RunTime: "+ (elapsedTime/1000000.0) + " MilliSecond");
		
    	
    }
}