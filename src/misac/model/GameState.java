package misac.model;

/**
 * Game states
 * @author Szab� Levente
 *
 */
public enum GameState {
	Menu,
	WaitingForClient,
	Turn,
	SendMap,
	OtherPlayerTurn,
	SendMapAndEnd,
	End
}
