/**
 * @author Gou Ming Shi
 * http://metaphy.javaeye.com/
 * Sep 22, 2009
 * [Updated]Sep 14, 2010
 * All Rights Reserved
 */
package com.javaeye.metaphy.game;

import static com.javaeye.metaphy.game.BoardUtil.CAMP;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_E;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_N;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_S;
import static com.javaeye.metaphy.game.BoardUtil.FLAG_W;
import static com.javaeye.metaphy.game.BoardUtil.HEADQUARTER;
import static com.javaeye.metaphy.game.BoardUtil.STATION_RAILWAY;
import static com.javaeye.metaphy.game.BoardUtil.STATION_ROAD;
import static com.javaeye.metaphy.game.Game.BOARD_GRID_SIZE;
import static com.javaeye.metaphy.game.Game.GRID_UNIT_LENGTH;
import static com.javaeye.metaphy.game.BoardUtil.BOARD_ARRAY_SIZE;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.javaeye.metaphy.action.GamePanelAction;
import com.javaeye.metaphy.action.command.CommandBox;
import com.javaeye.metaphy.action.operation.OperationButton;
import com.javaeye.metaphy.action.operation.OperationButton.Operations;
import com.javaeye.metaphy.model.BaseElement;
import com.javaeye.metaphy.model.Coordinate;
import com.javaeye.metaphy.model.Location;
import com.javaeye.metaphy.model.Movement;
import com.javaeye.metaphy.model.Piece;
import com.javaeye.metaphy.model.SoldierStation;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	/* Board */
	private Board gameBoard = Game.ME.getGameBoard();
	/* Draw base line or not */
	private boolean debugDrawBaseline = true;
	/* Draw background image or not */
	private boolean debugDrawBackgroundImage = false;
	/* File path of the background */
	private String backgroundImageFile = "res/images/background.jpg";
	/* back ground image */
	private Image backgroundImage = null;
	/* Operation Buttons */
	private OperationButton operations[] = new OperationButton[9];
	/* SaveLineupFile radios */
	private SaveLineupRadioPane saveLineupRadioPane = new SaveLineupRadioPane();
	/* Command input box and output box */
	private CommandBox commandBox = new CommandBox();
	/* Board */
	private byte[][] stations = gameBoard.getStations();
	/* Piece list */
	private ArrayList<Piece> pieces = new ArrayList<Piece>();
	/* SoldierStation list */
	private ArrayList<SoldierStation> ssList = new ArrayList<SoldierStation>();
	/* Path arrows */
	private Vector<Movement> arrowsList = new Vector<Movement>();

	/* Cached the arrow images */
	private static final Image[] ARROWS_IMAGE = new Image[8];

	static {
		try {
			URLClassLoader urlLoader = (URLClassLoader) (Game.class
					.getClassLoader());
			for (int i = 0; i < 8; i++) {
				String pngPath = "res/images/Arrow" + i + ".png";
				URL url = urlLoader.findResource(pngPath);
				ARROWS_IMAGE[i] = ImageIO.read(url);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor of the game panel
	 */
	public GamePanel() {
		super();
		setFont(Game.TIMER_NUMBER_FONT);
		setLayout(null);
		setFocusable(true); // enable the JPanle to accept key input
		// Add pieces and soldier stations
		addBaseElements();
		// Add operation buttons
		addOptionButtons();
		// Add radios pane
		add(saveLineupRadioPane);
		// Add the command input box and output box
		add(commandBox);
		add(commandBox.getCobwsp());
		// Add the playing timer
		add(Game.ME.getTimer());
		// Add action listeners
		addMouseListener(new GamePanelAction(this));
		addKeyListener(new GamePanelAction(this));
	}

	/**
	 * Add pieces and soldier stations
	 */
	private void addBaseElements() {
		// Add all pieces firstly.
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				if (gameBoard.getBoard()[i][j] != BoardUtil.INVALID) {
					Piece piece = new Piece(i, j, gameBoard.getBoard()[i][j]);
					if (gameBoard.getBoard()[i][j] < 0x10) {
						piece.setShowCaption(true);
					}
					piece.renderWidget();
					piece.addWidgetAction();
					pieces.add(piece); // add to the vector
					add(piece.getWidget());
				}
			}
		}

		// Then add all soldierStations
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				// Initialize the soldierStations and add them on the panel
				if (stations[i][j] != 0x00) {
					SoldierStation ss = new SoldierStation(i, j, stations[i][j]);
					ss.renderWidget();
					ss.addWidgetAction();
					ssList.add(ss);
					add(ss.getWidget());
				}
			}
		}
	}

	/**
	 * Refresh all pieces
	 * 
	 * @param showCaption 
	 * 0: game play, 1: replay
	 */
	public void refreshAllPieces(boolean showCaption) {
		for (int index = 0, j = 0; j < BOARD_ARRAY_SIZE; j++) {
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {
				// Re-load the pieces
				if (gameBoard.getBoard()[i][j] != BoardUtil.INVALID) {
					Piece piece = pieces.get(index++);
					piece.setType(gameBoard.getBoard()[i][j]);
					piece.setXY(i, j);
					piece.setVisible(true);

					if (showCaption) {
						piece.setShowCaption(true);
					} else {
						if (piece.getLocated() == Location.SOUTH) {
							piece.setShowCaption(true);
						} else {
							piece.setShowCaption(false);
						}
					}
					piece.renderWidget();
				}
			}
		}

	}

	/**
	 * Get the Staion or Piece
	 */
	public BaseElement getBaseElement(Coordinate c) {
		Piece p = getPiece(c);
		if (p != null) {
			return p;
		}
		for (SoldierStation ss : ssList) {
			if (ss.getX() == c.x && ss.getY() == c.y) {
				return ss;
			}
		}
		return null;
	}

	/**
	 * Add operation buttons
	 */
	private void addOptionButtons() {
		// Add operation buttons
		operations[0] = new OperationButton(Operations.START_GAME);
		operations[1] = new OperationButton(Operations.CALLIN_LINEUP);
		operations[2] = new OperationButton(Operations.SAVE_LINEUP);
		operations[3] = new OperationButton(Operations.CALLIN_REPLAY);
		operations[4] = new OperationButton(Operations.PASS);
		operations[5] = new OperationButton(Operations.GIVE_UP);
		operations[6] = new OperationButton(Operations.PREVIOUS_STEP);
		operations[7] = new OperationButton(Operations.NEXT_STEP);
		operations[8] = new OperationButton(Operations.REPLAY_END);

		// Hide the "Pass" "GiveUP" buttons before game started
		operations[4].setVisible(false);
		operations[5].setVisible(false);
		// Hide the pervious_step, next_step buttons
		operations[6].setVisible(false);
		operations[7].setVisible(false);
		operations[8].setVisible(false);

		for (int i = 0; i < operations.length; i++) {
			this.add(operations[i]);
		}
	}

	/**
	 * Paint
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Draw background image
		if (isDebugDrawBackgroundImage()) {
			// read background image
			try {
				URLClassLoader urlLoader = (URLClassLoader) (Game.class
						.getClassLoader());
				URL url = urlLoader.findResource(backgroundImageFile);
				backgroundImage = ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (backgroundImage != null) {
				int imgWidth = backgroundImage.getWidth(this);
				int imgHeight = backgroundImage.getHeight(this);
				g2.drawImage(backgroundImage, 0, 0, null);
				for (int i = 0; i * imgWidth <= getWidth(); i++)
					for (int j = 0; j * imgHeight <= getHeight(); j++)
						if (i + j > 0)
							g2.copyArea(0, 0, imgWidth, imgHeight,
									i * imgWidth, j * imgHeight);
			}
		}

		// Draw base line
		for (int i = 0; i <= BOARD_GRID_SIZE; i++) {
			// Draw horizontal line
			g2.setPaint(Color.LIGHT_GRAY);
			Line2D line = new Line2D.Double(GRID_UNIT_LENGTH, (i + 1)
					* GRID_UNIT_LENGTH, BOARD_GRID_SIZE * GRID_UNIT_LENGTH
					+ GRID_UNIT_LENGTH, (i + 1) * GRID_UNIT_LENGTH);
			g2.draw(line);
			// Draw vertical line
			line = new Line2D.Double((i + 1) * GRID_UNIT_LENGTH,
					GRID_UNIT_LENGTH, (i + 1) * GRID_UNIT_LENGTH,
					BOARD_GRID_SIZE * GRID_UNIT_LENGTH + GRID_UNIT_LENGTH);
			g2.draw(line);
			if (isDebugDrawBaseline()) {
				// Draw column number
				//g2.setPaint(Color.BLACK);
				//g2.drawString(String.valueOf(i), (i + 1) * GRID_UNIT_LENGTH,
					//	GRID_UNIT_LENGTH - GRID_UNIT_LENGTH / 2);
				// Draw line number
				//g2.drawString(String.valueOf(i), 0, (i + 1) * GRID_UNIT_LENGTH);
			}
		}

		// Draw all the roads and railways
		for (int j = 0; j < BOARD_ARRAY_SIZE; j++) { // Every column
			for (int i = 0; i < BOARD_ARRAY_SIZE; i++) {// Every row
				if (stations[i][j] != BoardUtil.INVALID) {
					if (stations[i][j] == STATION_ROAD
							|| stations[i][j] == HEADQUARTER
							|| stations[i][j] == CAMP) {
						if (j + 1 < BOARD_ARRAY_SIZE
								&& stations[i][j + 1] != BoardUtil.INVALID) {
							drawRoad(g2, i, j, i, j + 1, STATION_ROAD);
						}
						if (i + 1 < BOARD_ARRAY_SIZE
								&& stations[i + 1][j] != BoardUtil.INVALID) {
							drawRoad(g2, i, j, i + 1, j, STATION_ROAD);
						}
					}

					if (stations[i][j] == STATION_RAILWAY) {
						if (j + 1 < BOARD_ARRAY_SIZE
								&& stations[i][j + 1] == STATION_RAILWAY) {
							drawRoad(g2, i, j, i, j + 1, STATION_RAILWAY);
						} else if (j + 1 < BOARD_ARRAY_SIZE
								&& (stations[i][j + 1] == STATION_ROAD
										|| stations[i][j + 1] == CAMP || stations[i][j + 1] == HEADQUARTER)) {
							drawRoad(g2, i, j, i, j + 1, STATION_ROAD);
						} else if (j + 2 < BOARD_ARRAY_SIZE
								&& stations[i][j + 1] == BoardUtil.INVALID
								&& stations[i][j + 2] == STATION_RAILWAY) {
							drawRoad(g2, i, j, i, j + 2, STATION_RAILWAY);
						}
						if (i + 1 < BOARD_ARRAY_SIZE
								&& stations[i + 1][j] == STATION_RAILWAY) {
							drawRoad(g2, i, j, i + 1, j, STATION_RAILWAY);
						} else if (i + 1 < BOARD_ARRAY_SIZE
								&& (stations[i + 1][j] == STATION_ROAD
										|| stations[i + 1][j] == CAMP || stations[i + 1][j] == HEADQUARTER)) {
							drawRoad(g2, i, j, i + 1, j, STATION_ROAD);
						} else if (i + 2 < BOARD_ARRAY_SIZE
								&& stations[i + 1][j] == BoardUtil.INVALID
								&& stations[i + 2][j] == STATION_RAILWAY) {
							drawRoad(g2, i, j, i + 2, j, STATION_RAILWAY);
						}
					}

					// Draw / or \ path connected with camp
					if (stations[i][j] == CAMP)
						for (int m = -1; m <= 1; m += 2)
							for (int n = -1; n <= 1; n += 2)
								if (stations[i + m][j + n] == CAMP
										|| stations[i + m][j + n] == STATION_RAILWAY)
									drawRoad(g2, i, j, i + m, j + n,
											STATION_ROAD);
				}
			}
		}
		// Draw round railway
		drawRoad(g2, 5, 6, 6, 5, STATION_RAILWAY);
		drawRoad(g2, 5, 10, 6, 11, STATION_RAILWAY);
		drawRoad(g2, 10, 5, 11, 6, STATION_RAILWAY);
		drawRoad(g2, 10, 11, 11, 10, STATION_RAILWAY);

		drawArrows(g2);
	}

	/**
	 * Draw the roads and railways
	 */
	private void drawRoad(Graphics2D g2, int x0, int y0, int x, int y, byte type) {
		g2.setPaint(Color.BLACK);
		// Road
		if (type == STATION_ROAD) {
			Line2D line = new Line2D.Double((x0 + 1) * Game.GRID_UNIT_LENGTH,
					(y0 + 1) * Game.GRID_UNIT_LENGTH, (x + 1)
							* Game.GRID_UNIT_LENGTH, (y + 1)
							* Game.GRID_UNIT_LENGTH);
			g2.draw(line);
		}
		// Railway
		if (type == STATION_RAILWAY) {
			// adjustment for the single lien
			int adjustmentX = 0, adjustmentY = 0;
			if (x == x0) {
				adjustmentX = 2;
			} else if (y == y0) {
				adjustmentY = 2;
			} else { // Round railway
				adjustmentX = 1;
				adjustmentY = 1;
			}
			Line2D line = new Line2D.Double((x0 + 1) * Game.GRID_UNIT_LENGTH
					+ adjustmentX, (y0 + 1) * Game.GRID_UNIT_LENGTH
					+ adjustmentY, (x + 1) * Game.GRID_UNIT_LENGTH
					+ adjustmentX, (y + 1) * Game.GRID_UNIT_LENGTH
					+ adjustmentY);
			g2.draw(line);
			line = new Line2D.Double((x0 + 1) * Game.GRID_UNIT_LENGTH
					- adjustmentX, (y0 + 1) * Game.GRID_UNIT_LENGTH
					- adjustmentY, (x + 1) * Game.GRID_UNIT_LENGTH
					- adjustmentX, (y + 1) * Game.GRID_UNIT_LENGTH
					- adjustmentY);
			g2.draw(line);

			if (adjustmentX == adjustmentY) { // Round railway
				line = new Line2D.Double((x0 + 1) * Game.GRID_UNIT_LENGTH
						+ adjustmentX, (y0 + 1) * Game.GRID_UNIT_LENGTH
						- adjustmentY, (x + 1) * Game.GRID_UNIT_LENGTH
						+ adjustmentX, (y + 1) * Game.GRID_UNIT_LENGTH
						- adjustmentY);
				g2.draw(line);
				line = new Line2D.Double((x0 + 1) * Game.GRID_UNIT_LENGTH
						- adjustmentX, (y0 + 1) * Game.GRID_UNIT_LENGTH
						+ adjustmentY, (x + 1) * Game.GRID_UNIT_LENGTH
						- adjustmentX, (y + 1) * Game.GRID_UNIT_LENGTH
						+ adjustmentY);
				g2.draw(line);
			}

			g2.setPaint(Color.GREEN);
			line = new Line2D.Double((x0 + 1) * Game.GRID_UNIT_LENGTH, (y0 + 1)
					* Game.GRID_UNIT_LENGTH, (x + 1) * Game.GRID_UNIT_LENGTH,
					(y + 1) * Game.GRID_UNIT_LENGTH);
			g2.draw(line);
		}

	}

	/**
	 * Draw path arrows
	 */
	private void drawArrows(Graphics2D g2) {
		int imageIndex = 0;
		int adjust = 8;
		for (int i = 0; i < arrowsList.size(); i++) {
			Movement mm = arrowsList.get(i);
			if (mm.starty() == mm.endy()) {
				adjust = 8;
				if (mm.endx() > mm.startx()) {
					imageIndex = 0;
				} else {
					imageIndex = 4;
				}
			} else if (mm.startx() == mm.endx()) {
				adjust = 8;
				if (mm.endy() > mm.starty()) {
					imageIndex = 6;
				} else {
					imageIndex = 2;
				}
			} else if (mm.endx() > mm.startx() && mm.endy() < mm.starty()) {
				adjust = 12;
				imageIndex = 1;
			} else if (mm.endx() < mm.startx() && mm.endy() < mm.starty()) {
				adjust = 12;
				imageIndex = 3;
			} else if (mm.endx() < mm.startx() && mm.endy() > mm.starty()) {
				adjust = 12;
				imageIndex = 5;
			} else if (mm.endx() > mm.startx() && mm.endy() > mm.starty()) {
				adjust = 12;
				imageIndex = 7;
			}
			int x = (mm.endx() - mm.startx()) * GRID_UNIT_LENGTH / 2
					+ (mm.startx() + 1) * GRID_UNIT_LENGTH - adjust;
			int y = (mm.endy() - mm.starty()) * GRID_UNIT_LENGTH / 2
					+ (mm.starty() + 1) * GRID_UNIT_LENGTH - adjust;

			g2.drawImage(ARROWS_IMAGE[imageIndex], x, y, null);
		}
	}

	/**
	 * Exchange the location of p1, p2
	 */
	public void exchangeChessman(Piece piece1, Piece piece2) {
		int x = piece1.getX();
		int y = piece1.getY();
		piece1.setXY(piece2.getX(), piece2.getY());
		piece2.setXY(x, y);
		// board
		gameBoard.getBoard()[piece1.getX()][piece1.getY()] = piece1.getType();
		gameBoard.getBoard()[x][y] = piece2.getType();
		// widget
		piece1.renderWidget();
		piece2.renderWidget();
	}

	/**
	 * Pieces list
	 */
	public ArrayList<Piece> getPieces() {
		return pieces;
	}

	public void setPieces(ArrayList<Piece> pieces) {
		this.pieces = pieces;
	}

	/**
	 * Get the piece
	 */
	public Piece getPiece(Coordinate c) {
		for (Piece piece : pieces) {
			if (piece.isVisible() && piece.getX() == c.x && piece.getY() == c.y) {
				return piece;
			}
		}
		return null;
	}

	/**
	 * Get the flag
	 */
	public Piece getPieceFlag(Location loc) {
		for (Piece piece : pieces) {
			if (loc == Location.NORTH) {
				if (piece.getType() == FLAG_N) {
					return piece;
				}
			} else if (loc == Location.SOUTH) {
				if (piece.getType() == FLAG_S) {
					return piece;
				}
			} else if (loc == Location.WEST) {
				if (piece.getType() == FLAG_W) {
					return piece;
				}
			} else if (loc == Location.EAST) {
				if (piece.getType() == FLAG_E) {
					return piece;
				}
			}
		}
		return null;
	}

	/**
	 * @return the arrowsList
	 */
	public Vector<Movement> getArrowsList() {
		return arrowsList;
	}

	/**
	 * @param arrowsList
	 *            the arrowsList to set
	 */
	public void setArrowsList(Vector<Movement> arrowsList) {
		this.arrowsList = arrowsList;
	}

	public String getBackgroundImageFile() {
		return backgroundImageFile;
	}

	public void setBackgroundImageFile(String backgroundImageFile) {
		this.backgroundImageFile = backgroundImageFile;
	}

	public CommandBox getCommandBox() {
		return commandBox;
	}

	public void setCommandBox(CommandBox commandBox) {
		this.commandBox = commandBox;
	}

	public boolean isDebugDrawBackgroundImage() {
		return debugDrawBackgroundImage;
	}

	public void setDebugDrawBackgroundImage(boolean debugDrawBackgroundImage) {
		this.debugDrawBackgroundImage = debugDrawBackgroundImage;
	}

	public boolean isDebugDrawBaseline() {
		return debugDrawBaseline;
	}

	public void setDebugDrawBaseline(boolean debugDrawBaseline) {
		this.debugDrawBaseline = debugDrawBaseline;
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Image backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public SaveLineupRadioPane getSaveLineupRadioPane() {
		return saveLineupRadioPane;
	}

	public void setSaveLineupRadioPane(SaveLineupRadioPane saveLineupRadioPane) {
		this.saveLineupRadioPane = saveLineupRadioPane;
	}

	/*
	 * Set the operation buttons displayed on the screen or not
	 */
	public void viewAllOpButtons(boolean flag) {
		for (int i = 0; i < operations.length; i++)
			operations[i].setVisible(flag);
	}

	public void viewOneOpButton(Operations ops, boolean flag) {
		for (int i = 0; i < operations.length; i++) {
			if (operations[i].getOperation() == ops) {
				operations[i].setVisible(flag);
			}
		}
	}

	/**
	 * Enable one button
	 */
	public void enableAllOpButtons(boolean flag) {
		for (int i = 0; i < operations.length; i++)
			operations[i].setEnabled(flag);
	}

	public void enableOneOpButton(Operations ops, boolean flag) {
		for (int i = 0; i < operations.length; i++) {
			if (operations[i].getOperation() == ops) {
				operations[i].setEnabled(flag);
			}
		}
	}
}
