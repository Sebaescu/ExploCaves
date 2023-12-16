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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class App extends Application {

    private static final int CELDA_SIZE = 40;
    private static final int FILAS = 15;
    private static final int COLUMNAS = 15;
    private int jugadorFila, jugadorColumna;
    private Stage stage;
    private Timeline timeline;
    private ImageView jugadorImageView, salidaImageView;
    private Image jugadorDer = new Image("com/sebaescu/mavenproject1/JugadorDer.png");
    private Image jugadorIzq = new Image("com/sebaescu/mavenproject1/JugadorIzq.png");
    private GridPane gridPane;
    private int[][] laberinto;
    private Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        MainMenu menuPrincipal = new MainMenu();
        menuPrincipal.start(primaryStage);
    }

    public void startJuego(Stage primaryStage) {
        this.stage = primaryStage;

        // Generar el laberinto antes de iniciar el juego
        generarLaberintoConConexion();

        // Crear el GridPane una vez
        gridPane = new GridPane();

        // Establecer un fondo oscuro
        gridPane.setStyle("-fx-background-color: #333333;"); // Puedes ajustar el color según tus preferencias

        // Crear y agregar las celdas del laberinto
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                ImageView imageView;
                switch (laberinto[i][j]) {
                    case 1:
                        imageView = new ImageView(new Image("com/sebaescu/mavenproject1/obstaculo.png"));
                        break;
                    case 2:
                        salidaImageView = new ImageView(new Image("com/sebaescu/mavenproject1/puertaCerrada.png"));
                        imageView = salidaImageView;
                        break;
                    default:
                        imageView = new ImageView(new Image("com/sebaescu/mavenproject1/camino.png"));
                        break;
                }
                imageView.setFitWidth(CELDA_SIZE);
                imageView.setFitHeight(CELDA_SIZE);
                gridPane.add(imageView, j, i);
            }
        }

        // Encontrar una celda de camino para que el jugador comience
        encontrarCeldaInicio();

        // Agregar la imagen del jugador
        jugadorImageView = new ImageView(jugadorDer);
        jugadorImageView.setFitWidth(CELDA_SIZE);
        jugadorImageView.setFitHeight(CELDA_SIZE);
        gridPane.add(jugadorImageView, jugadorColumna, jugadorFila);

        Scene scene = new Scene(gridPane, COLUMNAS * CELDA_SIZE, FILAS * CELDA_SIZE);
        scene.setOnKeyPressed(event -> manejarTeclaPresionada(event.getCode()));
        scene.setOnKeyReleased(event -> detenerMovimiento());

        primaryStage.setTitle("ExploCaves");
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
                salidaImageView.setImage(new Image("com/sebaescu/mavenproject1/salida.png"));
                mostrarMensaje("¡Has ganado!");
            }
        }
    }

    private boolean esMovimientoValido(int fila, int columna) {
        return fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS && laberinto[fila][columna] != 1;
    }

    private void generarLaberintoConConexion() {
        // Inicializar el laberinto con todos los valores a 1 (paredes)
        laberinto = new int[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                laberinto[i][j] = 1;
            }
        }

        // Generar el laberinto con el algoritmo Recursive Backtracking
        recursiveBacktracking(1, 1);

        // Colocar la salida en una posición aleatoria
        laberinto[random.nextInt(FILAS)][random.nextInt(COLUMNAS)] = 2;
    }

    private void recursiveBacktracking(int x, int y) {
        // Marcamos la posición actual como camino
        laberinto[y][x] = 0;

        // Direcciones posibles
        int[][] direcciones = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        Collections.shuffle(Arrays.asList(direcciones));

        for (int[] dir : direcciones) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx > 0 && nx < COLUMNAS - 1 && ny > 0 && ny < FILAS - 1 && laberinto[ny][nx] == 1) {
                laberinto[ny - dir[1] / 2][nx - dir[0] / 2] = 0;
                recursiveBacktracking(nx, ny);
            }
        }
    }

    private void encontrarCeldaInicio() {
        // Buscar una celda aleatoria dentro del laberinto (evitando las filas y columnas de los bordes)
        do {
            jugadorFila = random.nextInt(FILAS - 1) + 1;
            jugadorColumna = random.nextInt(COLUMNAS - 1) + 1;
        } while (laberinto[jugadorFila][jugadorColumna] != 0);
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

