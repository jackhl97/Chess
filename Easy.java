import java.util.*;

public class Easy implements AI{
	private boolean side;
	private int depth;

	public Easy(boolean inputSide, int inputDepth) {
		side = inputSide;
		depth = inputDepth;
	}

	public int[] getMoves(Board board, boolean white, int inputDepth, boolean check) {
		ArrayList<int[]> values = new ArrayList<int[]>();
		int initScore = board.getScore();
		boolean multipleMoves = false;
		ArrayList<int[]> multiple = new ArrayList<int[]>();
		if (white) {
			values.add(new int[]{-2000, 0, 0, 0, 0});
		}
		else {
			values.add(new int[]{2000, 0, 0, 0, 0});
		}
		int position = 0;
		if (inputDepth == depth) {		
			int[] ret = {board.getScore(), 0, 0, 0, 0};
			return ret;
		}
		for (Pieces[] subPiece : board.getArray()) {
			for (Pieces piece : subPiece) {
				if (piece != null && piece.white() == white) {
					int[][] moves = piece.getMoves(board, false);
					if (moves != null) {
						for (int[] move : moves) {
							Board newBoard = board.newBoard(move[0], move[1], move[2], move[3]);
							boolean color = newBoard.get(move[2], move[3]).white();
							if (!color && !side) {
								if (move[3] == 7) {
									newBoard.set(move[2], move[3], new Queen(move[2], move[3], color));
								}
							}
							else if (color && side) {
								if (move[3] == 0) {
									newBoard.set(move[2], move[3], new Queen(move[2], move[3], color));
								}
							}
							if (newBoard != null) {
								int score = getMoves(newBoard, !white, inputDepth + 1, newBoard.check(white))[0];
								values.add(new int[]{score, move[0], move[1], move[2], move[3]});
								if ((white && score > values.get(position)[0]) || (!white && score < values.get(position)[0])) {
									position = values.size() - 1;
									multipleMoves = false;
									multiple.clear();
								}
								else if (score == values.get(position)[0]) {
									multipleMoves = true;
									multiple.add(values.get(values.size() - 1));
									multiple.add(values.get(position));
								}
							}
						}
					}
				}
			}
		}
		if (values.size() == 1) {
			if (depth == 0) {
				return null;
			}
			else {
				if (white) {
					return new int[]{-3000, 0, 0, 0, 0};
				}
				else {
					return new int[]{3000, 0, 0, 0, 0};
				}
			}
		}
		if (multipleMoves && multiple != null) {
			position = (int)((multiple.size() - 1) * Math.random() + 1);
			return multiple.get(position);
		}
		
		return values.get(position);
	}
	
	public int getDepth() {
		return depth;
	}
}