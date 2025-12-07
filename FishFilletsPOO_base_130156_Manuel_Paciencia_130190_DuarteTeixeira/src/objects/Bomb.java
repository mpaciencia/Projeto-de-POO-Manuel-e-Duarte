package objects;

import interfaces.Explosive;
import interfaces.GravityAffected;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Bomb extends GameObject implements Pushable, Explosive, GravityAffected{

    private boolean isFalling = false;

    public Bomb(Room room){
        super(room);
    }

    public boolean isFalling(){
        return isFalling;
    }

    public void setFalling(boolean falling){
        this.isFalling = falling;
    }

    @Override
    public String getName(){
        return "bomb";
    }

    @Override
    public int getLayer(){
        return 1;
    }

    @Override
    public boolean isPushableBy(GameObject gameObject){
        if(gameObject instanceof GameCharacter)
            return true;
        return false;
    }

    @Override
    public boolean isSupported(){
        Point2D posBelow = this.getPosition().plus(Direction.DOWN.asVector());
        GameObject objBelow = getRoom().getObjectAt(posBelow);
        if(objBelow != null && !(objBelow instanceof Water)){
            return true;
        }
        return false;  
    }
}
