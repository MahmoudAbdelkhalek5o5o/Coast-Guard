package code;

import java.util.ArrayList;
import java.util.Comparator;

public class StateNodeComparator implements Comparator<StateNode>{
        
        // Overriding compare()method of Comparator
                    // for descending order of cgpa
    	ArrayList<int[]>ships_positions;
    	ArrayList<int[]>stations_positions;
    	String method;
		public StateNodeComparator(ArrayList<int[]>ships_positions, ArrayList<int[]>stations_positions, String method) {
			this.ships_positions = ships_positions;
			this.stations_positions = stations_positions;
			this.method = method;
		}
		@Override
		public int compare(StateNode o1, StateNode o2) {
			// TODO Auto-generated method stub
			float score1 = o1.calcMaxScore(ships_positions, stations_positions, method);
			float score2 = o2.calcMaxScore(ships_positions, stations_positions, method);
			  if (score1 < score2)
	                return 1;
	            else if (score1 > score2)
	                return -1;
	                return 0;
		}
}
