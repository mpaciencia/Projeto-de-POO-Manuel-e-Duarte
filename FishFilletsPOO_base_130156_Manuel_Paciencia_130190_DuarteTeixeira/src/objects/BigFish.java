package objects;


import interfaces.Big;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class BigFish extends GameCharacter implements Big{

	private static BigFish bf = new BigFish(null);
	private String currentImage = "bigFishLeft"; // imagem inicial
	
	private BigFish(Room room) {
		super(room);
	}

	public static BigFish getInstance() {
		return bf;
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
		// Só muda sprite se efetivamente se moveu;
		if (!after.equals(before)) {
			if (dir == Direction.LEFT) {
				currentImage = "bigFishLeft";
			} else if (dir == Direction.RIGHT) {
				currentImage = "bigFishRight";
			}
		}
	}
}
