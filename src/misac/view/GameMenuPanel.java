package misac.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import misac.Main;
import misac.controller.GameEngine;

/**
 * Displays main menu
 * @author Szabó Levente
 *
 */
public class GameMenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private GameEngine gameEngine;
	private static Font arialBoldBig;
	private static Image background;
	
	static
	{
		//background
		background = GamePanel.generateNiceBackgroundImage();
		//font
		try
		{
			Font font = Font.createFont(Font.TRUETYPE_FONT,
					Main.class.getResourceAsStream("fonts/arial.ttf"));
			arialBoldBig = font.deriveFont(Font.BOLD, 90);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public GameMenuPanel(GameEngine gameEngine) {
		this.gameEngine = gameEngine;
		setLayout(null);
		Dimension dim = getPreferredSize();
		
		JLabel gameName = new JLabel("Misac");
		gameName.setFont(arialBoldBig);
		gameName.setBounds(23, 10, 300, 100);
		gameName.setForeground(Color.white);
		add(gameName);
		
		JButton host = new JButton("Host");
		host.setBounds(dim.width/2-40, 150, 80, 30);
		host.addActionListener(new HostClickListener());
		add(host);

		JButton join = new JButton("Join");
		join.setBounds(dim.width/2-40, 210, 80, 30);
		join.addActionListener(new JoinClickListener(this));
		add(join);
	}

	public Dimension getPreferredSize() {
		return new Dimension(300, 300);
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(background, 0, 0, null);
	}

	/**
	 * Click listener for host game
	 * @author Szabó Levente
	 *
	 */
	class HostClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				gameEngine.initServer();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Click listener for join game
	 * @author Szabó Levente
	 *
	 */
	class JoinClickListener implements ActionListener {
		private JPanel panel;

		public JoinClickListener(JPanel panel) {
			this.panel = panel;
		}

		public void actionPerformed(ActionEvent e) {
			String ip = (String) JOptionPane.showInputDialog(panel,
					"Type host ip:\n");
			try {
				gameEngine.initClient(ip);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}