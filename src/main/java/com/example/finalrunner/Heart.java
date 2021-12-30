package com.example.finalrunner;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Heart {
    Image image = new Image("File:///C:\\Users\\Администратор\\IdeaProjects\\FinalRunner\\src\\Images\\Assets\\Heart.png",30,25,false,false);
    ImageView imageView = new ImageView(image);
    ImageView heartView;
    int posX ;
    Heart(int posX) {
        this.heartView = imageView;
        this.posX = posX;
    }

    void draw(Group group) {
        group.getChildren().add(imageView);
        imageView.setX(posX);
    }




}
