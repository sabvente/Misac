package misac.controller;

import java.io.*;

import misac.model.GameModel;

public class NetHelper
{
	/**
	 * Deserialize GameEngine from inputstream
	 * @param s the InputStream
	 * @param gameEngine the GameEngine
	 */
	public static void receiveModel(InputStream s, GameEngine gameEngine)
	{
		try
		{
			ObjectInputStream i = new ObjectInputStream(s);
			gameEngine.loadModelFromServer((GameModel) i.readObject());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Serialize GameEngine from inputstream
	 * @param othe OutputStream
	 * @param gameEngine
	 */
	public static void sendModel(OutputStream os, GameEngine gameEngine)
	{
		try
		{
			ObjectOutputStream o = new ObjectOutputStream(os);
			o.writeObject(gameEngine.gameModel);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
