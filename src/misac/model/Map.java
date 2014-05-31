package misac.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import misac.model.Elements.Element;
import misac.model.Elements.ElementOwner;
import misac.model.Elements.ElementType;

/**
 * Map contains elements in a grid
 * @author Szabó Levente
 *
 */
public class Map implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int width = 30;
	private int height = 15;
	private Random random = new Random(System.currentTimeMillis());

	// display vars
	private ArrayList<Point> destroyedElements;
	private AtomicInteger destroyedElementDisplayLeft;

	public Element[][] elements;

	public Map()
	{
		destroyedElements = new ArrayList<Point>();
		destroyedElementDisplayLeft = new AtomicInteger(0);
	}
	
	/**
	 * Make new map
	 */
	public void makeMap()
	{
		elements = new Element[height][width];
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				elements[i][j] = Element.fromTypeFactory(ElementType.Empty);
		generateRandomElements();
		generateBases();
		setBorders();
		generateRiver();
	}

	/**
	 * Set borders around the map
	 */
	private void setBorders()
	{
		// don't remove this! GameEngine.calcPower needs this for not checking
		// borders
		for (int i = 0; i < height; i++)
			elements[i][0] = Element.fromTypeFactory(ElementType.Magical);
		for (int i = 0; i < height; i++)
			elements[i][width - 1] = Element
					.fromTypeFactory(ElementType.Magical);
		for (int j = 0; j < width; j++)
			elements[0][j] = Element.fromTypeFactory(ElementType.Magical);
		for (int j = 0; j < width; j++)
			elements[height - 1][j] = Element
					.fromTypeFactory(ElementType.Magical);
	}

	/**
	 * Generates bases at random positons
	 */
	private void generateBases()
	{
		int basePos = random.nextInt(height - 4) + 1;
		for (int i = basePos; i < basePos + 3; i++)
		{
			for (int j = 1; j < 4; j++)
			{
				elements[i][j] = Element.fromTypeAndOwnerFactory(
						ElementType.Base, ElementOwner.Self);
			}
		}
		basePos = random.nextInt(height - 3);
		for (int i = basePos; i < basePos + 3; i++)
		{
			for (int j = width - 4; j < width - 1; j++)
			{
				elements[i][j] = Element.fromTypeAndOwnerFactory(
						ElementType.Base, ElementOwner.Other);
			}
		}
	}

	/**
	 * Generates rivers with random width
	 */
	private void generateRiver()
	{
		int riverWidth = random.nextInt(6) + 1;
		int centerWidth = width / 2 - riverWidth / 2;
		for (int i = 0; i < height; i++)
		{
			for (int j = centerWidth; j < centerWidth + riverWidth; j++)
			{
				Element m = Element.fromTypeFactory(ElementType.Magical);
				elements[i][j] = m;
			}
		}
		// bridges
		int bridgeCount = random.nextInt(2) + 1;
		int bridgeDist = height / (bridgeCount + 1);
		int bridgeWidth = random.nextInt(2) + 1;
		int yShift = -bridgeDist / 2 + random.nextInt(bridgeDist);
		for (int i = bridgeDist + yShift; i < bridgeCount * bridgeDist + yShift
				+ 1; i += bridgeDist)
		{
			for (int j = i - (bridgeWidth / 2); j < i + (bridgeWidth / 2) + 1; j++)
			{
				for (int k = centerWidth; k < centerWidth + riverWidth; k++)
				{
					elements[j][k] = Element.fromTypeFactory(ElementType.Empty);
				}
			}
		}
	}

	/**
	 * Generates random elements at random positions
	 */
	private void generateRandomElements()
	{
		int randomCount = 50;
		for (int i = 0; i < randomCount; i++)
		{
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			if (elements[y][x].getElementType() == ElementType.Empty)
			{
				ElementType type = (new ElementType[] { ElementType.Wood,
						ElementType.Fire, ElementType.Earth, ElementType.Metal,
						ElementType.Water })[random.nextInt(5)];
				elements[y][x] = Element.fromTypeFactory(type);
			}
		}
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	/**
	 * Changes element owners to the oposite
	 * Self -> Other
	 * Other -> Self
	 */
	public void switchOwner()
	{
		for (Element[] el : elements)
			for (Element e : el)
			{
				if (e.getOwner() == ElementOwner.Other)
					e.setOwner(ElementOwner.Self);
				else if (e.getOwner() == ElementOwner.Self)
					e.setOwner(ElementOwner.Other);
			}
	}

	/**
	 * Gets the destroyed element positions
	 * @return The destroyes elements
	 */
	public ArrayList<Point> getDestroyedElements()
	{
		return destroyedElements;
	}

	/**
	 * Add a new position to the destroyed elements for the default time
	 * @param element The element
	 */
	public void addDestroyedElement(Point element)
	{
		addDestroyedElement(element, 80);
	}

	/**
	 * Add a new position to the destroyed elements for a specified time
	 * @param element The element
	 * @param time The time in ms
	 */
	public void addDestroyedElement(Point element, int time)
	{
		destroyedElements.add(element);
		this.destroyedElementDisplayLeft.set(time);
	}

	/**
	 * Removes all destroyed elements
	 */
	public void clearDestroyedElements()
	{
		destroyedElements.clear();
	}

	/**
	 * Decreases destroyed elements left time
	 */
	public void decreaseDestroyedElementDisplayLeft()
	{
		if (this.destroyedElementDisplayLeft.get() > 0)
			this.destroyedElementDisplayLeft.decrementAndGet();
		else
			destroyedElements.clear();
	}
}
