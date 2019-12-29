package GameData.Lattice.Monster;

public class SkeletonBoss extends Skeleton {
    public SkeletonBoss() {super(350,150,30,20,250,33,"魔王");}

    public String getCode() {return "x";}
    public String getGraphic() {return "file:pic/Lattice/Monster/SkeletonBoss.png";}
    public String getAudio() {return "audio/骷髅.mp3";}
}
