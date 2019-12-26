package GameData.Lattice.Roadblock;

import GUI.Game;
import GameData.Lattice.*;

import java.io.FileNotFoundException;

public class DoorRed extends Lattice {
    public void affectWith(Game game,Player player) throws FileNotFoundException {
        if(player.getKeyRedNum()==0){ player.moveCancel();}//没钥匙不能动
        else{player.changeKeyRedNum(-1);}
        game.musicAudioPlay(getAudio());
        game.gameSaveForUndo();
    }

    public String getCode() {return "w";}
    public String getGraphic() {return "file:pic/Lattice/Roadblock/DoorRed.png";}
    public String getAudio(){return "audio/开门.mp3";}
}