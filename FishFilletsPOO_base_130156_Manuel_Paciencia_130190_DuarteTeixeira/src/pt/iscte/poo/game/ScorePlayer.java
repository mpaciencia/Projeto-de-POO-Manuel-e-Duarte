package pt.iscte.poo.game;


public class ScorePlayer implements Comparable<ScorePlayer> {

	private String playerName;
	private int time;
	private int moves;

	public ScorePlayer(String playerName, int time, int moves) {
		this.playerName = playerName;
		this.time = time;
		this.moves = moves;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public int getTime() {
		return time;
	}

	public int getMoves() {
		return moves;
	}
	
	public void setTime(int time) {
		this.time = time;
	}

	
	@Override
	public int compareTo(ScorePlayer other) {
		
		if (this.time != other.time) {
			return this.time - other.time;
		}
		
		return this.moves - other.moves;
	}

	@Override
	public String toString() {
		
		return "Player: " + playerName + ", Time: " + time + ", Moves: " + moves;
	}
}