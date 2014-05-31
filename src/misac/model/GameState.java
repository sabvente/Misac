package misac.model;

/**
 * Game states
 * @author Szabó Levente
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
