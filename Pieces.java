import java.awt.*;
import javax.swing.*;

public abstract class Pieces {
	protected int x, y;
	protected boolean white;
	protected ImageIcon w;
	protected Image whiteImage;
	protected ImageIcon b;
	protected Image blackImage;
	protected int mouseX, mouseY;
	protected boolean moving = false;
	protected int dX, dY;
	protected int value;
	protected String shar;
	
	public Pieces(int inputX, int inputY, boolean side, String iShar, int val, String wIName, String bIName) {
		x = inputX;
		y = inputY;
		white = side;
		value = val;
		shar = iShar;
		w = new ImageIcon(wIName);
		whiteImage = w.getImage();
		b = new ImageIcon(bIName);
		blackImage = b.getImage();
	}

	public int[] getPos() {
		return new int[] {x, y};
	}

	public abstract int[][] getMoves(Board board, boolean any);
	//compares if pieces are of the same type
	public abstract boolean equals(Pieces input);
	public void move(int inputX, int inputY) {
		x = inputX;
		y = inputY;
	}

	public void draw(Graphics g, JPanel pane) {
		if (!moving) {
			if (white) {
				g.drawImage(whiteImage, x * 75, y * 75, pane);
			}
			else {
				g.drawImage(blackImage, x * 75, y * 75, pane);
			}
		}
		else {
			if (white) {
				g.drawImage(whiteImage, mouseX + dX, mouseY + dY, pane);
			}
			else {
				g.drawImage(blackImage, mouseX + dX, mouseY + dY, pane);
			}
		}
	}

	public boolean white() {
		return white;
	}

	public void moving(int iX, int iY, boolean move) {
		if (move) {
			mouseX = iX;
			mouseY = iY;
		}
		if (!moving) {
			dX = x * 75 - mouseX;
			dY = y * 75 - mouseY;
		}
		moving = move;
	}

	public String saveChar() {
		if (white) {
			return "W" + shar + " ";
		}
		else {
			return "B" + shar + " ";
		}
	}

	public String toString() {
		return shar;
	}

	public int getValue() {
		if (white) {
			return value;
		}
		else {
			return -1 * value;
		}
	}
}