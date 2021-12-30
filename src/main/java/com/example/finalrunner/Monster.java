package com.example.finalrunner;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import java.util.Random;

public class Monster extends Runner{
    Monster(int initialPosX, int initialPosY, Image image) {
           super(initialPosX, initialPosY, image);

    }
    void drawMonster(Group root){
        imageView = new ImageView(this.image);
        imageView.setX(this.initialPosX);
        imageView.setY(this.initialPosY);
        imageView.setScaleX(0.6);
        imageView.setScaleY(0.6);
        imageView.setRotationAxis(Rotate.Y_AXIS);
        imageView.setRotate(180);
        root.getChildren().add(imageView);
    }

    @Override
    void run(int speed){
        int currentX = (int)imageView.getX();
        currentX-=speed;
        imageView.setX(currentX);
    }

    void recreateNewMonster() {
        if (imageView.getX() < 0) {
            Random random = new Random();
            int p = random.nextInt(650,750);
            imageView.setX(p);
        }
    }
}
