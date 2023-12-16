/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sebaescu.mavenproject1;

/**
 *
 * @author Sebastian
 */
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        // Agregar una imagen de fondo
        Image backgroundImage = new Image("com/sebaescu/mavenproject1/fondo.png");
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(primaryStage.getWidth());
        backgroundImageView.setFitHeight(primaryStage.getHeight());
        root.getChildren().add(backgroundImageView);

        Label title = new Label("ExploCaves");
        title.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: white;");

        Button facilButton = new Button("Modo Fácil");
        facilButton.setStyle("-fx-background-color: #8FDB8F; -fx-text-fill: white;"); // Verde pastel

        Button dificilButton = new Button("Modo Difícil");
        dificilButton.setStyle("-fx-background-color: #FF6961; -fx-text-fill: white;"); // Rojo pastel

        Button closeButton = new Button("Cerrar");
        closeButton.setStyle("-fx-background-color: #696969; -fx-text-fill: white;"); // Negro pastel

        HBox buttonsBox = new HBox(50, facilButton, dificilButton, closeButton);
        buttonsBox.setAlignment(Pos.CENTER);

        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setAlignment(buttonsBox, Pos.CENTER);
        StackPane.setMargin(title, new javafx.geometry.Insets(50, 0, 0, 0));
        StackPane.setMargin(buttonsBox, new javafx.geometry.Insets(50, 0, 0, 0));

        root.getChildren().addAll(title, buttonsBox);

        Scene scene = new Scene(root, 400, 400);

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) ->
                backgroundImageView.setFitWidth((double) newVal));
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) ->
                backgroundImageView.setFitHeight((double) newVal));

        facilButton.setOnAction(event -> {
            primaryStage.hide();
            App game = new App();
            game.startJuego(new Stage());
        });
        dificilButton.setOnAction(event -> {
            primaryStage.hide();
            App game = new App();
            game.startJuegoDificil(new Stage());
        });
        closeButton.setOnAction(event -> primaryStage.close());

        // Establecer el tamaño máximo de la ventana al maximizado
        primaryStage.setMaximized(true);

        primaryStage.setTitle("ExploCaves");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

