import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

//images from http://commons.wikimedia.org/wiki/Category:PNG_chess_pieces/Standard_transparent
//help from docs.oracle.com

public class Chess extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
	
	private JFrame frame = new JFrame("Chess: by Jack Lance");
	private Container canvas;
	private ArrayList<int[]> prevMoves = new ArrayList<int[]>();
	private ArrayList<Pieces> removed = new ArrayList<Pieces>();
	private int tempX, tempY;
	private int undoNum = 0;
	private JMenuBar menu = new JMenuBar();
	private JMenu file = new JMenu("File");
	private JMenuItem game2 = new JMenuItem("New 2 Player Game");
	private JMenuItem save = new JMenuItem("Save game");
	private JMenuItem load = new JMenuItem("Load game");
	private JMenu newGame = new JMenu("New Game");
	private JMenu edit = new JMenu("Edit");
	private JMenuItem undo = new JMenuItem("Undo");
	private JMenu computerGame = new JMenu("New game against computer");
	private JMenuItem level1 = new JMenuItem("Computer level 1");
	private JMenuItem level2 = new JMenuItem("Computer level 2");
	private JMenuItem level3 = new JMenuItem("Computer level 3");
	private JMenuItem customLevel = new JMenuItem("Custom Computer level");
	private JMenuItem showMoves = new JMenuItem("Turn on moves");
	private JMenuItem exit = new JMenuItem("Quit Game");
	private Board board;
	private Pieces moving;
	private int[][] moves;
	private boolean whiteTurn = true;
	private boolean  single = false;
	private boolean di = false;
	private boolean computerColor = false;
	private AI computer;
	private javax.swing.Timer timer = new javax.swing.Timer(500, this);
	private boolean show = false;
	private boolean checkMateWhite;
	private boolean checkMateBlack;
	private boolean staleMate;
	private JPanel pane = new JPanel();
	private JPanel pane1 = new JPanel();
	private JPanel pane2 = new JPanel();
	private JLabel label = new JLabel("Choose Which Game You Want", SwingConstants.CENTER);
	private JButton newPVP = new JButton("New Two Player Game");
	private JButton newC1 = new JButton("New Game vs. Computer level 1");
	private JButton newC2 = new JButton("New Game vs. Computer level 2");
	private JButton newC3 = new JButton("New Game vs. Computer level 3");
	private JButton newCustom = new JButton("New Game vs. Custom Computer level");
	private JButton exitButton = new JButton("Quit Game");

	public Chess(boolean cheat) {
		if (cheat) {
			di = true;
			single = true;
		}
		else {
			single = true;
			di = false;
		}
		if (single) {
			computer = new Easy(computerColor, 2);
		}
		timer.start();
		pane.setLayout(new BorderLayout());
		pane1.setLayout(new BorderLayout());
		pane2.setLayout(new BorderLayout());
		pane1.add(newPVP, BorderLayout.NORTH);
		pane1.add(newC1, BorderLayout.CENTER);
		pane1.add(newC2, BorderLayout.SOUTH);
		pane2.add(newC3, BorderLayout.NORTH);
		pane2.add(newCustom, BorderLayout.CENTER);
		pane2.add(exitButton, BorderLayout.SOUTH);
		pane.add(label, BorderLayout.NORTH);
		pane.add(pane1, BorderLayout.CENTER);
		pane.add(pane2, BorderLayout.SOUTH);
		newPVP.addActionListener(this);
		newC1.addActionListener(this);
		newC2.addActionListener(this);
		newC3.addActionListener(this);
		newCustom.addActionListener(this);
		canvas = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		setPreferredSize(new Dimension(600, 600));
		setBackground(Color.DARK_GRAY);
		board = new Board(startBoard());
		canvas.add(this, BorderLayout.CENTER);
		addMouseListener(this);
		addMouseMotionListener(this);		
		canvas.add(menu, BorderLayout.NORTH);
		menu.add(file);
		menu.add(edit);
		file.add(newGame);
		file.add(save);
		file.add(load);
		file.add(exit);
		edit.add(undo);
		edit.add(showMoves);
		newGame.add(game2);
		newGame.add(computerGame);
		computerGame.add(level1);
		computerGame.add(level2);
		computerGame.add(level3);
		computerGame.add(customLevel);
		undo.setEnabled(false);
		game2.addActionListener(this);
		undo.addActionListener(this);
		save.addActionListener(this);
		load.addActionListener(this);
		level1.addActionListener(this);
		level2.addActionListener(this);
		level3.addActionListener(this);
		showMoves.addActionListener(this);
		customLevel.addActionListener(this);
		exit.addActionListener(this);
		exitButton.addActionListener(this);
		game2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		frame.pack();
		frame.setVisible(true);
	}

	private void save(String path) {
		BufferedWriter writer = null;
		try {
			File logFile = new File(path);
			writer = new BufferedWriter(new FileWriter(logFile));
			for (Pieces[] element : board.getArray()) {
				for (Pieces piece: element) {
					if (piece != null) {
						writer.write(piece.saveChar());
					}
					else {
						writer.write("   ");
					}
				}
				writer.write("\n");
			}
			if (whiteTurn) {
				writer.write("1");
			}
			else {
				writer.write("0");
			}
			if (single) {
				writer.write("" + computer.getDepth());
			}
			else {
				writer.write("0");
			}
			addSaveFile(path);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch (Exception e) {}
		}
	}

	private boolean load(String path) {
		try {
			Scanner in = new Scanner(new FileReader(path));
			Pieces[][] loading = new Pieces[8][8];
			for (int i = 0; i < 8; i++) {
				String input = in.nextLine();
				for (int j = 0; j < 24; j += 3) {
					String[] piece = {input.substring(j, j + 1), input.substring(j + 1, j + 2)};
					if (piece[1].equals(null)) {
						loading[i][j / 3] = null;
					}
					else if (piece[1].equals("K")) {
						loading[i][j / 3] = new Knight(i, j / 3, piece[0].equals("W"));
					}
					else if (piece[1].equals("I")) {
						loading[i][j / 3] = new King(i, j / 3, piece[0].equals("W"));
					}
					else if (piece[1].equals("P")) {
						loading[i][j / 3] = new Pawn(i, j / 3, piece[0].equals("W"));
					}
					else if (piece[1].equals("Q")) {
						loading[i][j / 3] = new Queen(i, j / 3, piece[0].equals("W"));
					}
					else if (piece[1].equals("B")) {
						loading[i][j / 3] = new Bishop(i, j / 3, piece[0].equals("W"));
					}
					else if (piece[1].equals("R")) {
						loading[i][j / 3] = new Rook(i, j / 3, piece[0].equals("W"));
					}
				}
			}
			board = new Board(loading);
			String temp = in.nextLine();
			whiteTurn = temp.substring(0, 1).equals("1");
			int level = Integer.parseInt(temp.substring(1, 2));
			if (level == 0) {
				single = false;
			}
			else {
				single = true;
				System.out.println(level);
				computer = new Easy(false, level);
			}
			undo.setEnabled(false);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	private void addSaveFile(String name) {
		BufferedWriter writer = null;
		try {
			Object[] names = getFiles();
			File logFile = new File("save.txt");
			writer = new BufferedWriter(new FileWriter(logFile));
			for (int i = 0; i < names.length; i++) {
				writer.write((String)names[i] + "\n");
			}
			writer.write(name.substring(0, name.length() - 4));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch (Exception e) {}
		}
	}

	private Object[] getFiles() {
		ArrayList<Object> files= new ArrayList<Object>();
		boolean yes = true;
		try {
			Scanner in = new Scanner(new FileReader("save.txt"));
			while(in.hasNext()) {
				String temp = in.nextLine();
				files.add(temp);
			}
		}
		catch (Exception e) {}
		return files.toArray(new Object[files.size()]);
	}

	public void actionPerformed(ActionEvent a) {
		Object input = a.getSource();
		if (input.equals(game2) || input.equals(newPVP)) {
			board = new Board(startBoard());
			single = false;
			di = false;
			whiteTurn = true;
			prevMoves.clear();
			if (input.equals(newPVP)) {
				canvas.removeAll();
				canvas.add(this, BorderLayout.CENTER);
				canvas.add(menu, BorderLayout.NORTH);
				frame.pack();
				checkMateWhite = false;
				checkMateBlack = false;
				staleMate = false;
				timer.start();
			}
		}
		else if (input.equals(level1) || input.equals(newC1)) {
			board = new Board(startBoard());
			single = true;
			di = false;
			whiteTurn = true;
			computer = new Easy(false, 1);
			prevMoves.clear();
			if (input.equals(newC1)) {
				canvas.removeAll();
				canvas.add(this, BorderLayout.CENTER);
				canvas.add(menu, BorderLayout.NORTH);
				frame.pack();
				checkMateWhite = false;
				checkMateBlack = false;
				staleMate = false;
				timer.start();
			}
		}
		else if (input.equals(level2) || input.equals(newC2)) {
			board = new Board(startBoard());
			single = true;
			di = false;
			whiteTurn = true;
			computer = new Easy(false, 2);
			prevMoves.clear();
			if (input.equals(newC2)) {
				canvas.removeAll();
				canvas.add(this, BorderLayout.CENTER);
				canvas.add(menu, BorderLayout.NORTH);
				frame.pack();
				checkMateWhite = false;
				checkMateBlack = false;
				staleMate = false;
				timer.start();
			}
		}
		else if (input.equals(level3) || input.equals(newC3)) {
			board = new Board(startBoard());
			single = true;
			di = false;
			whiteTurn = true;
			computer = new Easy(false, 3);
			prevMoves.clear();
			if (input.equals(newC3)) {
				canvas.removeAll();
				canvas.add(this, BorderLayout.CENTER);
				canvas.add(menu, BorderLayout.NORTH);
				frame.pack();
				checkMateWhite = false;
				checkMateBlack = false;
				staleMate = false;
				timer.start();
			}
		}
		else if (input.equals(customLevel) || input.equals(newCustom)) {
			boolean cont = true;
			int levelInput = 0;
			boolean contin = true;
			while(cont) {
				try {
					cont = false;
					String things = (String)JOptionPane.showInputDialog("Enter The Desired Computer Level\n" + "High Levels May Take A Lot Of Time");
					if (things != null) {
						levelInput = Integer.parseInt(things);
					}
					else {
						contin = false;
					}	
				}
				catch(NumberFormatException n) {
					cont = true;
				}
			}
			if (contin) {
				board = new Board(startBoard());
				single = true;
				di = false;
				whiteTurn = true;
				computer = new Easy(false, levelInput);
				prevMoves.clear();
				if (input.equals(newCustom)) {
					canvas.removeAll();
					canvas.add(this, BorderLayout.CENTER);
					canvas.add(menu, BorderLayout.NORTH);
					frame.pack();
					checkMateWhite = false;
					checkMateBlack = false;
					staleMate = false;
					timer.start();
				}
			}
		}
		else if (input.equals(exit) || input.equals(exitButton)) {
			System.exit(0);
		}
		else if (input.equals(undo)) {
			undo();
		}
		else if (input.equals(save)) {
			String things;
			boolean cont = true;
			boolean contin = true;
			while(cont) {
				cont = false;
				things = (String)JOptionPane.showInputDialog("Enter the name of the save file");
				if (things != null) {
					things += ".txt";
					save(things);
				}					
			}
		}
		else if (input.equals(load)) {
			String things;
			boolean cont = true;
			boolean contin = true;
			while(cont) {
				cont = false;
				Object[] possibleValues = getFiles();
				Object selectedValue = JOptionPane.showInputDialog(null, "Choose a save", "Input", JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
				things = (String)(selectedValue);
				if (things != null) {
					things += ".txt";
					boolean t = load(things);
					cont = !t;
				}					
			}
		}
		else if (input.equals(timer)) {
			if (!board.anyMoves(whiteTurn)){
				if (board.check(true)) {
					checkMateWhite = true;
				}
				else if (board.check(false)) {
					checkMateBlack = true;
				}
				else {
					staleMate = true;
				}
				double time = System.currentTimeMillis();
				double temp = 0;
				if (checkMateWhite) {
					frame.setTitle("Black Wins");
				}
				else if (checkMateBlack) {
					frame.setTitle("White Wins");
				}
				else if (staleMate) {
					frame.setTitle("Stalemate");
				}
				while (temp < 5000) {
					temp = System.currentTimeMillis() - time;
				} 
				canvas.removeAll();
				canvas.add(pane, BorderLayout.CENTER);
				frame.pack();
				timer.stop();
			}
			if (checkMateWhite) {
				frame.setTitle("Black Wins");
			}
			else if (checkMateBlack) {
				frame.setTitle("White Wins");
			}
			else if (staleMate) {
				frame.setTitle("Stalemate");
			}
			if (single) {
				if (computerColor == whiteTurn) {
					computerMove(computerColor);
				}
				else if (di) {
					computerMove(!computerColor);
				}
			}
		}
		else if (input.equals(showMoves)) {
			show = !show;
			if (show) {
				showMoves.setText("Turn off moves");
			}
			else {
				showMoves.setText("Turn on moves");
			}
		}
	}

	private void undo() {
		int[] tempMoves = prevMoves.get(prevMoves.size() - 1);
		
		if (board.get(tempMoves[2], tempMoves[3]) != null && board.get(tempMoves[2], tempMoves[3]).toString().equals("I")) {
			if (tempMoves[2] == 6) {
				if (board.get(tempMoves[2], tempMoves[3]).white() && tempMoves[3] == 7) {
					board = board.newBoard(5, 7, 7, 7);
				}
				else if (!board.get(tempMoves[2], tempMoves[3]).white() && tempMoves[3] == 0) {
					board = board.newBoard(5, 0, 7, 0);
				}
			}
		}
		board = board.newBoard(tempMoves[2], tempMoves[3], tempMoves[0], tempMoves[1]);
		board.set(tempMoves[2], tempMoves[3], removed.get(removed.size() - 1));
		whiteTurn = !whiteTurn;
		prevMoves.remove(prevMoves.size() - 1);
		removed.remove(removed.size() - 1);
		if (prevMoves.isEmpty()) {
			undo.setEnabled(false);
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!checkMateWhite && !checkMateBlack && !staleMate) {	
			g.setColor(Color.LIGHT_GRAY);
			for (int i = 0; i < 600; i += 150) {
				for (int j = 0; j < 600; j += 150) {
					g.fillRect(i, j, 75, 75);
					g.fillRect(i + 75, j + 75, 75, 75);
				}
			}
			if (moves != null && show) {
				g.setColor(Color.YELLOW);
				for (int[] moveThing : moves) {
					g.drawOval(moveThing[2] * 75, moveThing[3] * 75, 75, 75);
				}
			}
			board.draw(g, this);
		}
		frame.repaint();
	}

	private Pieces[][] startBoard() {
		Pieces[][] starting = new Pieces[8][8];
		starting[0][0] = new Rook(0, 0, false);
		starting[7][0] = new Rook(7, 0, false);
		starting[0][7] = new Rook(0, 7, true);
		starting[7][7] = new Rook(7, 7, true);
		starting[1][0] = new Knight(1, 0, false);
		starting[6][0] = new Knight(6, 0, false);
		starting[1][7] = new Knight(1, 7, true);
		starting[6][7] = new Knight(6, 7, true);
		starting[2][0] = new Bishop(2, 0,false);
		starting[5][0] = new Bishop(5, 0, false);
		starting[2][7] = new Bishop(2, 7, true);
		starting[5][7] = new Bishop(5, 7, true);
		starting[3][0] = new Queen(3, 0, false);
		starting[3][7] = new Queen(3, 7, true);
		starting[4][0] = new King(4, 0, false);
		starting[4][7] = new King(4, 7, true);
		for (int i = 0; i < 8; i++) {
			starting[i][1] = new Pawn(i, 1, false);
			starting[i][6] = new Pawn(i, 6, true);
		}
		return starting;
	}

	public void mouseMoved(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {
		if (moving != null) {
			moving.moving(e.getX(), e.getY(), true);
		}
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (!checkMateWhite && !checkMateBlack && !staleMate) {
			moving = board.get((int)Math.floor(x / 75), (int)Math.floor(y / 75));
		}
		else {
			moving = null;
		}
		if (moving != null) {
			if (whiteTurn == moving.white()) { 
				moves = moving.getMoves(board, false);
				moving.moving(x, y, true);
				tempX = x / 75;
				tempY = y / 75;
			}
			else {
				moving = null;
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (moving != null) {
			int x = e.getX();
			int y = e.getY();
			moving.moving(x, y, false);
			if (moves != null) {
				for (int[] element : moves) {
					if (element[2] == x / 75 && element[3] == y / 75) {
						removed.add(board.get(x / 75, y / 75));
						board.set(x / 75, y / 75, moving);
						board.set(moving.getPos()[0], moving.getPos()[1], null);
						moving.move(x / 75, y / 75);
						if (moving != null && moving.toString().equals("I")) {
							if (tempX + 2 == x / 75) {
								board.get(tempX + 3, tempY).move(tempX + 1, tempY);
								board.set(tempX + 1, tempY, board.get(tempX + 3, tempY));
								board.set(tempX + 3, tempY, null);
							}
						}
						else if (moving != null && moving.toString().equals("P")) {
							if (!moving.white() && !whiteTurn) {
								if (moving.getPos()[1] == 7) {
									Object possibleValues[] = {"Queen", "Rook", "Bishop", "Knight"};
									Object selectedValue = JOptionPane.showInputDialog(null, "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
									if (selectedValue.equals("Queen")) {
										board.set(x / 75, y / 75, new Queen(x / 75, y / 75, false));
									}
									else if (selectedValue.equals("Rook")) {
										board.set(x / 75, y / 75, new Rook(x / 75, y / 75, false));
									}
									else if (selectedValue.equals("Bishop")) {
										board.set(x / 75, y / 75, new Bishop(x / 75, y / 75, false));
									}
									else if (selectedValue.equals("Knight")) {
										board.set(x / 75, y / 75, new Knight(x / 75, y / 75, false));
									}
								}
							}
							else if (moving.white() && whiteTurn) {
								if (moving.getPos()[1] == 0) {
									Object possibleValues[] = {"Queen", "Rook", "Bishop", "Knight"};
									Object selectedValue = JOptionPane.showInputDialog(null, "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
									if (selectedValue.equals("Queen")) {
										board.set(x / 75, y / 75, new Queen(x / 75, y / 75, true));
									}
									else if (selectedValue.equals("Rook")) {
										board.set(x / 75, y / 75, new Rook(x / 75, y / 75, true));
									}
									else if (selectedValue.equals("Bishop")) {
										board.set(x / 75, y / 75, new Bishop(x / 75, y / 75, true));
									}
									else if (selectedValue.equals("Knight")) {
										board.set(x / 75, y / 75, new Knight(x / 75, y / 75, true));
									}								
								}
							}
						}
						undo.setEnabled(true);
						prevMoves.add(new int[]{tempX, tempY, x / 75, y / 75});
						whiteTurn = !whiteTurn;
						break;
					}
				}
			}
			moving = null;
			moves = null;
		}
	}

	private void computerMove(boolean input) {
		int[] move = null;
		if (!checkMateWhite && !checkMateBlack && !staleMate) {
			move = computer.getMoves(board, input, 0, board.check(input));
			removed.add(board.get(move[3], move[4]));
			board.set(move[3], move[4], board.get(move[1], move[2]));
			boolean color = board.get(move[3], move[4]).white();
			if (!color && !computerColor) {
				if (move[4] == 7) {
					board.set(move[3], move[4], new Queen(move[2], move[3], color));
				}
			}
			else if (color && computerColor) {
				if (move[4] == 0) {
					board.set(move[3], move[4], new Queen(move[2], move[3], color));
				}
			}
			board.set(move[1], move[2], null);
			Pieces set = board.get(move[3], move[4]);
			try {
				set.move(move[3], move[4]);
			}
			catch(Exception e) {}
			whiteTurn = !whiteTurn;
			undo.setEnabled(true);
			prevMoves.add(new int[]{move[1], move[2], move[3], move[4]});
		}
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			Chess game = new Chess(false);
		}
		else {
			if (args[0].equals("sudo")) {
				Chess game = new Chess(true);
			}
		}
	}
}