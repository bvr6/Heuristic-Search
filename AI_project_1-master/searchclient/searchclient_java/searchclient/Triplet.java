package searchclient;

public class Triplet {
	public char c;
	public int rowNum;
	public int colNum;
	public Triplet(char c, int rowNum, int colNum) {
		this.c = c;
		this.rowNum = rowNum;
		this.colNum = colNum;
	}
	public int getRowNum(){
		return rowNum;
	}
	public int getColNum(){
		return colNum;
	}
	public char getChar() {
		return c;
	}
	public String toString(){
		return "("+ c + " " + rowNum + " " + colNum + ")";
	}
}
