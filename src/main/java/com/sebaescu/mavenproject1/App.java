package com.sebaescu.mavenproject1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private static final int CELDA_SIZE = 40;
    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;

    private int[][] laberinto = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 1, 1, 1},
            {1, 0, 1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 2} // 2 representa la salida
    };

    private int jugadorFila = 1;
    private int jugadorColumna = 0;
    private Stage stage;
    private Timeline timeline;

    @Override
    public void start(Stage primaryStage) {
        MainMenu menuPrincipal = new MainMenu();
        menuPrincipal.start(primaryStage);
    }
    
    public void startJuego(Stage primaryStage) {
        this.stage = primaryStage; // Asigna el Stage a la variable de instancia

        GridPane gridPane = crearLaberinto();

        Scene scene = new Scene(gridPane, COLUMNAS * CELDA_SIZE, FILAS * CELDA_SIZE);
        scene.setOnKeyPressed(event -> manejarTeclaPresionada(event.getCode()));
        scene.setOnKeyReleased(event -> detenerMovimiento());

        primaryStage.setTitle("Laberinto Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane crearLaberinto() {
        GridPane gridPane = new GridPane();

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                Rectangle celda = new Rectangle(CELDA_SIZE, CELDA_SIZE);

                if (laberinto[i][j] == 1) {
                    celda.setFill(Color.BLACK);
                } else if (laberinto[i][j] == 2) {
                    celda.setFill(Color.GREEN);
                } else {
                    celda.setFill(Color.WHITE);
                }

                gridPane.add(celda, j, i);
            }
        }

        return gridPane;
    }

    private void manejarTeclaPresionada(KeyCode code) {
        detenerMovimiento(); // Detener el movimiento actual antes de comenzar uno nuevo

        timeline = new Timeline(new KeyFrame(Duration.millis(80), event -> {
            int deltaFila = 0;
            int deltaColumna = 0;

            switch (code) {
                case UP:
                    deltaFila = -1;
                    break;
                case DOWN:
                    deltaFila = 1;
                    break;
                case LEFT:
                    deltaColumna = -1;
                    break;
                case RIGHT:
                    deltaColumna = 1;
                    break;
            }

            mover(deltaFila, deltaColumna);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void detenerMovimiento() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void mover(int deltaFila, int deltaColumna) {
        int nuevaFila = jugadorFila + deltaFila;
        int nuevaColumna = jugadorColumna + deltaColumna;

        if (esMovimientoValido(nuevaFila, nuevaColumna)) {
            GridPane gridPane = crearLaberinto();
            Rectangle jugador = new Rectangle(CELDA_SIZE, CELDA_SIZE, Color.BLUE);
            gridPane.add(jugador, nuevaColumna, nuevaFila);

            jugadorFila = nuevaFila;
            jugadorColumna = nuevaColumna;

            if (laberinto[jugadorFila][jugadorColumna] == 2) {
                mostrarMensaje("¡Has ganado!");
            }

            Scene scene = new Scene(gridPane, COLUMNAS * CELDA_SIZE, FILAS * CELDA_SIZE);
            scene.setOnKeyPressed(event -> manejarTeclaPresionada(event.getCode()));
            scene.setOnKeyReleased(event -> detenerMovimiento());

            stage.setScene(scene);
        }
    }

    private boolean esMovimientoValido(int fila, int columna) {
        return fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS && laberinto[fila][columna] != 1;
    }

    private void mostrarMensaje(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("¡Ganaste!");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.setOnHidden(event -> volverAlMenu());
            alert.show();
            detenerMovimiento();
        });
    }

    private void volverAlMenu() {
        MainMenu menuPrincipal = new MainMenu();
        menuPrincipal.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

