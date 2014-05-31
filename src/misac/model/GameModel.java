package misac.model;

import java.io.Serializable;
import java.util.HashMap;

import misac.model.Elements.ElementType;

/**
 * Game model for storing actual game state
 * @author Szabó Levente
 *
 */
public class GameModel implements Serializable{
	private static final long serialVersionUID = 1L;
	public Map map;
	public GameState gameState;
	public HashMap<ElementType, Factory> factoriesSelf;
	public HashMap<ElementType, Factory> factoriesOther;
	public int pointsSelf = 0;
	public int pointsOther = 0;
	public int time = 0;
	
	public GameModel()
	{
		factoriesSelf = new HashMap<ElementType, Factory>(5);
		factoriesOther = new HashMap<ElementType, Factory>(5);
		map = new Map();
		
		for (ElementType t : new ElementType[]{ElementType.Earth, ElementType.Fire,
				ElementType.Metal, ElementType.Wood, ElementType.Water})
		{
			factoriesSelf.put(t, new Factory());
			factoriesOther.put(t, new Factory());
		}
	}
}
