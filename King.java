import javax.swing.*;
import java.awt.*;
import java.util.*;

public class King extends Pieces {
	public King(int inputX, int inputY, boolean side) {
		super(inputX, inputY, side, "I", 500, "WhiteKing.png", "BlackKing.png");
	}

	public boolean equals(Pieces input) {
		return input != null && input.toString().equals("I") && input.getPos()[0] == x && input.getPos()[1] == y && input.white() == white;
	}
	
	public int[][] getMoves(Board board, boolean any) {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (j != 0 || i != 0) {
					if (x + i > -1 && x + 1 < 8 && y + j > -1 && y + j < 8) {
						if (board.get(x + i, y + j) == null || board.get(x + i, y + j).white() != white || any) {
							if (any) {
								moves.add(new int[]{x, y, x + i, y + j});
							}
							else if (!board.control(x + i, y + j, white)) {
								moves.add(new int[]{x, y, x + i, y + j});
							}
						}
					}
				}
			}
		}
		if (white && x == 4 && y == 7 && board.get(x + 1, y) == null && board.get(x + 2, y) == null && board.get(x + 3, y) instanceof Rook) {
			moves.add(new int[]{x, y, x + 2, y});
		}
		else if (!white && x == 4 && y == 0 && board.get(x + 1, y) == null && board.get(x + 2, y) == null && board.get(x + 3, y) instanceof Rook) {
			moves.add(new int[]{x, y, x + 2, y});
		}
		return moves.toArray(new int[moves.size()][4]);
	}
}