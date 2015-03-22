import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Rook extends Pieces {
	public Rook(int inputX, int inputY, boolean side) {
		super(inputX, inputY, side, "R", 5, "WhiteRook.png", "BlackRook.png");
	}

	public boolean equals(Pieces input) {
		return input != null && input.toString().equals("R") && input.getPos()[0] == x && input.getPos()[1] == y && input.white() == white;
	}
	
	public int[][] getMoves(Board board, boolean any) {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		for (int i = -1; i < 2; i += 2) {
			int tempX = x + i;
			boolean done = false;	
			while(!done && tempX < 8 && tempX > -1) {
				if (board.get(tempX, y) != null) {
					done = true;
					if (any || (board.get(tempX, y).white() != white && !board.check(white, x, y, tempX, y))) {
						moves.add(new int[]{x, y, tempX, y});
					}
				}
				else if (any || !board.check(white, x, y, tempX, y)) {
					moves.add(new int[]{x, y, tempX, y});
					tempX += i;
				}
				else {
					done = true;
				}
			}
			done = false;
			int tempY = y + i;
			while(!done && tempY < 8 && tempY > -1) {
				if (board.get(x, tempY) != null) {
					done = true;
					if (any || (board.get(x, tempY).white() != white && !board.check(white, x, y, x, tempY))) {
						moves.add(new int[]{x, y, x, tempY});
					}
				}
				else if (any || !board.check(white, x, y, x, tempY)) {
					moves.add(new int[]{x, y, x, tempY});
					tempY += i;
				}
				else {
					done = true;
				}
			}
		}
		return moves.toArray(new int[moves.size()][4]);
	}
}