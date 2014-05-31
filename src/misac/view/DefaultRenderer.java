package misac.view;

import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import misac.controller.*;
import misac.model.*;

/**
 * Default renderer for Misac
 * Displays game in a window
 * Manages menu and gameplay screen
 * @author Szabó Levente
 *
 */
public class DefaultRenderer implements Renderer
{
	private GameModel gameModel;
	private GameEngine gameEngine;

	private JFrame frame;
	private GamePanel gamePanel;
	private GameMenuPanel gameMenuPanel;
	JDialog waitingForClient;

	/**
	 * Added to a GameEngine
	 * Gets gameModel
	 */
	@Override
	public void AddedToGameEngine(GameEngine ge)
	{
		gameModel = ge.gameModel;
		this.gameEngine = ge;

		// make window
		frame = new JFrame("Misac");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		gamePanel = new GamePanel(gameEngine);
		gameMenuPanel = new GameMenuPanel(gameEngine);
		frame.setResizable(false);
		frame.setVisible(true);
		
		JOptionPane optionPane = new JOptionPane(
				"Waiting for client...", JOptionPane.PLAIN_MESSAGE,
				0, null, new Object[] {}, null);	
		waitingForClient = new JDialog();
		waitingForClient.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				gameEngine.stopServer();
				waitingForClient.setVisible(false);
			}
		});
		waitingForClient.setTitle("Message");
		waitingForClient
				.setModalityType(ModalityType.APPLICATION_MODAL);
		waitingForClient.setContentPane(optionPane);
		waitingForClient.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		waitingForClient.pack();
		waitingForClient.setLocationRelativeTo(null);
	}

	/**
	 * Render GameModel
	 */
	@Override
	public void Render()
	{
		switch (gameModel.gameState)
		{
		case Menu:
			if (frame.getContentPane() != gameMenuPanel)
			{
				frame.setContentPane(gameMenuPanel);
				frame.setSize(frame.getPreferredSize());
				frame.setLocationRelativeTo(null);
			}
			break;

		case WaitingForClient:
			if (waitingForClient.isVisible())
				break;
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					waitingForClient.setVisible(true);
				}
			});
			break;

		case Turn:
		case SendMap:
		case OtherPlayerTurn:
		case End:
			if (frame.getContentPane() != gamePanel)
			{
				waitingForClient.setVisible(false);
				frame.setContentPane(gamePanel);
				frame.setSize(frame.getPreferredSize());
				frame.setLocationRelativeTo(null);
			}
			gamePanel.gameRender();
			gamePanel.repaint();
			break;
		default:
			break;
		}
	}
}
