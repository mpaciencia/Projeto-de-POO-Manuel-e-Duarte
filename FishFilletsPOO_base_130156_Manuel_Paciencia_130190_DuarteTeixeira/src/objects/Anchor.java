package objects;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;
import pt.iscte.poo.game.Room;
import interfaces.Big;
import interfaces.GravityAffected;
import interfaces.Heavy;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;

public class Anchor extends GameObject implements Pushable, Heavy, GravityAffected{
    private boolean moved = false; // Memória individual da âncora
    public Anchor(Room room){
        super(room);
    }
    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
    @Override
    public String getName(){
        return "anchor";
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
