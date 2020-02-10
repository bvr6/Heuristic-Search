package searchclient;
public class DistanceMap {
	
	public int[][] distance;
	public int row;
	public int col;
	public boolean valid = true; // used if the position given is valid (not in a wall)
	public DistanceMap(int i, int j) {
	    if (!State.walls[i][j]) {
			
			row = State.walls.length;
	    	col = State.walls[0].length;
	    	int initI = i;
	    	int initJ = j;
	    	
	    	
	    	
	    	distance = new int[row][col];
	    	
	    	// Initialize entire map
	    	for (int k = 0; k < row; k++) {
	    		for (int l = 0; l < col; l++) {
	    			distance[k][l] = -1;
	    		}
	    	}
	    	
	    	distance[i][j] = 0;
			
	    	// Initialize walls
	    	for (int k = 0; k < row; k++) {
	    		for (int l = 0; l < col; l++) {
	    			if (State.walls[k][l]) {
	    				distance[k][l] = Integer.MAX_VALUE;
	    			}
	    		}
	    	}
	    	distance = recursiveDistance(distance, initI, initJ, 1);
	    }
	    else {
	    	valid = false;
	    }
	}
	
	public int[][] recursiveDistance(int[][] dist, int I, int J, int num){
		if (I + 1 < row && dist[I + 1][J] != Integer.MAX_VALUE) {
			if (dist[I + 1][J] == -1 || dist[I+1][J] > num) {
				dist[I + 1][J] = num;
				recursiveDistance(dist, I + 1, J, num + 1);
			}
		}
		if (J + 1 < col && dist[I][J + 1] != Integer.MAX_VALUE) {
			if (dist[I][J + 1] == -1 || dist[I][J+1] > num) {
				dist[I][J + 1] = num;
				recursiveDistance(dist, I, J + 1, num + 1);
			}
		}
		if (I - 1 >= 0 && dist[I - 1][J] != Integer.MAX_VALUE) {
			if (dist[I - 1][J] == -1 || dist[I-1][J] > num) {
				dist[I - 1][J] = num;
				recursiveDistance(dist, I - 1, J, num + 1);
			}
		}
		if (J - 1 >= 0 && dist[I][J - 1] != Integer.MAX_VALUE) {
			if (dist[I][J - 1] == -1 || dist[I][J - 1] > num) {
				dist[I][J - 1] = num;
				recursiveDistance(dist, I, J - 1, num + 1);
			}
		}
		return dist; // change
	}
	
	public void printDist() {
		if (valid) {
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					if (distance[i][j] == Integer.MAX_VALUE) System.err.print("w ");
					else System.err.print(distance[i][j] + " ");
				}
				System.err.println();
			}
		}
		else {
			System.err.println("Not valid :(");
		}
	}
	
	public int distFromPoint(int a, int b) {
		return distance[a][b];
	}

}
