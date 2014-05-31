package misac.view;

import misac.controller.GameEngine;

/**
 * Render interface for GameEngine
 * @author Szabó Levente
 *
 */
public interface Renderer {
	/**
	 * Called, when added to a GameEngine
	 * @param ge The GameEngine
	 */
	public void AddedToGameEngine(GameEngine ge);
	/**
	 * Renders the GameModel
	 */
	public void Render();
}
