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
			float score1=0;
			float score2=0;
			if(method.equals("GR1")){
				score1 = o1.calcMaxScoreGR1(ships_positions, stations_positions);
				score2 = o2.calcMaxScoreGR1(ships_positions, stations_positions);
			}
			else if(method.equals("GR2")) {
				// change to gr2
				score1 = o1.calcMaxScoreGR1(ships_positions, stations_positions);
				score2 = o2.calcMaxScoreGR1(ships_positions, stations_positions);
			}
			else if(method.equals("AS1")) {
				score1 = o1.calcMaxScoreGR1(ships_positions, stations_positions);
				score1 +=o1.getPath_cost();
				score2 = o2.calcMaxScoreGR1(ships_positions, stations_positions);
				score2 +=o2.getPath_cost();
			}
			else if(method.equals("AS2")){
				score1 = o1.calcMaxScoreGR1(ships_positions, stations_positions);
				score1 +=o1.getPath_cost();
				score2 = o2.calcMaxScoreGR1(ships_positions, stations_positions);
				score2 +=o2.getPath_cost();
			}
			
			
			  if (score1 > score2)
	                return 1;
	            else if (score1 < score2)
	                return -1;
	                return 0;
		}
}
