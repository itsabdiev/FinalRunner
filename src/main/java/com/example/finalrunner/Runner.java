package com.example.finalrunner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Runner {
    int initialPosX;
    int initialPosY;
    Image image;
    ImageView imageView;
    Timeline jumpLine;
    int jump_vel = 15;

    Runner(int initialPosX, int initialPosY, Image image){
        this.initialPosX = initialPosX;
        this.initialPosY = initialPosY;
        this.image = image;


    }
    void drawRunner(Group tv){
        imageView = new ImageView(image);
        imageView.setX(initialPosX);
        imageView.setY(initialPosY);
        tv.getChildren().add(imageView);
    }
    void frameChange(Image image){
        imageView.setImage(image);
    }
    void run(int speed){
        int currentX = (int)imageView.getX();
        currentX+=speed;
        imageView.setX(currentX);
    }

    void jump () {
        jumpLine = new Timeline(new KeyFrame(Duration.millis(40), e -> {
            imageView.setY(imageView.getY() - jump_vel);
            jump_vel -= 3;
            if (jump_vel < (-15)) {
                jump_vel = 15;
            }}));
        jumpLine.setCycleCount(11);
        if (imageView.getY() == initialPosY) {
            jumpLine.play();
        }
    }

}
