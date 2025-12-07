package objects;

import pt.iscte.poo.game.Room;
//novo objeto adicional, LER README para explicação adicional.
public class Number8 extends GameObject{
	   public Number8(Room room){
	        super(room);
	    }

	    @Override
	    public String getName(){
	        return "number8";
	    }

	    @Override
	    public int getLayer(){
	        return 1;
	    }
	}

