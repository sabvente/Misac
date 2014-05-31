package misac.controller;

import java.io.*;
import java.net.*;

import misac.model.GameState;

/**
 * Client
 * @author Szabó Levente
 *
 */
public class NetClient
{
	private Socket socket;
	private GameEngine gameEngine;

	public NetClient(GameEngine gameEngine)
	{
		this.gameEngine = gameEngine;
	}

	public void init(String IP) throws UnknownHostException, IOException
	{
		socket = new Socket(IP, 44244);
		System.out.println("Connected to " + socket.getRemoteSocketAddress());
		InputStream is = socket.getInputStream();
		NetHelper.receiveModel(is, gameEngine);
		gameEngine.gameModel.gameState = GameState.OtherPlayerTurn;
	}

	public void handleNet() throws IOException
	{
		switch (gameEngine.gameModel.gameState)
		{
		case SendMap:
			NetHelper.sendModel(socket.getOutputStream(), gameEngine);
			gameEngine.gameModel.gameState = GameState.OtherPlayerTurn;
			break;
		case OtherPlayerTurn:
			InputStream is = socket.getInputStream();
			if (is.available() != 0)
			{
				NetHelper.receiveModel(is, gameEngine);
			}
			break;
		case End:
			stop();
			break;
		default:
			break;
		}
	}

	public void stop() throws IOException
	{
		if (!socket.isClosed())
		{
			socket.close();
			System.out.println("Closed client on port " + socket.getLocalPort()
					+ "...");
		}
	}
}
