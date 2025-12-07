package objects;

import interfaces.Untransposable;
import pt.iscte.poo.game.Room;

public class Wall extends GameObject implements Untransposable{

	public Wall(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "wall";
	}	

	@Override
	public int getLayer() {
		return 1;
	}

}
