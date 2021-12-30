package com.example.finalrunner;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class Game extends Application {
    private Runner runner;
    private int currentFrameRunner = 0;
    private int currentFrameMonster = 0;
    private final int sceneWidth = 700;
    private final int sceneHeight = 230;
    private final ArrayList<Image> images_list_runner = new ArrayList<>();
    private final ArrayList<Image> images_list_monster = new ArrayList<>();
    private final Image [] assets = {new Image("file:///C:/Users/Администратор/IdeaProjects/FinalRunner/src/Images/Buttons/playInit.png",50,50,false,false),
           new Image("file:///C:/Users/Администратор/IdeaProjects/FinalRunner/src/Images/Buttons/playPressed.png",50,50,false,false),
            new Image("file:///C:/Users/Администратор/IdeaProjects/FinalRunner/src/Images/Bg/MenuBack.png",sceneWidth,sceneHeight,false,false),
            new Image("file:///C:/Users/Администратор/IdeaProjects/FinalRunner/src/Images/Buttons/retryInit.png",50,50,false,false),
            new Image("file:///C:/Users/Администратор/IdeaProjects/FinalRunner/src/Images/Buttons/retryPressed.png",50,50,false,false)
            };
    private final Image bg = new Image("file:///C:/Users/Администратор/IdeaProjects/FinalRunner/src/Images/Bg/GameBack.png");
    private ImageView bgView;
    ImageView bgView2;
    int initialX = 0;
    int initialY = sceneHeight - 100;
    int speedOfRunner = 6;
    int speedOfMonster = 10;
    int score = 0;
    int thresh = 200;
    ArrayList<Heart> hearts = new ArrayList<>();
    Heart heart1 = new Heart(650);
    Heart heart2 = new Heart(620);
    Heart heart3 =  new Heart(590);
    int quantityOfMonsters = 1;
    ArrayList<Monster> monsters = new ArrayList<>();
    Timeline timeline;
    Connection connection;
    Statement statement;
    ImageView retryButton;
    Group gameRoot;
    boolean onProcess = false;
    int bestScore = 0;
    @Override
    public void start(Stage stage)  {
        onProcess = true;
        connectToDatabase();
        try {
            bestScore();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        hearts.add(heart1);
        hearts.add(heart2);
        hearts.add(heart3);
        //game
        gameRoot = new Group();
        Scene gameScene = new Scene(gameRoot);
        //menu
        Group menuRoot = new Group();
        Scene menuScene = new Scene(menuRoot);
        ImageView playButton = new ImageView(assets[0]);
        retryButton = new ImageView(assets[3]);
        ImageView promo = new ImageView(assets[2]);
        playButton.setX(300);
        playButton.setY(80);
        retryButton.setX(300);
        retryButton.setY(80);
        Label label = new Label();
        label.setTextFill(Color.rgb(120, 128, 128));
        label.setFont(Font.loadFont("file:///C:\\Users\\Администратор\\IdeaProjects\\FinalRunner\\src\\Images\\Font.ttf", 20));
        label.setLayoutX(5);
        Label bestLabel = new Label("Best score : " + bestScore);
        bestLabel.setTextFill(Color.rgb(218, 247, 166));
        bestLabel.setFont(new Font("Verdana",15));
        menuRoot.getChildren().addAll(promo, playButton,bestLabel);
        bgView = new ImageView(bg);
        bgView2 = new ImageView(bg);
        bgView2.setX(bgView.getImage().getWidth());
        stage.setWidth(sceneWidth);
        stage.setHeight(sceneHeight);
        stage.getIcons().add(new Image("file:///C:\\Users\\Администратор\\IdeaProjects\\FinalRunner\\src\\Images\\Icon\\Icon.png"));
        uploadFiles("Run", images_list_runner);
        uploadFiles("Monster", images_list_monster);
        runner = new Runner(initialX, initialY, images_list_runner.get(0));
        gameRoot.getChildren().addAll(bgView, bgView2, label);
        runner.drawRunner(gameRoot);
        drawHearts(hearts,gameRoot);
        createMonsters(quantityOfMonsters,gameRoot);
        stage.setScene(menuScene);
        stage.setTitle("FinalRunner");
        stage.show();
        //Loop
        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            score += 1;
            label.setText(String.format("Score: %d", score));
            currentFrameRunner = currentFrameRunner + 1;
            if (currentFrameRunner >= images_list_runner.size() - 1) {
                currentFrameRunner = 0;
            }
            runner.frameChange(images_list_runner.get(currentFrameRunner));
            currentFrameMonster = currentFrameMonster + 1;
            if (currentFrameMonster >= images_list_monster.size() - 1) {
                currentFrameMonster = 0;
            }
            scroll();
            runner.run(speedOfRunner);
            for (Monster monster : monsters) {
                monster.run(speedOfMonster);
                monster.recreateNewMonster();
                collide(gameRoot,monster);
            }


        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        //functions
        playButton.setOnMouseEntered(mouseEvent -> playButton.setImage(assets[1]));
        playButton.setOnMouseExited(mouseEvent -> playButton.setImage(assets[0]));
        playButton.setOnMouseClicked(mouseEvent -> {
            stage.setScene(gameScene);
            Timeline wait = new Timeline(new KeyFrame(Duration.seconds(10),e->{
            }));
            wait.setCycleCount(1);
            wait.play();
            timeline.play();
        });
        retryButton.setOnMouseEntered(mouseEvent -> retryButton.setImage(assets[4]));
        retryButton.setOnMouseExited(mouseEvent -> retryButton.setImage(assets[3]));
        retryButton.setOnMouseClicked(mouseEvent -> retry());

        gameScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE  && onProcess) {
                runner.jump();
                uploadFiles("Idle",images_list_runner);
            }
        });
        gameScene.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE  && onProcess) {
                uploadFiles("Run",images_list_runner);
            }
        });
    }


    void uploadFiles(String currentpath,ArrayList<Image>images_list){
        File file = new File(String.format("C:/Users/Администратор/IdeaProjects/FinalRunner/src/Images/%s",currentpath));
        images_list.clear();
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            images_list.add(new Image("file:///" + listFile.toString()));
        }
    }

    void scroll(){
        if (runner.imageView.getX() + runner.imageView.getImage().getWidth() >= thresh  ) {
            bgView.setX(bgView.getX() - 10);
            bgView2.setX(bgView2.getX() - 10);
            speedOfRunner = 0;
            runner.imageView.setX(thresh - runner.imageView.getImage().getWidth());
        }
        else {
            bgView.setX(bgView.getX() - 0);
            speedOfRunner = 6;
        }
        if (bgView.getX() <= -(bgView2.getImage().getWidth())) {
            bgView.setX(bgView2.getImage().getWidth());
        }
        if (bgView2.getX() <= -(bgView.getImage().getWidth())) {
            bgView2.setX(bgView.getImage().getWidth());
        }


    }

    void drawHearts (ArrayList<Heart> hearts,Group group) {
        for (Heart heart : hearts) {
            heart.draw(group);
        }
    }
    void createMonsters (int quantityOfMonsters,Group group) {
        for (int y = 1;y <= quantityOfMonsters;y++) {
            Monster monster = new Monster(660,147,images_list_monster.get(0));
            monster.drawMonster(group);
            monsters.add(monster);
        }
    }

    void collide(Group gameRoot,Monster monster) {
        double monsterY =  monster.imageView.getY() - (monster.imageView.getImage().getHeight() / 2);
        double runnerY =  runner.imageView.getY() - (runner.imageView.getImage().getHeight() / 2);
        double monsterX = monster.imageView.getX() + (monster.imageView.getImage().getWidth() / 2);
        double runnerX = runner.imageView.getX() + (runner.imageView.getImage().getWidth() / 2);
        double distance = Math.sqrt(Math.pow(runnerX - monsterX,2) + Math.pow(runnerY - monsterY,2));
        if (distance <= 40) {
            uploadFiles("Damaged",images_list_runner);
            remove(gameRoot,hearts.get(hearts.size()-1));
            hearts.remove(hearts.size()-1);
            stop(timeline);
        }else {
            uploadFiles("Run",images_list_runner);
        }



    }

    void remove(Group group, Heart heart) {
        group.getChildren().remove(heart.heartView);
    }
    void stop (Timeline timeline) {

        if (hearts.size() <= 0) {
            timeline.stop();
            onProcess = false;
            scoreInsert(score);
            gameRoot.getChildren().add(retryButton);
        }
    }

    void retry () {
        gameRoot.getChildren().remove(retryButton);
        score = 0;
        runner.imageView.setX(runner.initialPosX);
        runner.imageView.setY(runner.initialPosY);
        timeline.play();
        onProcess = true;
        hearts.add(heart1);
        hearts.add(heart2);
        hearts.add(heart3);
        for (Monster monster : monsters) {
            monster.imageView.setX(660);
            monster.imageView.setY(147);
        }
        drawHearts(hearts,gameRoot);

    }

    void scoreInsert (int y) {
        String inserting= String.format("INSERT INTO scores values (%d)",y);
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Objects.requireNonNull(statement).executeUpdate(inserting);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    void bestScore () throws SQLException {
        String query = "SELECT max(score) FROM scores";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            bestScore = resultSet.getInt("max");
        }
        else {
            bestScore = 0;
        }
    }

    void connectToDatabase () {
        String jdbcURL = "jdbc:postgresql://ec2-34-254-120-2.eu-west-1.compute.amazonaws.com:5432/dcrmtoimuop3fk";
        String username = "ddjmsrfqefccnm";
        String password = "3e27bdd148b6fe94e24ed9f4dd548f94c1517dd21fd81b1746fc8097d3f131bb";
        try {
            connection = DriverManager.getConnection(jdbcURL,username,password);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }
}