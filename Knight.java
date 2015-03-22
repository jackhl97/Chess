import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Knight extends Pieces {
	public Knight(int inputX, int inputY, boolean side) {
		super(inputX, inputY, side, "K", 3, "WhiteKnight.png", "BlackKnight.png");
	}

	public boolean equals(Pieces input) {
		return input != null && input.toString().equals("K") && input.getPos()[0] == x && input.getPos()[1] == y && input.white() == white;
	}
	
	public int[][] getMoves(Board board, boolean any) {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		for (int i = -2; i < 5; i += 4) {
			for (int j = -1; j < 2; j += 2) {
				if (x + j < 8 && x + j > -1 && y + i < 8 && y + i > -1) {
					Pieces place = board.get(x + j, y + i);
					if (any || ((place == null || place.white() != white) && !board.check(white, x, y, x + j, y + i))) {
						moves.add(new int[]{x, y, x + j, y + i});
					}
				}
				if (x + i < 8 && x + i > -1 && y + j < 8 && y + j > -1) {
					Pieces place = board.get(x + i, y + j);
					if (any || ((place == null || place.white() != white) && !board.check(white, x, y, x + i, y + j))) {
						moves.add(new int[]{x, y, x + i, y + j});
					}
				}
			}
		}
		return moves.toArray(new int[moves.size()][4]);
	}
}