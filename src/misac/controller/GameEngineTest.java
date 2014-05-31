package misac.controller;

import static org.junit.Assert.*;
import misac.model.Elements.Element;
import misac.model.Elements.ElementOwner;
import misac.model.Elements.ElementType;
import misac.view.Renderer;

import org.junit.Test;

public class GameEngineTest
{

	@Test
	public void testSetElement()
	{
		GameEngine e = new GameEngine();
		e.gameModel.map.elements = new Element[15][30];
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 30; j++)
				e.gameModel.map.elements[i][j] = Element
						.fromTypeFactory(ElementType.Empty);
		e.gameModel.map.elements[2][2] = Element.fromTypeAndOwnerFactory(
				ElementType.Fire, ElementOwner.Self);
		e.calculateElementsVisibility();
		e.setElement(2, 3, ElementType.Earth, ElementOwner.Self);

		Element actual = e.gameModel.map.elements[2][3];
		assertEquals(ElementType.Earth, actual.getElementType());
		assertEquals(ElementOwner.Self, actual.getOwner());
	}

	@Test
	public void testUpgradeFactory()
	{
		GameEngine e = new GameEngine();
		e.gameModel.pointsSelf = 0;
		int lvl = e.gameModel.factoriesSelf.get(ElementType.Earth).getLevel();
		e.upgradeFactory(ElementType.Earth);
		assertEquals(lvl, e.gameModel.factoriesSelf.get(ElementType.Earth)
				.getLevel());

		e.gameModel.pointsSelf += Points.factoryCost;
		lvl = e.gameModel.factoriesSelf.get(ElementType.Earth).getLevel();
		e.upgradeFactory(ElementType.Earth);
		assertEquals(lvl + 1, e.gameModel.factoriesSelf.get(ElementType.Earth)
				.getLevel());
	}

	public static boolean rendercalled = false;
	@Test
	public void testRender()
	{
		GameEngine e = new GameEngine();
		e.addRenderer(new Renderer() {
			
			@Override
			public void Render()
			{
				rendercalled = true;
			}
			
			@Override
			public void AddedToGameEngine(GameEngine ge)
			{
				
			}
		});
		try
		{
			//Render called after 33 ms
			Thread.sleep(100);
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		assertTrue(rendercalled);
	}

}
