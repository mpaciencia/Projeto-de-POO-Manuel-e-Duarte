package objects;


import interfaces.Small;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class SmallFish extends GameCharacter implements Small{

	private static SmallFish sf = new SmallFish(null);
	private String currentImage = "smallFishLeft"; // adicionado
	
	private SmallFish(Room room) {
		super(room);
	}

	public static SmallFish getInstance() {
		return sf;
	}
	
	@Override
	public String getName() {
		return currentImage;
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void move(Direction dir) {
		Point2D before = getPosition();
		super.move(dir);
		Point2D after = getPosition();
		// Só muda sprite se efetivamente se moveu; ADICIONADO
		if (!after.equals(before)) {
			if (dir == Direction.LEFT) {
				currentImage = "smallFishLeft";
			} else if (dir == Direction.RIGHT) {
				currentImage = "smallFishRight";
			}
		}
	}
}
