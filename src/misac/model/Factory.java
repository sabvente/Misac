package misac.model;

import java.io.Serializable;

/**
 * Factory that can have elements and updgrades
 * @author Szabó Levente
 *
 */
public class Factory implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int level = 1;
	private int elements = 1;

	public int getLevel()
	{
		return level;
	}

	public int getElements()
	{
		return elements;
	}

	public void useElement()
	{
		elements--;
	}

	public void regenerateElements()
	{
		elements = level;
	}

	public void upgrade()
	{
		level++;
		elements++;
	}
}
