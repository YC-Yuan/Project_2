package GUI;

import GameData.Lattice.Lattice;
import GameData.Lattice.Player;
import GameData.Map;
import GameData.MotaGame;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.Stack;

public class Game extends Application {
    //游戏初始化
    private static int turn = 0, maxTurn = 0;
    private MotaGame motaGame = Map.mapLoad("redo",turn);
    Game() throws FileNotFoundException {}
    //GUI设定
    private final static int LENGTH = 48;
    private final static int PADDLE = 20;
    private final static Insets INSETS = new Insets(PADDLE,PADDLE,PADDLE,PADDLE);
    private Stage stage = new Stage();
    //显示区声明
    private VBox panel = new VBox(), status = new VBox();
    private HBox statusPos = new HBox(), statusHpLv = new HBox(), statusAbility = new HBox(), statusKey = new HBox();
    private Pane display = new Pane();
    private Text displayText = new Text(20,20,"\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    //游戏区域声明
    private StackPane gameArea = new StackPane();
    private GridPane gamePlayground = new GridPane(), gameBackground = new GridPane();
    //按钮区声明
    private HBox buttons = new HBox();
    private Button btSave = new Button("存档<Z>"), btLoad = new Button("读档<X>"),
            btUndo = new Button("撤销<C>"), btRedo = new Button("重做<V>"),
            btInfo = new Button("怪物手册<F>"), btRestart = new Button("重新开始<R>"),
            btBgm = new Button("开关音乐<B>"), btAudio = new Button("开关音效<G>"),
            btHelp = new Button("帮助手册<H>");

    @Override
    public void start(Stage stage) throws Exception {
        //大构架声明与组件
        HBox hbox = new HBox();
        hbox.getChildren().add(gameArea);
        hbox.getChildren().add(panel);
        VBox vbox = new VBox();
        vbox.getChildren().add(hbox); vbox.getChildren().add(buttons);
        vbox.setPrefSize(1280,720);
        vbox.setStyle("-fx-background-image:url('file:pic/Background/背景.jpg')");
        //显示区组件
        panel.getChildren().add(status); status.getChildren().add(display);
        status.getChildren().add(statusPos); status.getChildren().add(statusHpLv);
        status.getChildren().add(statusAbility); status.getChildren().add(statusKey);
        display.getChildren().add(displayText);
        //游戏区域组件
        gameArea.getChildren().add(gameBackground); gameArea.getChildren().add(gamePlayground);
        //按钮区组件
        buttons.getChildren().add(btSave); buttons.getChildren().add(btLoad);
        buttons.getChildren().add(btUndo); buttons.getChildren().add(btRedo);
        buttons.getChildren().add(btInfo); buttons.getChildren().add(btRestart);
        buttons.getChildren().add(btBgm); buttons.getChildren().add(btAudio);
        buttons.getChildren().add(btHelp);
        //显示区设定
        Scene scene = new Scene(vbox);
        //游戏初始化
        setGameBackground();
        setGamePlayground(motaGame);
        //键盘事件
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) motaGame.move(this,new int[] {-1,0});
            if (e.getCode() == KeyCode.S) motaGame.move(this,new int[] {1,0});
            if (e.getCode() == KeyCode.A) motaGame.move(this,new int[] {0,-1});
            if (e.getCode() == KeyCode.D) motaGame.move(this,new int[] {0,1});
        });

        stage.setResizable(false);//窗口不能伸缩
        stage.setTitle("Java魔塔");//设定窗口名称
        stage.setScene(scene);
        stage.show();
    }

    void showWindow() throws Exception {
        start(stage);
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
}