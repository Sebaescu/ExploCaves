package com.sebaescu.mavenproject1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private int jugadorFila = 1;
    private int jugadorColumna = 1;
    private Stage stage;
    private Timeline timeline;
    private ImageView jugadorImageView;
    private Image jugadorDer = new Image("com/sebaescu/mavenproject1/JugadorDer.png");
    private Image jugadorIzq = new Image("com/sebaescu/mavenproject1/JugadorIzq.png");
    private GridPane gridPane;

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

    @Override
    public void start(Stage primaryStage) {
        MainMenu menuPrincipal = new MainMenu();
        menuPrincipal.start(primaryStage);
    }

    public void startJuego(Stage primaryStage) {
        this.stage = primaryStage;

        // Crear el GridPane una vez
        gridPane = new GridPane();

        // Agregar la imagen de fondo como fondo del laberinto
        ImageView fondoImageView = new ImageView(new Image("com/sebaescu/mavenproject1/fondo.png"));
        fondoImageView.setFitWidth(COLUMNAS * CELDA_SIZE);
        fondoImageView.setFitHeight(FILAS * CELDA_SIZE);
        gridPane.add(fondoImageView, 0, 0, COLUMNAS, FILAS);

        // Crear y agregar las celdas del laberinto
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (laberinto[i][j] == 1) {
                    ImageView obstaculoImageView = new ImageView(new Image("com/sebaescu/mavenproject1/obstaculo.png"));
                    obstaculoImageView.setFitWidth(CELDA_SIZE);
                    obstaculoImageView.setFitHeight(CELDA_SIZE);
                    gridPane.add(obstaculoImageView, j, i);
                } else if (laberinto[i][j] == 2) {
                    // Agregar la imagen de salida
                    ImageView salidaImageView = new ImageView(new Image("com/sebaescu/mavenproject1/salida.png"));
                    salidaImageView.setFitWidth(CELDA_SIZE);
                    salidaImageView.setFitHeight(CELDA_SIZE);
                    gridPane.add(salidaImageView, j, i);
                } else {
                    ImageView caminoImageView = new ImageView(new Image("com/sebaescu/mavenproject1/camino.png"));
                    caminoImageView.setFitWidth(CELDA_SIZE);
                    caminoImageView.setFitHeight(CELDA_SIZE);
                    gridPane.add(caminoImageView, j, i);
                }
            }
        }

        // Agregar la imagen del jugador
        jugadorImageView = new ImageView(new Image("com/sebaescu/mavenproject1/JugadorDer.png"));
        jugadorImageView.setFitWidth(CELDA_SIZE);
        jugadorImageView.setFitHeight(CELDA_SIZE);
        gridPane.add(jugadorImageView, jugadorColumna, jugadorFila);

        Scene scene = new Scene(gridPane, COLUMNAS * CELDA_SIZE, FILAS * CELDA_SIZE);
        scene.setOnKeyPressed(event -> manejarTeclaPresionada(event.getCode()));
        scene.setOnKeyReleased(event -> detenerMovimiento());

        primaryStage.setTitle("Laberinto Game");
        primaryStage.setScene(scene);
        primaryStage.show();
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
                    jugadorImageView.setImage(jugadorIzq);
                    break;
                case RIGHT:
                    deltaColumna = 1;
                    jugadorImageView.setImage(jugadorDer);
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
            // Actualizar la posición del jugador
            gridPane.getChildren().remove(jugadorImageView);
            gridPane.add(jugadorImageView, nuevaColumna, nuevaFila);

            jugadorFila = nuevaFila;
            jugadorColumna = nuevaColumna;

            if (laberinto[jugadorFila][jugadorColumna] == 2) {
                mostrarMensaje("¡Has ganado!");
            }
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
