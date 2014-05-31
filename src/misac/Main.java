package misac;

import javax.swing.SwingUtilities;

import misac.controller.GameEngine;
import misac.view.DefaultRenderer;

/**
 * Main class to launch the GameEngine and add Renderers
 * @author Szabó Levente
 *
 */
public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GameEngine ge = new GameEngine();
				DefaultRenderer renderer = new DefaultRenderer();
				ge.addRenderer(renderer);
			}
		});
	}
}
