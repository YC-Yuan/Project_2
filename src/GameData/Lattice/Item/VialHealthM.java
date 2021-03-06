package GameData.Lattice.Item;

import GUI.Game;
import GameData.Lattice.Lattice;
import GameData.Lattice.Player;

import java.io.FileNotFoundException;

public class VialHealthM extends Lattice {
    public void affectWith(Game game,Player player) throws FileNotFoundException {
        player.changeHealth(200);
        game.musicAudioPlay(getAudio());
        game.gameSaveForUndo();
        game.setGamePopup(this,"捡到中血瓶，恢复200生命");
        Game.addDisplayText("喝下中血瓶，血气方刚！");
    }

    public String getCode() {return "p";}
    public String getGraphic() {return "file:pic/Lattice/Item/VialHealthM.png";}
    public String getAudio() {return "Audio/捡到血瓶.mp3";}
}
