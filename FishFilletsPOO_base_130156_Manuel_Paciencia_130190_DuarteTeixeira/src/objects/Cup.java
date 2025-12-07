package objects;

import interfaces.GravityAffected;
import interfaces.Lightweight;
import interfaces.Pushable;
import interfaces.Small;
import interfaces.Transposable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Cup extends GameObject implements Pushable, Lightweight, Small, GravityAffected{
    public Cup(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "cup";
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
            if(objBelow instanceof Transposable)
                return false;
            return true;
        }
        return false;  
    }
}
