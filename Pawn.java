import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Pawn extends Pieces {
	public Pawn(int inputX, int inputY, boolean side) {
		super(inputX, inputY, side, "P", 1, "WhitePawn.png", "BlackPawn.png");
	}

	public boolean equals(Pieces input) {
		return input != null && input.toString().equals("P") && input.getPos()[0] == x && input.getPos()[1] == y && input.white() == white;
	}
	
	public int[][] getMoves(Board board, boolean any) {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		if (!white) {
			if (y + 1 < 8) {
				if (board.get(x, y + 1) == null && !any) {
					if (board.get(x, y + 2) == null && y == 1 && !board.check(white, x, y, x, y + 2)) {
						moves.add(new int[]{x, y, x, y + 2});
					}
					if (!board.check(white, x, y, x, y + 1)) {
						moves.add(new int[]{x, y, x, y + 1});
					}
				}
				Pieces left = board.get(x - 1, y + 1);
				Pieces right = board.get(x + 1, y + 1);
				if (any || (left != null && left.white() && !board.check(white, x, y, x - 1, y + 1))) {
					moves.add(new int[]{x, y, x - 1, y + 1});
				}
				if (any || (right != null && right.white() && !board.check(white, x, y, x + 1, y + 1))) {
					moves.add(new int[]{x, y, x + 1, y + 1});
				}
			}
		}
		else  {
			if (y - 1 > -1) {
				if (board.get(x, y - 1) == null && !any) {
					if (board.get(x, y - 2) == null && y == 6 && !board.check(white, x, y, x, y - 2)) {
						moves.add(new int[]{x, y, x, y - 2});
					}
					if (!board.check(white, x, y, x, y - 1)) {
						moves.add(new int[]{x, y, x, y - 1});
					}
				}
				Pieces left = board.get(x - 1, y - 1);
				Pieces right = board.get(x + 1, y - 1);
				if (any || (left != null && !left.white() && !board.check(white, x, y, x - 1, y - 1))) {
					moves.add(new int[]{x, y, x - 1, y - 1});
				}
				if (any || (right != null && !right.white() && !board.check(white, x, y, x + 1, y - 1))) {
					moves.add(new int[]{x, y, x + 1, y - 1});
				}
			}
		}
		return moves.toArray(new int[moves.size()][4]);
	}
}