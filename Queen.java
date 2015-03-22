import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Queen extends Pieces {
	public Queen(int inputX, int inputY, boolean side) {
		super(inputX, inputY, side, "Q", 9, "WhiteQueen.png", "BlackQueen.png");
	}

	public boolean equals(Pieces input) {
		return input != null && input.toString().equals("Q") && input.getPos()[0] == x && input.getPos()[1] == y && input.white() == white;
	}
	
	public int[][] getMoves(Board board, boolean any) {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		for (int i = -1; i < 2; i+=2) {
			for (int j = -1; j < 2; j+=2) {
				int tempY = y + i;
				int tempX = x + j;
				boolean done = false;
				while (!done && tempX < 8 && tempY < 8 && tempY > -1 && tempX > -1) {
					if (board.get(tempX, tempY) != null) {
						done = true;
						if (any || (board.get(tempX, tempY).white() != white)) {
							moves.add(new int[]{x, y, tempX, tempY});
						}
					}
					else if (any || !board.check(white, x, y, tempX, tempY)) {
						moves.add(new int[]{x, y, tempX, tempY});
						tempY += i;
						tempX += j;
					}
					else {
						done = true;
					}
				}
			}
		}
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