package GameData.Lattice.Roadblock;

import GameData.Lattice.*;

public class DoorYellow extends Lattice {
    public void affectWith(Player player) {
        if(player.getKeyYellowNum()==0){ player.moveCancel();}//没钥匙不能动
        else{player.changeKeyYellowNum(-1);}
    }

    public String getCode() {return "s";}

    public String getGraphic() {return "file:pic/Roadblock/DoorYellow.png";}
}