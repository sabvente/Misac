package misac.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import misac.Main;
import misac.controller.GameEngine;
import misac.model.Factory;
import misac.model.GameModel;
import misac.model.GameState;
import misac.model.Elements.ElementOwner;
import misac.model.Elements.ElementType;

/**
 * Displays the game
 * Manages interact with the GameEngine
 * @author Szabó Levente
 *
 */
public class GamePanel extends JPanel
{
	private static Image background;
	private static HashMap<ElementType, HashMap<ElementOwner, Image>> elementImages;
	private static HashMap<ElementType, ImageIcon> factoryImages;
	private static Image factoryButtonImage;
	private static Font arialBold;
	private static Image hiddenBackground;
	private static Image table;
	private static Image helpImage;
	private static ImageIcon explositionImage;

	private static final long serialVersionUID = 1L;
	private static int marginLeft = 130;
	private static int marginTop = 80;
	private static int elementWidth = 30;

	private HashMap<ElementType, JLabel> factories;
	private HashMap<ElementType, Rectangle> factoryButtons;
	private Rectangle helpButton;
	private Point helpPosition;
	private HashMap<Point, JLabel> explositions;

	private GameModel gameModel;

	private ElementType selected;

	/**
	 * Initializes textures and fonts
	 */
	static
	{
		// background
		URL url = Main.class.getResource("images/background.jpg");
		ImageIcon icon = new ImageIcon(url);
		background = icon.getImage();

		// elements
		elementImages = new HashMap<ElementType, HashMap<ElementOwner, Image>>();
		for (ElementType type : new ElementType[] { ElementType.Wood,
				ElementType.Fire, ElementType.Earth, ElementType.Metal,
				ElementType.Water })
		{
			HashMap<ElementOwner, Image> ownerMap = new HashMap<ElementOwner, Image>();
			for (ElementOwner owner : ElementOwner.values())
			{
				url = Main.class.getResource("images/element" + type.toString()
						+ owner.toString() + ".png");
				icon = new ImageIcon(url);
				Image image = icon.getImage();
				ownerMap.put(owner, image);
			}
			elementImages.put(type, ownerMap);
		}
		HashMap<ElementOwner, Image> ownerMap = new HashMap<ElementOwner, Image>();
		ownerMap.put(
				ElementOwner.Map,
				(new ImageIcon(Main.class
						.getResource("images/elementEmptyMap.png"))).getImage());
		elementImages.put(ElementType.Empty, ownerMap);

		ownerMap = new HashMap<ElementOwner, Image>();
		ownerMap.put(
				ElementOwner.Map,
				(new ImageIcon(Main.class
						.getResource("images/elementMagicalMap.png")))
						.getImage());
		elementImages.put(ElementType.Magical, ownerMap);

		ownerMap = new HashMap<ElementOwner, Image>();
		ownerMap.put(
				ElementOwner.Self,
				(new ImageIcon(Main.class
						.getResource("images/elementBaseSelf.png"))).getImage());
		ownerMap.put(
				ElementOwner.Other,
				(new ImageIcon(Main.class
						.getResource("images/elementBaseOther.png")))
						.getImage());
		elementImages.put(ElementType.Base, ownerMap);

		// hidden elements background
		url = Main.class.getResource("images/hidden_background.jpg");
		icon = new ImageIcon(url);
		hiddenBackground = icon.getImage();

		// table
		url = Main.class.getResource("images/tabla.png");
		icon = new ImageIcon(url);
		table = icon.getImage();

		// factories
		factoryImages = new HashMap<ElementType, ImageIcon>();
		for (ElementType type : new ElementType[] { ElementType.Wood,
				ElementType.Fire, ElementType.Earth, ElementType.Metal,
				ElementType.Water })
		{
			factoryImages.put(
					type,
					new ImageIcon(Main.class.getResource("images/glassGlobe"
							+ type.toString() + ".gif")));
		}

		// factory button
		url = Main.class.getResource("images/factoryButton.png");
		icon = new ImageIcon(url);
		factoryButtonImage = icon.getImage();

		// font
		try
		{
			Font font = Font.createFont(Font.TRUETYPE_FONT,
					Main.class.getResourceAsStream("fonts/arial.ttf"));
			arialBold = font.deriveFont(Font.BOLD, 14);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// help image
		url = Main.class.getResource("images/help.png");
		icon = new ImageIcon(url);
		helpImage = icon.getImage();

		explositionImage = new ImageIcon(
				Main.class.getResource("images/explosition.gif"));
	}

	/**
	 * Initializes java Components and instance variables
	 * @param gameEngine
	 */
	public GamePanel(GameEngine gameEngine)
	{
		this.gameModel = gameEngine.gameModel;

		setLayout(null); // set absolute layout

		// mouse events
		addMouseListener(new MouseListener(gameEngine));

		// factories
		factories = new HashMap<ElementType, JLabel>(5);
		factoryButtons = new HashMap<ElementType, Rectangle>();
		int x = 14;
		int y = marginTop - 30;
		for (ElementType type : new ElementType[] { ElementType.Wood,
				ElementType.Fire, ElementType.Earth, ElementType.Metal,
				ElementType.Water })
		{
			// JLabels
			ImageIcon ii = factoryImages.get(type);
			JLabel imageLabel = new JLabel();
			imageLabel.setBounds(x, y, ii.getIconWidth() + 2,
					ii.getIconHeight() + 2);
			imageLabel.setIcon(ii);
			imageLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 0,
					0, 0)));
			add(imageLabel);
			factories.put(type, imageLabel);

			// factory buttons
			factoryButtons.put(type,
					new Rectangle(x, y + ii.getIconHeight() + 8,
							factoryButtonImage.getWidth(null),
							factoryButtonImage.getHeight(null)));
			y += ii.getIconHeight() + 40;
		}
		selected = ElementType.Wood;
		selectFactory(selected);

		// help button
		helpButton = new Rectangle(150, 20, factoryButtonImage.getWidth(null),
				factoryButtonImage.getHeight(null));

		// explosition JLabels
		explositions = new HashMap<Point, JLabel>();
	}

	/**
	 * Gets a basic element Rectangle by coordinates
	 * @param i X coordinate
	 * @param j Y coordinate
	 * @return Returns the Rectangle
	 */
	private static Rectangle GetElementRect(int i, int j)
	{
		return new Rectangle(marginLeft + j * elementWidth, marginTop + i
				* elementWidth, elementWidth, elementWidth);
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(1150, 600);
	}

	/**
	 * Main drawing here
	 */
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Font defaultFont = g.getFont();
		g.setFont(arialBold);
		g.setColor(Color.WHITE);

		g.drawImage(background, 0, 0, null);

		g.drawString("Points: " + gameModel.pointsSelf, 25, 30);
		g.drawString("Time left: " + gameModel.time, 500, 30);

		// factory buttons
		g.setFont(defaultFont);
		for (ElementType type : new ElementType[] { ElementType.Wood,
				ElementType.Fire, ElementType.Earth, ElementType.Metal,
				ElementType.Water })
		{
			Rectangle rect = factoryButtons.get(type);
			g.drawImage(factoryButtonImage, rect.x, rect.y,
					(int) rect.getWidth(), (int) rect.getHeight(), null);
			Factory f = gameModel.factoriesSelf.get(type);
			g.setColor(Color.WHITE);
			g.drawString(f.getElements() + " e - lvl" + f.getLevel(),
					rect.x + 7, rect.y + 13);
		}

		// help button
		g.drawImage(factoryButtonImage, helpButton.x, helpButton.y,
				(int) helpButton.getWidth(), (int) helpButton.getHeight(), null);
		g.setColor(Color.WHITE);
		g.drawString("Help", helpButton.x + 20, helpButton.y + 13);

		int mapWidth = gameModel.map.getWidth();
		int mapHeight = gameModel.map.getHeight();

		// elements background
		g.drawImage(hiddenBackground, marginLeft - 30, marginTop - 30, mapWidth
				* elementWidth + 60, mapHeight * elementWidth + 60, null);
		// elements
		g.setFont(arialBold);
		for (int i = 0; i < gameModel.map.getHeight(); i++)
		{
			for (int j = 0; j < gameModel.map.getWidth(); j++)
			{
				// Check if element is visible for player
				Rectangle r = GetElementRect(i, j);
				if (gameModel.map.elements[i][j].isVisible())
				{
					String power = ((Byte) gameModel.map.elements[i][j]
							.getPower()).toString();
					ElementType type = gameModel.map.elements[i][j]
							.getElementType();
					ElementOwner owner = gameModel.map.elements[i][j]
							.getOwner();
					g.drawImage(elementImages.get(type).get(owner), r.x, r.y,
							elementWidth, elementWidth, null);

					// draw power
					if (type != ElementType.Empty
							&& type != ElementType.Magical
							&& type != ElementType.Base)
					{
						g.setColor(Color.black);
						g.drawString(power, r.x + 8, r.y + 20);
						g.setColor(Color.white);
						g.drawString(power, r.x + 7, r.y + 19);
					}
				}
			}
		}
		g.setFont(defaultFont);
		// draw end game
		if (gameModel.gameState == GameState.End)
			drawMessageAndButton(g, "Game over\n Click to exit.");
		else if (gameModel.gameState == GameState.OtherPlayerTurn)
			drawMessageAndButton(g, "Wait for the other player!");

		/*
		 * //draw gameState g.setColor(Color.white);
		 * g.drawString(gameModel.gameState.toString(), 300, 40);
		 */

		// draw help
		if (helpPosition != null)
			g.drawImage(helpImage, helpPosition.x, helpPosition.y, null);
	}

	/**
	 * Draws a nice messagebox thingy
	 * @param g The Graphics to draw to
	 * @param msg The message
	 */
	private void drawMessageAndButton(Graphics g, String msg)
	{
		// draw panel
		Dimension dim = getPreferredSize();
		Rectangle topLeft = new Rectangle(dim.width / 2 - table.getWidth(null)
				/ 2, dim.height / 2 - table.getHeight(null) / 2,
				table.getWidth(null), table.getHeight(null));
		g.drawImage(table, topLeft.x, topLeft.y, topLeft.width, topLeft.height,
				null);
		g.setColor(Color.white);
		g.drawString(msg, topLeft.x + 70, topLeft.y + 70);
	}

	/**
	 * Generates a nice background image
	 * @return Returns a 500x500 Image
	 */
	public static Image generateNiceBackgroundImage()
	{
		Random random = new Random(System.currentTimeMillis());
		BufferedImage img = new BufferedImage(500, 500,
				BufferedImage.TYPE_INT_ARGB);

		Graphics g = img.createGraphics();
		for (int i = 0; i < 30; i++)
		{
			for (int j = 0; j < 30; j++)
			{
				Rectangle r = new Rectangle(j * elementWidth, i * elementWidth,
						elementWidth, elementWidth);;
				ElementType type = (new ElementType[] { ElementType.Wood,
						ElementType.Fire, ElementType.Earth, ElementType.Metal,
						ElementType.Water })[random.nextInt(5)];
				g.drawImage(elementImages.get(type).get(ElementOwner.Map), r.x,
						r.y, elementWidth, elementWidth, null);
			}
		}
		g.setColor(new Color(0, 0, 0, 100));
		g.fillRect(0, 0, 500, 500);
		return img;
	}

	/**
	 * Selects a factory by it's type
	 * @param type The type
	 */
	private void selectFactory(ElementType type)
	{
		// restore previously selected factory
		JLabel prevSelectedLabel = factories.get(selected);
		prevSelectedLabel.setBorder(BorderFactory.createLineBorder(new Color(0,
				0, 0, 0)));
		selected = type;
		// set new size
		JLabel selectedLabel = factories.get(type);
		selectedLabel.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	/** Updates explositions (destroyed elements)
	 * Called when game requests render
	 */
	public void gameRender()
	{
		// add recently created explositions
		ArrayList<Point> c = new ArrayList<Point>();
		c.addAll(gameModel.map.getDestroyedElements());
		c.removeAll(explositions.keySet());
		for (Point p : c)
		{
			JLabel imageLabel = new JLabel();
			Rectangle r = GetElementRect(p.x, p.y);
			imageLabel.setBounds(r.x - 5, r.y - 6,
					explositionImage.getIconWidth(),
					explositionImage.getIconHeight());
			imageLabel.setIcon(explositionImage);
			imageLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 0,
					0, 0)));
			add(imageLabel);
			explositions.put(p, imageLabel);
		}
		// remove destroyed explositions
		ArrayList<Point> d = new ArrayList<Point>();
		d.addAll(explositions.keySet());
		d.removeAll(gameModel.map.getDestroyedElements());
		for (Point p : d)
		{
			remove(explositions.get(p));
			explositions.remove(p);
		}
	}

	/**
	 * Handles mouse clicks
	 * @author Szabó Levente
	 *
	 */
	private class MouseListener extends MouseAdapter
	{
		private GameEngine gameEngineCopy;

		public MouseListener(GameEngine gameEngineCopy)
		{
			this.gameEngineCopy = gameEngineCopy;
		}

		public void mousePressed(MouseEvent e)
		{
			if (gameEngineCopy.gameModel.gameState == GameState.End)
				gameEngineCopy.exitToMenu();
			if (gameEngineCopy.gameModel.gameState == GameState.Turn)
			{
				// check elements
				outer: for (int i = 0; i < gameEngineCopy.gameModel.map
						.getHeight(); i++)
				{
					for (int j = 0; j < gameEngineCopy.gameModel.map.getWidth(); j++)
					{
						Rectangle r = GetElementRect(i, j);
						if (r.contains(e.getX(), e.getY()))
						{
							if (e.getButton() == MouseEvent.BUTTON1)
								gameEngineCopy.setElement(i, j, selected,
										ElementOwner.Self);
							// break outer loop
							break outer;
						}
					}
				}

				// check factory buttons (upgrade)
				for (Entry<ElementType, Rectangle> f : factoryButtons
						.entrySet())
				{
					if (f.getValue().contains(e.getX(), e.getY()))
					{
						gameEngineCopy.upgradeFactory(f.getKey());
						break;
					}
				}
			}
			
			// check factory selection
			for (Entry<ElementType, JLabel> f : factories.entrySet())
			{
				Rectangle rect = f.getValue().getBounds();
				Ellipse2D circle = new Ellipse2D.Float(rect.x, rect.y,
						rect.width, rect.height);
				if (circle.contains(e.getX(), e.getY()))
				{
					selectFactory(f.getKey());
					break;
				}
			}

			// check help button
			if (helpButton.contains(e.getX(), e.getY()))
				helpPosition = e.getPoint();
			else
				helpPosition = null;
		}
	}
}