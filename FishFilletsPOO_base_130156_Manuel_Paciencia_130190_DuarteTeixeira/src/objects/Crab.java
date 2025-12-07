package objects;
import java.util.Random;
import interfaces.GravityAffected;
import interfaces.Small;
import interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
//Ele comporta-se como um personagem (move-se), 
//sofre gravidade, é pequeno (passa buracos) e é transponível (para permitir colisão/morte quando os peixes lhe tocam).
public class Crab extends GameCharacter implements GravityAffected, Small{
    public Crab(Room room) {
        super(room);
    }
    private String currentImage = "krabLeft"; // imagem inicial
    
    @Override
    public String getName() {
        return currentImage; 
    }

    @Override
    public int getLayer() {
        return 2;
    }

    @Override
    public boolean isSupported() {
        Point2D posBelow = getPosition().plus(Direction.DOWN.asVector());
        GameObject objBelow = getRoom().getObjectAt(posBelow);
        return objBelow != null && !(objBelow instanceof Water);
    }

    // Lógica de movimento aleatório (apenas Esquerda/Direita)
    public void moveRandom() {
        Direction[] possibleDirs = {Direction.LEFT, Direction.RIGHT};
        int index = new Random().nextInt(possibleDirs.length);
        super.move(possibleDirs[index]);
        if (possibleDirs[index] == Direction.LEFT) {
			currentImage = "krabLeft";
		} else if (possibleDirs[index] == Direction.RIGHT) {
			currentImage = "krabRight";
		}
    }
}