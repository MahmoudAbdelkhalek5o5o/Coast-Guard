package code;

public class Agent extends Cell{
    int max_capacity;
    int remaining_capacity;
    int blackBoxes=0;
    public Agent(int i, int j, int max_capacity){
        super(i,j);
        this.max_capacity = max_capacity;
        remaining_capacity = max_capacity;
    }
    
    public Agent(int i, int j, int max_capacity, int remaining_capacity){
        super(i,j);
        this.max_capacity = max_capacity;
        this.remaining_capacity = remaining_capacity;
        
    }
    public int getMaxCapacity() {
    	return max_capacity;
    }
    public int getRemainingCapacity() {
    	return remaining_capacity;
    }
    public int getBlackBoxes() {
		return blackBoxes;
	}

	public void setBlackBoxes(int blackBoxes) {
		this.blackBoxes = blackBoxes;
	}

	public int getPassengersOnBoard() {
    	return max_capacity - remaining_capacity;
    }
    public void setPassengersOnBoard(int passengerNumber) {
    	this.remaining_capacity = max_capacity - passengerNumber;
    }
    public void setRemainingCapacity(int remaining_capacity) {
    	this.remaining_capacity = remaining_capacity;
    }
    public boolean hasFreeSpace() {
    	return remaining_capacity>0;
    }
    public Agent copyAgentWithModification(int i, int j) {
    	return new Agent(i, j, this.max_capacity, this.remaining_capacity);
    }
    public Agent copyAgentWithModification(int i, int j, int newCapacity) {
    	return new Agent(i, j, this.max_capacity, this.max_capacity-newCapacity);
    }
}