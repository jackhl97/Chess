import java.awt.*;
import javax.swing.*;

public class Board {
	private Pieces[][] board;

	public Board(Pieces[][] input) {
		board = deepCopy(input);
	}

	public Pieces[][] deepCopy(Pieces[][] input) {
		Pieces[][] temp = new Pieces[input.length][input[0].length];
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[0].length; j++) {
				Pieces p = input[i][j];
				if (p != null) {
					if (p.toString().equals("P")) {
						temp[i][j] = new Pawn(p.getPos()[0], p.getPos()[1], p.white());
					}
					else if (p.toString().equals("K")) {
						temp[i][j] = new Knight(p.getPos()[0], p.getPos()[1], p.white());
					}
					else if (p.toString().equals("I")) {
						temp[i][j] = new King(p.getPos()[0], p.getPos()[1], p.white());
					}
					else if (p.toString().equals("Q")) {
						temp[i][j] = new Queen(p.getPos()[0], p.getPos()[1], p.white());
					}
					else if (p.toString().equals("B")) {
						temp[i][j] = new Bishop(p.getPos()[0], p.getPos()[1], p.white());
					}
					else if (p.toString().equals("R")) {
						temp[i][j] = new Rook(p.getPos()[0], p.getPos()[1], p.white());
					}		
				}
				else {
					temp[i][j] = null;
				}
			}
		}
		return temp;
	}

	public Pieces[][] getArray() {
		return board;
	}

	public void set(int x, int y, Pieces change) {
		board[x][y] = change;
	}

	public Pieces get(int x, int y) {
		try{
			return board[x][y];
		}
		catch (ArrayIndexOutOfBoundsException a) {
			return null;
		}
	}

	public Board newBoard(int prevX, int prevY, int x, int y) {
		Board newB = new Board(board);
		Pieces temp = newB.get(prevX, prevY);
		if (temp != null) {
			newB.set(x, y, temp);				
			temp.move(x, y);
			newB.set(prevX, prevY, null);
			return newB;
		}
		else {
			return null;
		}
	}

	public boolean control(int x, int y, boolean white) {
		for (Pieces[] subPieces : board) {
			for (Pieces piece : subPieces) {
				if (piece != null) {
					if (piece.white() != white) {
						int[][] moves = piece.getMoves(this, true);
						for (int[] move : moves) {
							if (x == move[2] && move[3] == y) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public void draw(Graphics g, JPanel pane) {
		for (Pieces[] subPieces : board) {
			for (Pieces piece : subPieces) {
				if (piece != null) {
					piece.draw(g, pane);
				}
			}
		}
	}

	public int getScore() {
		int score = 0;
		for (Pieces[] subPiece : board) {
			for (Pieces piece : subPiece) {
				if (piece != null) {
					score += piece.getValue();
				}
			}
		}
		return score;
	}

	public boolean check(boolean white) {
		for (Pieces[] element: board) {
			for (Pieces piece : element) {
				if (piece != null && piece.toString().equals("I") && piece.white() == white) {
					if (control(piece.getPos()[0], piece.getPos()[1], white)) {
						return true;
					}
					else {
						return false;
					}
				}
			}
		}
		return false;
	}

	public boolean check(boolean white, int prevX, int prevY, int x, int y) {
		Board tempBoard = newBoard(prevX, prevY, x, y);
		return tempBoard.check(white);
	}
	
	public boolean anyMoves(boolean turn) {
		for (Pieces[] element: board) {
			for (Pieces piece : element) {
				if (piece != null) {
					if (piece.white() == turn) {
						int[][] temp = piece.getMoves(this, false);
						if (temp.length != 0) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}