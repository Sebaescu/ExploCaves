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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainMenu extends Application {

    private static final double BUTTON_FONT_SIZE = 20;
    private static final double TITLE_FONT_SIZE = 50;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        // Agregar una imagen de fondo
        Image backgroundImage = new Image("com/sebaescu/mavenproject1/fondo.png");
        ImageView backgroundImageView = new ImageView(backgroundImage);
        root.getChildren().add(backgroundImageView);

        Label title = new Label("ExploCaves");
        title.setStyle("-fx-font-size: " + TITLE_FONT_SIZE + "; -fx-font-weight: bold; -fx-text-fill: white;");

        Button facilButton = createStyledButton("Modo Fácil", "#8FDB8F");
        Button dificilButton = createStyledButton("Modo Difícil", "#FF6961");
        Button closeButton = createStyledButton("Cerrar", "#696969");

        HBox buttonsBox = new HBox(20, facilButton, dificilButton, closeButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(20, title, buttonsBox);
        vbox.setAlignment(Pos.CENTER);

        // Ajustar el tamaño del VBox para que ocupe todo el espacio disponible
        VBox.setVgrow(buttonsBox, Priority.ALWAYS);

        StackPane.setAlignment(vbox, Pos.CENTER);
        StackPane.setMargin(vbox, new javafx.geometry.Insets(50, 0, 0, 0));

        root.getChildren().addAll(vbox);

        // Escuchar cambios en el tamaño de la ventana
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            backgroundImageView.setFitWidth(newVal.doubleValue());
            updateFontSize(primaryStage, title, facilButton, dificilButton, closeButton);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            backgroundImageView.setFitHeight(newVal.doubleValue());
            updateFontSize(primaryStage, title, facilButton, dificilButton, closeButton);
        });

        primaryStage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            updateFontSize(primaryStage, title, facilButton, dificilButton, closeButton);
        });

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
        // Usar dimensiones iniciales de la ventana
        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createStyledButton(String text, String backgroundColor) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white;");
        button.setMinSize(200, 50);
        setButtonFontSize(button, BUTTON_FONT_SIZE);
        return button;
    }

    private void setButtonFontSize(Button button, double fontSize) {
        button.setFont(new Font("Arial", fontSize));
    }

    private void updateFontSize(Stage stage, Label title, Button facilButton, Button dificilButton, Button closeButton) {
        double scaleFactor = stage.isMaximized() ? 1.5 : 1.0;

        title.setStyle("-fx-font-size: " + (TITLE_FONT_SIZE * scaleFactor) +
                "; -fx-font-weight: bold; -fx-text-fill: white;");

        setButtonFontSize(facilButton, BUTTON_FONT_SIZE * scaleFactor);
        setButtonFontSize(dificilButton, BUTTON_FONT_SIZE * scaleFactor);
        setButtonFontSize(closeButton, BUTTON_FONT_SIZE * scaleFactor);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
