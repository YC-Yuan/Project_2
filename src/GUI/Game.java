package GUI;

import GameData.Lattice.Lattice;
import GameData.Lattice.Monster.Monster;
import GameData.Lattice.Monster.*;
import GameData.Lattice.Player;
import GameData.Map;
import GameData.MotaGame;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Game extends Application {
    //游戏初始化
    private int turn = 0, maxTurn = 0;
    private static int addNum = 0, addTimes = 0;
    private MotaGame motaGame = Map.mapLoad("redo",turn);
    private Player player = motaGame.getPlayer();
    Game() throws FileNotFoundException {}

    //GUI设定
    private final static int LENGTH = 48;
    private final static int PADDLE = 20;
    private final static Insets INSETS = new Insets(PADDLE,PADDLE,PADDLE,PADDLE);
    private Stage stage = new Stage();
    //显示区声明
    private VBox panel = new VBox(), status = new VBox();
    private HBox statusLv = new HBox(), statusAbility = new HBox(), statusKey = new HBox();
    private static Pane display = new Pane();
    private static Text displayText = new Text("");
    private static String displayString = "";
    private static ArrayList<String> text = new ArrayList<>();
    private static final int LINES = 17;
    //游戏区域声明
    private StackPane gameArea = new StackPane();
    private GridPane gamePlayground = new GridPane(), gameBackground = new GridPane();
    private HBox gamePopup = new HBox();
    //按钮区声明
    private HBox buttons = new HBox();
    private Button btSave = new Button("存档<Z>"), btLoad = new Button("读档<X>"),
            btUndo = new Button("撤销<C>"), btRedo = new Button("重做<V>"),
            btInfo = new Button("怪物手册<F>"), btRestart = new Button("重新开始<R>"),
            btBgm = new Button("开关音乐<B>"), btAudio = new Button("开关音效<G>"),
            btHelp = new Button("帮助手册<H>");
    //音乐播放器声明
    private MediaPlayer mediaBGM;
    private double bgmVolume = 0.4, audioVolume = 1;
    //怪物手册声明
    private GridPane monsterInfo = new GridPane();

    //老兵与商人界面声明
    @Override
    public void start(Stage stage) {
        //大构架声明与组件
        HBox hbox = new HBox();
        hbox.getChildren().add(gameArea); hbox.getChildren().add(panel);
        VBox vbox = new VBox();
        vbox.getChildren().add(hbox); vbox.getChildren().add(buttons);
        vbox.setPrefSize(1280,720); vbox.setId("background-VBox"); vbox.getStylesheets().add("GUI/game.css");

        //显示区组件
        panel.getChildren().add(status); panel.getChildren().add(display);
        status.getChildren().add(statusLv); status.getChildren().add(statusAbility); status.getChildren().add(statusKey);
        display.getChildren().add(displayText);
        panelInit(); updateStatus(player);
        //游戏区组件
        gameArea.getChildren().add(gameBackground); gameArea.getChildren().add(gamePlayground);
        gameArea.getChildren().add(gamePopup);
        //按钮区组件
        buttons.getChildren().add(btSave); buttons.getChildren().add(btLoad);
        buttons.getChildren().add(btUndo); buttons.getChildren().add(btRedo);
        buttons.getChildren().add(btInfo); buttons.getChildren().add(btRestart);
        buttons.getChildren().add(btBgm); buttons.getChildren().add(btAudio);
        buttons.getChildren().add(btHelp);
        buttons.setPadding(INSETS); buttons.setSpacing(17); buttons.setTranslateX(-5);
        buttons.setAlignment(Pos.BOTTOM_CENTER); buttonsInit();
        //音乐初始化
        musicBGMPlay("audio/背景音乐.mp3");
        //屏幕设定
        Scene scene = new Scene(vbox);
        //怪物手册和商人老兵弹出框初始化
        monsterInfoInit();
        gameArea.getChildren().add(monsterInfo);
        //游戏初始化
        setGameBackground();
        setGamePlayground(motaGame);
        gamePopupInit();
        //键盘事件
        vbox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) { try { motaGame.move(this,new int[] {-1,0}); } catch (FileNotFoundException ex) { ex.printStackTrace(); } }
            if (e.getCode() == KeyCode.S) { try { motaGame.move(this,new int[] {1,0}); } catch (FileNotFoundException ex) { ex.printStackTrace(); } }
            if (e.getCode() == KeyCode.A) { try { motaGame.move(this,new int[] {0,-1}); } catch (FileNotFoundException ex) { ex.printStackTrace(); } }
            if (e.getCode() == KeyCode.D) { try { motaGame.move(this,new int[] {0,1}); } catch (FileNotFoundException ex) { ex.printStackTrace(); } }
            if (e.getCode() == KeyCode.Z) { try { gameSave(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } }
            if (e.getCode() == KeyCode.X) { try { gameLoad(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } }
            if (e.getCode() == KeyCode.R) { try { gameRestart(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } }
            if (e.getCode() == KeyCode.B) { useBtBGM();}
            if (e.getCode() == KeyCode.G) {
                if (audioVolume == 0) audioVolume = 1;
                else audioVolume = 0;
            }
            if (e.getCode() == KeyCode.C) { try { gameUndo(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } }
            if (e.getCode() == KeyCode.V) { try { gameRedo(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } }
        });
        //按钮事件
        btBgm.setOnAction(event -> {useBtBGM();});
        btAudio.setOnAction(event -> {useBtAudio();});
        btUndo.setOnAction(event -> { try { gameUndo(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } });
        btRedo.setOnAction(event -> { try { gameRedo(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } });
        btSave.setOnAction(event -> { try { gameSave(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } });
        btLoad.setOnAction(event -> { try { gameLoad(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } });
        btRestart.setOnAction(event -> { try { gameRestart(); } catch (FileNotFoundException ex) { ex.printStackTrace(); } });
        btHelp.setOnAction(event -> {});
        btInfo.setOnAction(event -> { try { monsterInfo(motaGame); } catch (CloneNotSupportedException e) { e.printStackTrace(); } });

        stage.setResizable(false);//窗口不能伸缩
        stage.setTitle("Java魔塔");//设定窗口名称
        stage.setScene(scene);
        stage.show();
    }

    //使用怪物手册
    private void monsterInfo(MotaGame motaGame) throws CloneNotSupportedException {
        final int LENGTH = 32;
        monsterInfo.setVisible(true);
        monsterInfo.requestFocus();
        monsterInfo.getChildren().clear();
        Player player = motaGame.getPlayer();
        Player[] players = new Player[9];
        for (int i = 0; i < players.length; i++) {
            players[i] = player.clone();
        }
        Lattice[][][] map = motaGame.getMap();
        Lattice[][][] mapSimulated = map.clone();

        //先检测当前楼层有哪些怪物
        boolean greenSlime = false, redSlime = false, blackSlime = false, bat = false, bigBat = false, skeletonMan = false, skeletonSoldier = false, boss = false, stoneMan = false;
        int floor = player.getFloor();
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                switch (mapSimulated[floor][i][j].getCode()) {
                    case "c":
                        greenSlime = true; break;
                    case "d":
                        redSlime = true; break;
                    case "e":
                        blackSlime = true; break;
                    case "f":
                        bat = true; break;
                    case "F":
                        bigBat = true; break;
                    case "g":
                        skeletonMan = true; break;
                    case "h":
                        skeletonSoldier = true; break;
                    case "x":
                        boss = true; break;
                    case "z":
                        stoneMan = true; break;
                }
            }
        }
        int row = 0;
        //表格上沿
        monsterInfo.add(gettext("怪物名称"),1,0);
        monsterInfo.add(gettext("生命值"),2,0);
        monsterInfo.add(gettext("攻击力"),3,0);
        monsterInfo.add(gettext("防御力"),4,0);
        monsterInfo.add(gettext("金钱"),5,0);
        monsterInfo.add(gettext("经验值"),6,0);
        monsterInfo.add(gettext("模拟损血"),7,0);
        monsterInfo.add(gettext("特殊说明"),8,0);
        row++;
        if (greenSlime) {
            String[] tempString;
            Monster temp = new SlimeGreen();
            tempString = temp.fightPlan(players[0]);
            monsterInfo.add(getImageView(tempString[0],LENGTH),0,row);
            for (int i = 1; i < 9; i++) monsterInfo.add(gettext(tempString[i]),i,row);
            row++;

        }
        if (redSlime) {
            String[] tempString;
            Monster temp = new SlimeRed();
            tempString = temp.fightPlan(players[1]);
            monsterInfo.add(getImageView(tempString[0],LENGTH),0,row);
            for (int i = 1; i < 9; i++) monsterInfo.add(gettext(tempString[i]),i,row);
            row++;
        }
        if (blackSlime) {
            String[] tempString;
            Monster temp = new SlimeBlack();
            tempString = temp.fightPlan(players[2]);
            monsterInfo.add(getImageView(tempString[0],LENGTH),0,row);
            for (int i = 1; i < 9; i++) monsterInfo.add(gettext(tempString[i]),i,row);
            row++;
        }
        if (bat) {
            String[] tempString;
            Monster temp = new BatSmall();
            tempString = temp.fightPlan(players[3]);
            monsterInfo.add(getImageView(tempString[0],LENGTH),0,row);
            for (int i = 1; i < 9; i++) monsterInfo.add(gettext(tempString[i]),i,row);
            row++;
        }
        if (bigBat) {
            String[] tempString;
            Monster temp = new BatLarge();
            tempString = temp.fightPlan(players[4]);
            monsterInfo.add(getImageView(tempString[0],LENGTH),0,row);
            for (int i = 1; i < 9; i++) monsterInfo.add(gettext(tempString[i]),i,row);
            row++;
        }
        if (skeletonMan) {
            String[] tempString;
            Monster temp = new SkeletonMan();
            tempString = temp.fightPlan(players[5]);
            monsterInfo.add(getImageView(tempString[0],LENGTH),0,row);
            for (int i = 1; i < 9; i++) monsterInfo.add(gettext(tempString[i]),i,row);
            row++;
        }
        if (skeletonSoldier) {
            String[] tempString;
            Monster temp = new SkeletonSoldier();
            tempString = temp.fightPlan(players[6]);
            monsterInfo.add(getImageView(tempString[0],LENGTH),0,row);
            for (int i = 1; i < 9; i++) monsterInfo.add(gettext(tempString[i]),i,row);
            row++;
        }
        if (boss) {
            String[] tempString;
            Monster temp = new SkeletonBoss();
            tempString = temp.fightPlan(players[7]);
            monsterInfo.add(getImageView(tempString[0],LENGTH),0,row);
            for (int i = 1; i < 9; i++) monsterInfo.add(gettext(tempString[i]),i,row);
            row++;
        }
        if (stoneMan) {
            String[] tempString;
            Monster temp = new StoneMan();
            tempString = temp.fightPlan(players[8]);
            monsterInfo.add(getImageView(tempString[0],LENGTH),0,row);
            for (int i = 1; i < 9; i++) monsterInfo.add(gettext(tempString[i]),i,row);
        }
    }
    //将字符串添加到右下日志区的text中
    public static void addDisplayText(String add) {
        text.add(add + "\n");
        addNum++;
    }
    //功能函数生成特定大小的ImageView
    private ImageView getImageView(String path,int length) {
        Image image = new Image(path);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(length);
        imageView.setFitHeight(length);
        return imageView;
    }
    //功能函数生成像素字体
    private Text getText(String string) {
        Text text = new Text(string);
        text.setFont(Font.loadFont("file:resources/fonts/swfit.ttf",20));
        return text;
    }
    private Text getText(int num) {
        String string = String.valueOf(num);
        Text text = new Text(string);
        text.setFont(Font.loadFont("file:resources/fonts/swfit.ttf",20));
        return text;
    }
    private Text gettext(String string) {
        Text text = new Text(string);
        text.setFont(Font.loadFont("file:resources/fonts/Zfull-GB.ttf",12));
        return text;
    }
    //从开始界面弹出本窗口
    void showWindow() {
        start(stage);
    }
    //更新弹出框
    public void setGamePopup(Lattice lattice,String string) {
        gamePopup.setVisible(true);
        gamePopup.getChildren().clear();
        gamePopup.getChildren().add(getImageView(lattice.getGraphic(),40));
        Text text = new Text(string);
        text.setFont(Font.loadFont("file:resources/fonts/Zfull-GB.ttf",20));
        gamePopup.getChildren().add(text);
        gamePopup.requestFocus();
    }
    //BGM播放
    private void musicBGMPlay(String paths) {
        Media mediaSource = new Media(Paths.get(paths).toUri().toString());
        mediaBGM = new MediaPlayer(mediaSource);
        mediaBGM.setVolume(bgmVolume);
        mediaBGM.setAutoPlay(true);
        mediaBGM.setCycleCount(40);
    }
    //音效播放
    public void musicAudioPlay(String paths) {
        Media mediaSource = new Media(Paths.get(paths).toUri().toString());
        MediaPlayer mediaAudio = new MediaPlayer(mediaSource);
        mediaAudio.setVolume(audioVolume);
        mediaAudio.setAutoPlay(true);
    }
    //按钮功能打包
    private void useBtBGM() {
        if (bgmVolume == 0) {
            bgmVolume = 0.4;
            mediaBGM.setVolume(bgmVolume);
        }
        else {
            bgmVolume = 0;
            mediaBGM.setVolume(bgmVolume);
        }
    }
    private void useBtAudio() {
        if (audioVolume == 0) audioVolume = 1;
        else audioVolume = 0;
    }
    //生成叠底用GridPane
    private void setGameBackground() {
        gameBackground.setAlignment(Pos.TOP_LEFT);
        gameBackground.setVgap(0);
        gameBackground.setHgap(0);
        gameBackground.setPadding(INSETS);
        Image image;
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                image = new Image("file:pic/Lattice/Lattice.png");
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(LENGTH);
                imageView.setFitHeight(LENGTH);
                gameBackground.add(imageView,j,i);
            }
        }
    }
    //基于现有的MotaGame改变游戏显示
    public void setGamePlayground(MotaGame motaGame) {
        Player player = motaGame.getPlayer();
        Lattice[][][] map = motaGame.getMap();

        gamePlayground.setAlignment(Pos.TOP_LEFT);
        gamePlayground.setVgap(0);
        gamePlayground.setHgap(0);
        gamePlayground.setPadding(INSETS);

        gamePlayground.getChildren().clear();
        Image image;
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 13; j++) {
                if (player.isHere(i,j)) image = new Image(player.getGraphic());
                else image = new Image(map[player.getFloor()][i][j].getGraphic());

                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(LENGTH);
                imageView.setFitHeight(LENGTH);
                gamePlayground.add(imageView,j,i);
            }
        }
    }
    //日志区动态刷新
    public void updateDisplay() {
        int changeNum = addNum;
        addNum = 0;
        if (changeNum > 0) {
            Service<String> service = new Service<String>() {
                @Override
                protected Task<String> createTask() {
                    return new Task<String>() {
                        @Override
                        protected String call() {
                            for (int i = 0; i < changeNum; i++) {
                                displayString = "";
                                if (addTimes >= LINES) {//displayString的增加次数超过line限制
                                    for (int j = 0; j < LINES; j++) {//循环Line次
                                        displayString += text.get(text.size() - (LINES - j) - (changeNum - i) + 3);
                                    }
                                }
                                else {//display只会加Line次以下
                                    for (int j = 0; j < addTimes; j++) {//循环text行数次
                                        displayString += text.get(j);
                                    }
                                }
                                updateMessage(displayString);
                                addTimes++;
                                try { Thread.sleep(120); } catch (InterruptedException e) { e.printStackTrace(); }
                            }
                            return null;
                        }
                    };
                }
            };
            displayText.textProperty().bind(service.messageProperty()); service.restart();
        }
    }
    //游戏存档
    private void gameSave() throws FileNotFoundException {
        Map.mapSave("save",motaGame,0);
    }
    //游戏读档
    private void gameLoad() throws FileNotFoundException {
        motaGame = Map.mapLoad("save",0);
        setGamePlayground(motaGame);
    }
    //撤销
    private void gameUndo() throws FileNotFoundException {
        if (turn > 0) {
            turn--;
            motaGame = Map.mapLoad("redo",turn);
            setGamePlayground(motaGame);
        }//防止过度撤销
    }
    //重做
    private void gameRedo() throws FileNotFoundException {
        if (turn < maxTurn) {
            turn++;
            motaGame = Map.mapLoad("redo",turn);
            setGamePlayground(motaGame);
        }//防止过度重做
    }
    //重启
    public void gameRestart() throws FileNotFoundException {
        motaGame = Map.mapLoad("redo",0);
        setGamePlayground(motaGame);
    }
    //每轮为重做存档
    public void gameSaveForUndo() throws FileNotFoundException {
        updateTurn();
        Map.mapSave("redo",motaGame,turn);
    }
    //轮次增加
    private void updateTurn() {
        turn++;
        maxTurn = Math.max(turn,maxTurn);
    }
    //怪物手册初始化
    private void monsterInfoInit() {
        monsterInfo.setAlignment(Pos.CENTER);
        monsterInfo.setStyle("-fx-background-color:rgba(239,221,173,0.8);");
        monsterInfo.setVisible(false);
        monsterInfo.setHgap(20);
        monsterInfo.setVgap(20);
        monsterInfo.setMaxHeight(400);
        monsterInfo.setMaxWidth(624);
        monsterInfo.setOnKeyPressed(event -> monsterInfo.setVisible(false));
    }
    //弹出框初始化
    private void gamePopupInit() {
        gamePopup.setAlignment(Pos.CENTER);
        gamePopup.setStyle("-fx-background-color:rgba(239,221,173,0.8);");
        gamePopup.setVisible(false);
        gamePopup.setSpacing(40);
        gamePopup.setMaxHeight(120);
        gamePopup.setMaxWidth(624);
        gamePopup.setOnKeyPressed(event -> gamePopup.setVisible(false));
    }
    //按钮初始化
    private void buttonsInit() {
        final int height = 35, width = 123;
        btAudio.setStyle("-fx-font-size: 16;" + "-fx-text-fill:#FFFFFF;" + "-fx-background-image: url('file:pic/Background/按钮背景.jpg');");
        btBgm.setStyle("-fx-font-size: 16;" + "-fx-text-fill:#FFFFFF;" + "-fx-background-image: url('file:pic/Background/按钮背景.jpg');");
        btRestart.setStyle("-fx-font-size: 16;" + "-fx-text-fill:#FFFFFF;" + "-fx-background-image: url('file:pic/Background/按钮背景.jpg');");
        btHelp.setStyle("-fx-font-size: 16;" + "-fx-text-fill:#FFFFFF;" + "-fx-background-image: url('file:pic/Background/按钮背景.jpg');");
        btInfo.setStyle("-fx-font-size: 16;" + "-fx-text-fill:#FFFFFF;" + "-fx-background-image: url('file:pic/Background/按钮背景.jpg');");
        btSave.setStyle("-fx-font-size: 16;" + "-fx-text-fill:#FFFFFF;" + "-fx-background-image: url('file:pic/Background/按钮背景.jpg');");
        btUndo.setStyle("-fx-font-size: 16;" + "-fx-text-fill:#FFFFFF;" + "-fx-background-image: url('file:pic/Background/按钮背景.jpg');");
        btLoad.setStyle("-fx-font-size: 16;" + "-fx-text-fill:#FFFFFF;" + "-fx-background-image: url('file:pic/Background/按钮背景.jpg');");
        btRedo.setStyle("-fx-font-size: 16;" + "-fx-text-fill:#FFFFFF;" + "-fx-background-image: url('file:pic/Background/按钮背景.jpg');");
        btAudio.setPrefSize(width,height); btBgm.setPrefSize(width,height);
        btRestart.setPrefSize(width,height); btHelp.setPrefSize(width,height); btInfo.setPrefSize(width,height);
        btSave.setPrefSize(width,height); btLoad.setPrefSize(width,height);
        btUndo.setPrefSize(width,height); btRedo.setPrefSize(width,height);
    }
    //status更新
    public void updateStatus(Player player) {
        statusLv.getChildren().clear();
        statusAbility.getChildren().clear();
        statusKey.getChildren().clear();
        statusLv.getChildren().add(getImageView("file:pic/Status/等级.png",LENGTH));
        statusLv.getChildren().add(getText(player.getLevel()));
        statusLv.getChildren().add(getImageView("file:pic/Status/经验.png",LENGTH));
        statusLv.getChildren().add(getText(player.getExperience() + "/" + player.getExpNeed()));
        statusAbility.getChildren().add(getImageView("file:pic/Status/血量.png",LENGTH));
        statusAbility.getChildren().add(getText(player.getHealth() + "/" + player.getHealthMax()));
        statusAbility.getChildren().add(getImageView("file:pic/Status/攻击.png",LENGTH));
        statusAbility.getChildren().add(getText(player.getAttack()));
        statusAbility.getChildren().add(getImageView("file:pic/Status/防御.png",LENGTH));
        statusAbility.getChildren().add(getText(player.getDefence()));
        statusAbility.getChildren().add(getImageView("file:pic/Status/金钱.png",LENGTH));
        statusAbility.getChildren().add(getText(player.getMoney()));
        statusKey.getChildren().add(getImageView("file:pic/Status/黄钥匙.png",LENGTH));
        statusKey.getChildren().add(getText(player.getKeyYellowNum()));
        statusKey.getChildren().add(getImageView("file:pic/Status/蓝钥匙.png",LENGTH));
        statusKey.getChildren().add(getText(player.getKeyBlueNum()));
        statusKey.getChildren().add(getImageView("file:pic/Status/红钥匙.png",LENGTH));
        statusKey.getChildren().add(getText(player.getKeyRedNum()));
    }
    //显示区初始化
    private void panelInit() {
        final int LENGTH = 32, width = 596, height = 624;
        //panel
        panel.setStyle("-fx-background-color:rgba(239,221,173,0.8);");
        panel.setMaxSize(width,height);
        panel.setMinSize(width,height);
        panel.setTranslateX(5);
        panel.setTranslateY(20);
        //status
        status.setPadding(INSETS);
        //display
        display.setTranslateX(20);
        display.setTranslateY(5);
        displayText.setFont(Font.font("file:resources/fonts/Zfull-GB.ttf",20));
    }
}
