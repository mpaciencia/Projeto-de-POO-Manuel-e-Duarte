package objects;

import interfaces.Big;
import interfaces.GravityAffected;
import interfaces.Heavy;
import interfaces.Lightweight;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Stone extends GameObject implements Pushable, Heavy, GravityAffected{
    private boolean hasSpawnedCrab = false;
    public Stone(Room room){
        super(room);
    }
    public boolean hasSpawnedCrab(){
        return hasSpawnedCrab;
    }
    public void setSpawnedCrab(boolean status) {
    this.hasSpawnedCrab = status;
    }

    @Override
    public String getName(){
        return "stone";
    }

    @Override
    public int getLayer(){
        return 1;
    }
    @Override
    public boolean isPushableBy(GameObject gameObject){
        if(gameObject instanceof Big)
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
