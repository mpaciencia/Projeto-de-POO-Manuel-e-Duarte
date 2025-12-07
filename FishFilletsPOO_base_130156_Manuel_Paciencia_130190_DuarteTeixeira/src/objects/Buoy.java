package objects;

import interfaces.Big;
import interfaces.Floatable;
import interfaces.Lightweight;
import interfaces.Pushable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

public class Buoy extends GameObject implements Pushable, Lightweight, Floatable{
    public Buoy(Room room){
        super(room);
    }

    @Override
    public String getName(){
        return "buoy";
    }

    @Override
    public int getLayer(){
        return 1;
    }

    @Override
    public boolean isPushableBy(GameObject gameObject){
        //empurrado na vertical por ambos na horizontal
        //empurrado na horizontal pelo peixe grande

        Point2D pusherPos = gameObject.getPosition();
        Point2D myPos = this.getPosition();
        //verificar se é horizontal
        if(pusherPos.getY() == myPos.getY()){
            return true;
        }
        if(pusherPos.getY() < myPos.getY()){
            return gameObject instanceof Big;
        }


        if(gameObject instanceof GameCharacter)
            return true;
        return false;
    }
}
