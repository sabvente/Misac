package misac.view;

import static org.junit.Assert.*;
import misac.controller.GameEngine;

import org.junit.Test;

public class GamePanelTest
{

	@Test
	public void testGamePanel()
	{
		GameEngine e = new GameEngine();
		GamePanel p = new GamePanel(e);
		assertNotNull(p);
	}

	@Test
	public void testGetPreferredSize()
	{
		GameEngine e = new GameEngine();
		GamePanel p = new GamePanel(e);
		assertNotNull(p.getPreferredSize());
	}

	@Test
	public void testGenerateNiceBackgroundImage()
	{
		assertNotNull(GamePanel.generateNiceBackgroundImage());
	}

}
