package code;

import java.util.ArrayList;

public class StateNode{
	Cell[][] grid;
	Agent agent;
	StateNode parent;
	String operator;
	int depth;
	int path_cost;
	float score;

	public StateNode(Cell[][] grid,Agent agent, StateNode parent, String operator, int depth, int path_cost) {
		this.grid = grid;
		this.agent = agent;
		this.parent = parent;
		this.operator = operator;
		this.depth = depth;
		this.path_cost = path_cost;
	}

	public int getPath_cost() {
		return path_cost;
	}

	public void setPath_cost(int path_cost) {
		this.path_cost = path_cost;
	}

	public boolean isGoal() {
    	// case 1
    	if(endGame()) {
    		printPath("");
    		System.out.println(agent.getBlackBoxes());
    	}
    	
    	return endGame();
    }
	public float calcMaxScore(ArrayList<int []> ships_position, ArrayList<int []> stations_position, String method) {
		float maxScore = 0; 
		for (int i = 0; i < ships_position.size(); i++) {
			float score = formula(ships_position.get(i)[0], ships_position.get(i)[1], method);
			if(score>maxScore)
				maxScore=score;
		}
		for (int i = 0; i < stations_position.size(); i++) {
			float score = formula(stations_position.get(i)[0], stations_position.get(i)[1], method);
			if(score>maxScore)
				maxScore=score;
		}
		return maxScore;
	}
	//this heuristic assumes we can do any operation without the need to move to the desired position. the coast guard agent
	// 	can pickup, retrieve and drop without moving at all. for simplicity, the agent can pickup all passengers of any
	// 	ship at once but he can't pick up more than one ship before dropping the passengers
		// the heuristic calculates the number of cruical actions(non movement actions) needed to reach the goal state
		
	public float calcMaxScoreGR2(ArrayList<int[]> ships_positions, ArrayList<int[]> stations_positions) {
		float maxScore = 0;
		ArrayList<Integer> ships_state = getShipsState(ships_positions);
		int pickups=0;
		int blackboxes=0;
		for (int i = 0; i < ships_state.size(); i++) {
			if (ships_state.get(i)==2) {
				pickups++;
				blackboxes++;
			}
			else if (ships_state.get(i)==1) {
				blackboxes++;
			}
		}
		//multiplied by 2 to account for the drop operation after each pickup, 
		// it last pickup is followed by retrieve, thus the drop operation of the pickup is delayed after
		// all available boxes are retrieved, and this is why we don't add drop operation for the retrieves if there are
		// pickups
		maxScore+= 2*pickups + blackboxes;
		
		if (pickups==0 && blackboxes >0) {
			maxScore++;
		}
		//if the agent has something to drop and no other operations are available to do we return 1 for the drop operation
		if (noShipHasBB(ships_state)&& agent.getBlackBoxes()!=0 || agent.getPassengersOnBoard()!=0) {
			maxScore+=1;
		}
		return maxScore;
	}
	//this heuristic assumes we have infinte capacity, but we can carry the passengers of only one ship at a time.
		//it tries to estimate the number of steps needed to achieve the goal by counting the steps from the agent 
		// 	position till the end goal going through all the hubs in between
		//if we had 2 ships the heuristic calculates the distance to the nearest ship for pickup + the sitance to the nearest
		// 	station to drop + the distance to the nearest ship to pickup + the distance to the nearest station to drop
		// 	+ the distance to the nearest blackboxes to retrieve + finally the distance to the nearest station to drop + 1
		//possible states:
			// looking for ship with passengers
			// looking to drop off passengers
			// looking for a ship with blackboxes
			// looking to drop off blackboxes
	public float calcMaxScoreGR1(ArrayList<int []> ships_positions, ArrayList<int []> stations_position) {
		float maxScore = 0; 
		int[] ship_pos=  nearestShipWithPassengers(ships_positions);
		String look_for= "";
		if(ship_pos != null) {
			if(agent.getRemainingCapacity()== agent.getMaxCapacity()) {
				// state 1
				look_for= "pick";
			}
			else {
				// state 2
				look_for= "drop";

			}
		}
		else {
			int[] BB_pos = nearestShipWithBBs(ships_positions);
			if(BB_pos != null) {
				// state 3
				look_for= "retrieve";

			}
			else {
				// state 4
				look_for= "drop";
			}
		}
		maxScore = planScore(look_for, ships_positions, stations_position);
		
		return maxScore;
	}
	private float planScore(String look_for, ArrayList<int[]> ships_positions, ArrayList<int[]> stations_position) {
		int plan_score= 1;
		ArrayList<Integer> ships_state = getShipsState(ships_positions);
		if(noShipHasBB(ships_state) ) {
			int[] nearest_station = nearestStation(stations_position);
			if(nearest_station[0]== agent.getI() && nearest_station[1] == agent.getJ() && agent.getBlackBoxes()==0) {
				return 0;
			}
		}
		int[] curr_pos = new int[] {agent.getI(),agent.getJ()};
		while(true) {
			if(look_for.equals("pick")) {
				int[] ship_pos = nearestShipWithPassengers(ships_positions, ships_state);
				if(ship_pos == null) {
					look_for = "retrieve";
					continue;
				}
				plan_score+= calcDistanceBetween(curr_pos[0], curr_pos[1], ship_pos[0], ship_pos[1], "man");
				curr_pos = ship_pos;
				look_for = "drop";
			}
			else if(look_for.equals("retrieve")) {
				int[] ship_pos = nearestShipWithBBs(ships_positions, ships_state);
				if(ship_pos == null) {
					look_for = "drop";
					continue;
				}
				plan_score+= calcDistanceBetween(curr_pos[0], curr_pos[1], ship_pos[0], ship_pos[1], "man");
				curr_pos = ship_pos;
				look_for = "drop";
			}
			else if(look_for.equals("drop")) {
				int[] station_pos = nearestStation(stations_position);
				plan_score+= calcDistanceBetween(curr_pos[0], curr_pos[1], station_pos[0], station_pos[1], "man");
				curr_pos = station_pos;
				if(noShipHasBB(ships_state)) {
					break;
					
				}
				

				look_for = "pick";
			}
			
		}
		return plan_score;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	private boolean noShipHasBB(ArrayList<Integer> ships_state) {
		for (int i = 0; i < ships_state.size(); i++) {
			if(ships_state.get(i) >0)
				return false;
		}
		return true;
	}
	
	private boolean allShipsWrecked(ArrayList<Integer> ships_state) {
		for (int i = 0; i < ships_state.size(); i++) {
			if(ships_state.get(i) >1)
				return false;
		}
		return true;
	}
	
	public boolean shipsWrecked(ArrayList<int[]> ships_positions) {
		return allShipsWrecked(getShipsState(ships_positions));
	}

	private int[] nearestStation(ArrayList<int[]> stations_position) {
		int[] res = null;
		double smallest_distance = 1000;// the grid maximum distance can't be more than 30 
		for(int i=0;i<stations_position.size();i++) {
			
			
			double distance = calcDistanceBetween(agent.getI(),agent.getJ(),stations_position.get(i)[0], stations_position.get(i)[1], "man");
			if(distance<=smallest_distance) {
				smallest_distance = distance;
				res = stations_position.get(i);
			}
			
			
		}
		return res;
	}

	private int[] nearestShipWithBBs(ArrayList<int[]> ships_position, ArrayList<Integer> ships_state) {
		int[] res = null;
		double smallest_distance = 1000;// the grid maximum distance can't be more than 30 
		int nearest_ship_index = -1;
		for(int i=0;i<ships_position.size();i++) {
			if(ships_state.get(i)!=1) {
				continue;
			}
			Ship ship = (Ship)grid[ships_position.get(i)[0]][ships_position.get(i)[1]];
			if(ship.hasBlackBox()) {
				double distance = calcDistanceBetween(agent.getI(),agent.getJ(),ships_position.get(i)[0], ships_position.get(i)[1], "man");
				if(distance<=smallest_distance) {
					smallest_distance = distance;
					nearest_ship_index = i;
					res = ships_position.get(i);
				}
			}
			
		}
		if(nearest_ship_index !=-1) {
			ships_state.remove(nearest_ship_index);
			ships_state.add(nearest_ship_index, 0);
		}
		return res;
	}

	private int[] nearestShipWithPassengers(ArrayList<int[]> ships_position, ArrayList<Integer> ships_state) {
		int[] res = null;
		double smallest_distance = 1000;// the grid maximum distance can't be more than 30 
		int nearest_ship_index = -1;
		for(int i=0;i<ships_position.size();i++) {
			if(ships_state.get(i)!=2) {
				continue;
			}
			Ship ship = (Ship)grid[ships_position.get(i)[0]][ships_position.get(i)[1]];
			if(ship.getNoOfPassengers()>0) {
				double distance = calcDistanceBetween(agent.getI(),agent.getJ(),ships_position.get(i)[0], ships_position.get(i)[1], "man");
				if(distance<=smallest_distance) {
					smallest_distance = distance;
					nearest_ship_index = i;
					res = ships_position.get(i);
				}
			}
			
		}
		if(nearest_ship_index !=-1) {
			ships_state.remove(nearest_ship_index);
			ships_state.add(nearest_ship_index, 1);
		}
		return res;
	}

	private ArrayList<Integer> getShipsState(ArrayList<int[]> ships_position) {
		// TODO Auto-generated method stub
		ArrayList<Integer> res= new ArrayList<Integer>(ships_position.size());
		for(int i=0; i<ships_position.size();i++) {
			Ship ship = (Ship)grid[ships_position.get(i)[0]][ships_position.get(i)[1]];
			if(ship.getNoOfPassengers()>0) {
				res.add(2);
			}
			else if(ship.hasBlackBox()) {
				res.add(1);
			}
			else {
				res.add(0);
			}
			
		}
		return res;
	}

	private double calcDistanceBetween(int fi,int fj,int ti, int tj, String method) {
		if(method.equals("euc")) {
			return Math.sqrt(Math.pow(ti-fi,2) + Math.pow(tj-fj,2));
		}
		else if(method.equals("man")){
			return Math.abs(ti-fi) + Math.abs(tj-fj);
		}
		return -1;
	}
	private int[] nearestShipWithBBs(ArrayList<int[]> ships_position) {
		int[] res = null;
		double smallest_distance = 1000;// the grid maximum distance can't be more than 30 
		for(int i=0;i<ships_position.size();i++) {
			Ship ship = (Ship)grid[ships_position.get(i)[0]][ships_position.get(i)[1]];
			if(ship.hasBlackBox()) {
				double distance = calcDistanceBetween(agent.getI(),agent.getJ(),ships_position.get(i)[0], ships_position.get(i)[1], "man");
				if(distance<=smallest_distance) {
					smallest_distance = distance;
					res = ships_position.get(i);
				}
			}
			
		}
		return res;
	}

	private int[] nearestShipWithPassengers(ArrayList<int[]> ships_position) {
		int[] res = null;
		double smallest_distance = 1000;// the grid maximum distance can't be more than 30 
		for(int i=0;i<ships_position.size();i++) {
			Ship ship = (Ship)grid[ships_position.get(i)[0]][ships_position.get(i)[1]];
			if(ship.getNoOfPassengers()>0) {
				double distance = calcDistanceBetween(agent.getI(),agent.getJ(),ships_position.get(i)[0], ships_position.get(i)[1], "man");
				if(distance<=smallest_distance) {
					smallest_distance = distance;
					res = ships_position.get(i);
				}
			}
			
		}
		return res;
	}

	public float formula(int i, int j, String method) {
		if (agent.getI()==1 && agent.getJ()==1 && operator=="retrieve")
			System.out.println("7oooda"+operator);
		
		double distance=0;
		if(method.equals("Man")) {
			distance = Math.abs(i-agent.getI()) + Math.abs(j-agent.getJ());
		}
		if(method.equals("Euc")) {
			distance = Math.sqrt(Math.pow(i-agent.getI(),2) + Math.pow(j-agent.getJ(),2));
		}
		int nominator=0;
		if(grid[i][j] instanceof Ship) {
			Ship ship = ((Ship)grid[i][j]);
			if (agent.getRemainingCapacity()>0) {
				nominator = 10000*ship.getNoOfPassengers()+100*(20-ship.getBlackBoxDamage())+ 10000*agent.getSavedPassengers()+ 10000*agent.getBlackBoxes();
			}
		}
		if(grid[i][j] instanceof Station) {
			nominator = agent.getBlackBoxes()+agent.getPassengersOnBoard();
			if (agent.getRemainingCapacity()==0) {
				nominator +=1000000;
			}
			
			if (agent.getI()==i && agent.getJ()==j) {
				nominator +=100000000;
			}
		}
		
		distance++;
		return (float)(nominator/distance);
	}
	
	public String printPath(String result) {
		if(this.parent!=null) {
			result = parent.printPath(result) + "," + operator;
			// System.out.print("," + operator);
		}
		else {
			System.out.println("-----------------------------------------");
			visualizeGrid();
		}
		return result;
	}
	public boolean endGame() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j] instanceof Ship) {
					Ship ship = (Ship) grid[i][j];
					if (!ship.isWreck() || ship.hasBlackBox()) {
						return false;
					}
				}
			}
		}
		return agent.getPassengersOnBoard()==0 && agent.getBlackBoxes()==0;
	}
	public StateNode clone() {
		//does this deep copy the grid or not?
		Cell [][] newGrid = new Cell [this.grid.length][this.grid[0].length];
		for (int i = 0; i < newGrid.length; i++) {
			for (int j = 0; j < newGrid[i].length; j++) {
				newGrid[i][j] = grid[i][j].clone();
			}
		}
		return new StateNode(
				newGrid,
				this.agent,
				this.parent,
				this.operator,
				this.depth,
				this.path_cost);
	}
	
	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Cell[][] getGrid() {
		return grid;
	}

	public void setGrid(Cell[][] grid) {
		this.grid = grid;
	}

	public void visualizeGrid() {
		Cell[][] grid = getGrid();
		Agent agent = getAgent();
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
		System.out.println("--------------------"+"depth:"+ depth+", cost:"+ path_cost+
				",operator: " + operator + ",capacity:"+ agent.getRemainingCapacity()+ "--------------------");
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
    	}
		return padded;
	}
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
	
	public boolean sameAs(StateNode node) {
		Cell[][] compGrid = node.getGrid();
		for(int i=0; i<compGrid.length; i++) {
			for(int j=0; j< compGrid[0].length; j++) {
				if (compGrid[i][j] instanceof Ship && grid[i][j] instanceof Ship) {
					Ship ship1 = (Ship)compGrid[i][j];
					Ship ship2 = (Ship)grid[i][j];
					if(!ship1.sameAs(ship2))
						return false;
				}
			}
		}
		return true;
	}


	
}
