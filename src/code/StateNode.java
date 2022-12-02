package code;


public class StateNode {
	Cell[][] grid;
	Agent agent;
	StateNode parent;
	String operator;
	int depth;
	int path_cost;
	String plan;

	public StateNode(Cell[][] grid,Agent agent, StateNode parent, String operator, int depth, int path_cost) {
		this.grid = grid;
		this.agent = agent;
		this.parent = parent;
		this.operator = operator;
		this.depth = depth;
		this.path_cost = path_cost;
		plan="";
	}

	public boolean isGoal() {
    	// case 1
    	if(endGame()) {
    		plan = printPath("");
    		System.out.println(agent.getBlackBoxes());
    	}
    	
    	return endGame();
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
	public String plan() {
		return plan;
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
//	public int getGridHC() {
//		int counter = 0;
//		for( int i=0; i< grid.length; i++) {
//			for(int j=0; j<grid[0].length;j++) {
//				
//			}
//		}
//	}
//	public int hashCode() {
//		int agentHC = agent.hashCode();
//		
//		
//		return agentHC;
//	}
//	public String toString() {
//		visualizeGrid();
//		return operator +" "+ depth + " " + path_cost + " " + agent.getI() + " " + agent.getJ();
//	}
//
}
