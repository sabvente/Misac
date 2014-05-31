package misac.controller;

import java.io.*;
import java.net.*;

import misac.model.GameState;

/**
 * Server
 * @author Szabó Levente
 *
 */
public class NetServer
{

	private ServerSocket socket;
	private GameEngine gameEngine;
	private Thread waitConnectionThread;
	private Socket client;

	public NetServer(GameEngine gameEngine)
	{
		this.gameEngine = gameEngine;
	}

	public void init() throws IOException
	{
		socket = new ServerSocket(44244);
	}

	public void handleNet() throws IOException
	{
		switch (gameEngine.gameModel.gameState)
		{
		case WaitingForClient:
			if (waitConnectionThread == null)
			{
				//on new thread, because Socket.accept() is blocking
				waitConnectionThread = new Thread(new Runnable() {
					@Override
					public void run()
					{
						System.out.println("Waiting for client on port "
								+ socket.getLocalPort() + "...");
						try
						{
							client = socket.accept();
							System.out.println("Client connected"
									+ client.getRemoteSocketAddress());
							NetHelper.sendModel(client.getOutputStream(), gameEngine);
						}
						catch(SocketException e) //when user cancels
						{}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				});
				waitConnectionThread.start();
			}
			else if (!waitConnectionThread.isAlive())
			{
				gameEngine.gameModel.gameState = GameState.Turn;
			}
			break;
		case SendMap:
			NetHelper.sendModel(client.getOutputStream(), gameEngine);
			gameEngine.gameModel.gameState = GameState.OtherPlayerTurn;
			break;
		case OtherPlayerTurn:
			InputStream is = client.getInputStream();
			if (is.available() == 0)
				return;
			NetHelper.receiveModel(is, gameEngine);
			break;
		case SendMapAndEnd:
			NetHelper.sendModel(client.getOutputStream(), gameEngine);
			gameEngine.gameModel.gameState = GameState.End;
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
		if (client != null && !client.isClosed())
			client.close();
		if (!socket.isClosed())
		{
			socket.close();
			System.out.println("Closed server on port "
						+ socket.getLocalPort() + "...");
		}
	}
}
