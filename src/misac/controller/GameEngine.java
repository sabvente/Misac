package misac.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.Timer;

import misac.model.*;
import misac.model.Elements.Element;
import misac.model.Elements.ElementOwner;
import misac.model.Elements.ElementType;
import misac.view.*;

/**
 * Responsable for the main game logic
 * 
 * @author Szabó Levente
 *
 */
public class GameEngine
{
	public GameModel gameModel;
	public boolean isServer;
	private ArrayList<Renderer> renderers;
	private NetServer netServer;
	private NetClient netClient;

	public static int Version = 1;
	public static int turnTime = 60;

	public GameEngine()
	{
		gameModel = new GameModel();
		renderers = new ArrayList<Renderer>();
		gameModel.gameState = GameState.Menu;
		// repaint loop
		ActionListener update = new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				update();
				render();
			}
		};
		new Timer(33, update).start();
		// millisec timer
		ActionListener time = new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				gameModel.map.decreaseDestroyedElementDisplayLeft();
			}
		};
		new Timer(1, time).start();
		// millisec timer
		ActionListener sdf = new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				if (gameModel.gameState == GameState.Turn)
					gameModel.time--;
			}
		};
		new Timer(1000, sdf).start();

	}
	/**
	 * 
	 * @param i X coordinate
	 * @param j Y coordinate
	 * @param type Element type to set
	 * @param owner Element owner to set
	 */
	public void setElement(int i, int j, ElementType type, ElementOwner owner)
	{
		if (!gameModel.map.elements[i][j].isVisible())
			return;
		if (gameModel.factoriesSelf.get(type).getElements() == 0)
			return;
		ElementType oldType = gameModel.map.elements[i][j].getElementType();
		if (oldType == ElementType.Base
				|| oldType == ElementType.Magical)
			return;
		gameModel.map.elements[i][j] = Element.fromTypeAndOwnerFactory(type,
				owner);
		calcPower(i, j);
		calculateElementsVisibility();
		if (isGameOver())
			gameModel.gameState = GameState.SendMapAndEnd;
		else
		{
			gameModel.factoriesSelf.get(type).useElement();
			// check if any elements remaining
			int remainingElements = 0;
			for (Entry<ElementType, Factory> e : gameModel.factoriesSelf
					.entrySet())
			{
				remainingElements += e.getValue().getElements();
			}
			if (remainingElements == 0)
				gameModel.gameState = GameState.SendMap;
		}
	}

	/**
	 * Updgrades a factory 
	 * @param type The type of factory to upgrade
	 */
	public void upgradeFactory(ElementType type)
	{
		Factory f = gameModel.factoriesSelf.get(type);
		if (gameModel.pointsSelf >= Points.factoryCost)
		{
			gameModel.pointsSelf -= Points.factoryCost;
			f.upgrade();
		}
	}
	
	/**
	 * Checks if it is game over.
	 * @return true if the game is over
	 */
	private boolean isGameOver()
	{
		// count bases
		int countOwner = 0;
		for (Element[] el : gameModel.map.elements)
			for (Element e : el)
				if (e.getElementType() == ElementType.Base
						&& e.getOwner() == ElementOwner.Self)
					countOwner++;
		int countOther = 0;
		for (Element[] el : gameModel.map.elements)
			for (Element e : el)
				if (e.getElementType() == ElementType.Base
						&& e.getOwner() == ElementOwner.Other)
					countOther++;
		return ((countOwner == 0) || (countOther == 0));
	}

	/**
	 * Calculates power for the element at the given coordinate
	 * @param i X coordinate
	 * @param j Y coordinate
	 */
	private void calcPower(int i, int j)
	{
		calcPower(i, j, new ArrayList<Point>());
	}

	/**
	 * Calculates power for the element at the given coordinate
	 * Should not called directly. Use {@link #calcPower(int, int)} instead.
	 * @param i X coordinate
	 * @param j Y coordinate
	 * @param alreadyChecked Already checked elements
	 */
	// recursive
	private void calcPower(int i, int j, ArrayList<Point> alreadyChecked)
	{
		ElementType typeOfThis = gameModel.map.elements[i][j].getElementType();
		// don't check empty or magical
		if (typeOfThis == ElementType.Empty
				|| typeOfThis == ElementType.Magical)
			return;
		// prevent infinite loop
		if (alreadyChecked.contains(new Point(i, j)))
			return;
		byte powerOfThis = gameModel.map.elements[i][j].getDefaultPower();
		powerOfThis += getPointChange(typeOfThis,
				gameModel.map.elements[i - 1][j]);
		powerOfThis += getPointChange(typeOfThis,
				gameModel.map.elements[i + 1][j]);
		powerOfThis += getPointChange(typeOfThis,
				gameModel.map.elements[i][j - 1]);
		powerOfThis += getPointChange(typeOfThis,
				gameModel.map.elements[i][j + 1]);
		if (powerOfThis < 1)
		{
			// element didn't made it :(
			if (gameModel.map.elements[i][j].getOwner() == ElementOwner.Self)
				gameModel.pointsSelf += Points.forDestroyingSelfElement;
			if (gameModel.map.elements[i][j].getOwner() == ElementOwner.Other)
				gameModel.pointsSelf += Points.forDestroyingOtherElement;
			if (gameModel.map.elements[i][j].getOwner() == ElementOwner.Map)
				gameModel.pointsSelf += Points.forDestroyingMapElement;
			gameModel.map.elements[i][j] = Element
					.fromTypeFactory(ElementType.Empty);
			// element destroyed. check others near it with new already checked.
			calcPower(i - 1, j);
			calcPower(i + 1, j);
			calcPower(i, j - 1);
			calcPower(i, j + 1);
			gameModel.map.addDestroyedElement(new Point(i, j));
			return;
		}
		else if (powerOfThis != gameModel.map.elements[i][j].getPower())
		{
			if (powerOfThis > gameModel.map.elements[i][j].getPower()
					&& gameModel.map.elements[i][j].getOwner() == ElementOwner.Self)
				gameModel.pointsSelf += Points.forSelfElementPowerUp;
			if (powerOfThis < gameModel.map.elements[i][j].getPower()
					&& gameModel.map.elements[i][j].getOwner() == ElementOwner.Other)
				gameModel.pointsSelf += Points.forOtherElementPowerDown;
			gameModel.map.elements[i][j]
					.setPower(powerOfThis <= 10 ? powerOfThis : 10);
		}

		alreadyChecked.add(new Point(i, j));
		calcPower(i - 1, j, alreadyChecked);
		calcPower(i + 1, j, alreadyChecked);
		calcPower(i, j - 1, alreadyChecked);
		calcPower(i, j + 1, alreadyChecked);
	}
	
	/**
	 * Get the point delta
	 * @param typeOfThis Type of the first element.
	 * @param other The new element.
	 * @return The delta change of points
	 */
	private int getPointChange(ElementType typeOfThis, Element other)
	{
		int value = 0;
		if (other.isGenerate(typeOfThis))
			value += other.getPower();
		if (other.isOvercome(typeOfThis))
			value -= other.getPower();
		return value;
	}

	/**
	 * Resets the game model to start a new game.
	 */
	private void resetGameModel()
	{
		gameModel.map.makeMap();
		// Calc power of random elements and Bases
		for (int i = 1; i < gameModel.map.getHeight() - 1; i++)
			for (int j = 1; j < gameModel.map.getWidth() - 1; j++)
				calcPower(i, j);

		gameModel.pointsSelf = 0;
		gameModel.pointsOther = 0;
		calculateElementsVisibility();
		gameModel.map.clearDestroyedElements();
		gameModel.time = turnTime;
	}

	/**
	 * Initializes the server.
	 * @throws IOException IOException is thrown when networking error occures.
	 */
	public void initServer() throws IOException
	{
		resetGameModel();
		netServer = new NetServer(this);
		netServer.init();
		isServer = true;
		gameModel.gameState = GameState.WaitingForClient;
	}

	/**
	 * Initializes the client.
	 * @param IP The server IP to connect to
	 * @throws UnknownHostException UnknownHostException is thrown when the host is not present.
	 * @throws IOException IOException is thrown when networking error occures.
	 */
	public void initClient(String IP) throws UnknownHostException, IOException
	{
		netClient = new NetClient(this);
		netClient.init(IP);
		isServer = false;
		gameModel.gameState = GameState.OtherPlayerTurn;
	}

	/**
	 * Calculate each element's visibility in the map.
	 */
	public void calculateElementsVisibility()
	{
		for (int i = 0; i < gameModel.map.getHeight(); i++)
			for (int j = 0; j < gameModel.map.getWidth(); j++)
			{
				boolean visible = false;
				if (gameModel.map.elements[i][j].getOwner() == ElementOwner.Self)
					visible = true;
				else
				{
					// horizontal check
					outerLoop: for (int k = -3; k < 4; k++)
						// -2 .. 2
						for (int l = -2; l < 3; l++)
							// -1 .. 1
							if (i + k >= 0 && i + k < gameModel.map.getHeight()
									&& j + l >= 0
									&& j + l < gameModel.map.getWidth())
								if (gameModel.map.elements[i + k][j + l]
										.getOwner() == ElementOwner.Self)
								{
									visible = true;
									break outerLoop;
								}
					// vertical check
					outerLoop: for (int k = -3; k < 4; k++)
						for (int l = -2; l < 3; l++)
							if (j + k >= 0 && j + k < gameModel.map.getWidth()
									&& i + l >= 0
									&& i + l < gameModel.map.getHeight())
								if (gameModel.map.elements[i + l][j + k]
										.getOwner() == ElementOwner.Self)
								{
									visible = true;
									break outerLoop;
								}

				}
				gameModel.map.elements[i][j].setVisibiliy(visible);
			}
	}

	/**
	 * Loads the incoming model
	 * @param other The incoming model
	 */
	public void loadModelFromServer(GameModel other)
	{
		gameModel.map = other.map;
		gameModel.factoriesSelf = other.factoriesOther;
		gameModel.factoriesOther = other.factoriesSelf;
		gameModel.pointsSelf = other.pointsOther;
		gameModel.pointsOther = other.pointsSelf;
		gameModel.map.switchOwner();
		if (other.gameState == GameState.SendMapAndEnd)
			gameModel.gameState = GameState.End;
		else
			gameModel.gameState = GameState.Turn;
		for (Entry<ElementType, Factory> f : gameModel.factoriesSelf.entrySet())
		{
			f.getValue().regenerateElements();
		}
		calculateElementsVisibility();
		gameModel.map.clearDestroyedElements();
		gameModel.time = turnTime;
	}

	/**
	 * Adds a renderer
	 * Note: Observer pattern
	 * @param renderer
	 */
	public void addRenderer(Renderer renderer)
	{
		renderers.add(renderer);
		renderer.AddedToGameEngine(this);
	}
	
	/**
	 * Updates
	 */
	private void update()
	{
		//time over
		if(gameModel.gameState == GameState.Turn && gameModel.time == 0)
		{
			gameModel.gameState = GameState.SendMap;
			gameModel.time = turnTime;
		}
		try
		{
			if (netServer != null)
			{
				try
				{
					netServer.handleNet();
				}
				catch (SocketException e)
				{
					// network error
					netServer.stop();
					gameModel.gameState = GameState.Menu;
				}
			}
			else if (netClient != null)
			{
				try
				{
					netClient.handleNet();
				}
				catch (SocketException e)
				{
					// network error
					netClient.stop();
					gameModel.gameState = GameState.Menu;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Renders each renderer
	 */
	private void render()
	{
		for (Renderer r : renderers)
		{
			r.Render();
		}
	}

	/**
	 * Exits to the menu
	 */
	public void exitToMenu()
	{
		gameModel.gameState = GameState.Menu;
	}

	/**
	 * Stops the server (when cancelling)
	 */
	public void stopServer()
	{
		try
		{
			netServer.stop();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		gameModel.gameState = GameState.Menu;
	}
}
