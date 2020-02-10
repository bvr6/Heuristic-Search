package searchclient;

public class BoxPair {
	public int goalRow;
	public int goalCol;
	public int boxRow;
	public int boxCol;
	public int distance;
	public DistanceMap DM;
	public char c;
	public BoxPair(int boxRow, int boxCol, char c, int distance, DistanceMap DM) {
		this.boxRow = boxRow;
		this.boxCol = boxCol;
		this.c = c;
		this.distance = distance;
		this.DM = DM;
	}
	
	
	public int pairDistance() {
		return DM.distFromPoint(goalRow, goalCol);
	}
	
}