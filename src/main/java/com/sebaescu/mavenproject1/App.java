package com.sebaescu.mavenproject1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

public class App extends Application {

    private static final int CELDA_SIZE = 40;
    private static final int FILAS = 15;
    private static final int COLUMNAS = 15;
    private static final int FILAS_DIFICIL = FILAS * 2;
    private static final int COLUMNAS_DIFICIL = COLUMNAS * 2;
    private static final int CELDA_SIZE_DIFICIL = CELDA_SIZE / 2;
    private int jugadorFila, jugadorColumna;
    private Stage stage; // Referencia al escenario principal
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
        gridPane.setStyle("-fx-background-color: #333333;");

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
        scene.widthProperty().addListener((observable, oldValue, newValue) -> escalarImagenes(COLUMNAS,FILAS));
        scene.heightProperty().addListener((observable, oldValue, newValue) -> escalarImagenes(COLUMNAS,FILAS));
        scene.setOnKeyPressed(event -> manejarTeclaPresionada(event.getCode(),FILAS,COLUMNAS));
        scene.setOnKeyReleased(event -> detenerMovimiento());

        // Restaurar el tamaño de la ventana al maximizado al volver al menú
        stage.setMaximized(true);

        primaryStage.setTitle("ExploCaves");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void manejarTeclaPresionada(KeyCode code, int filas, int columnas) {
        detenerMovimiento();

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

            mover(deltaFila, deltaColumna, filas, columnas);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void detenerMovimiento() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void mover(int deltaFila, int deltaColumna, int filas, int columnas) {
        int nuevaFila = jugadorFila + deltaFila;
        int nuevaColumna = jugadorColumna + deltaColumna;

        if (esMovimientoValido(nuevaFila, nuevaColumna, filas, columnas)) {
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

    private boolean esMovimientoValido(int fila, int columna, int filas, int columnas) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas && laberinto[fila][columna] != 1;
    }

    private void generarLaberintoConConexion() {
        laberinto = new int[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                laberinto[i][j] = 1;
            }
        }

        recursiveBacktracking(1, 1);
        laberinto[random.nextInt(FILAS - 2) + 1][random.nextInt(COLUMNAS - 2) + 1] = 2;
    }

    private void recursiveBacktracking(int x, int y) {
        laberinto[y][x] = 0;
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
        do {
            jugadorFila = random.nextInt(FILAS - 1) + 1;
            jugadorColumna = random.nextInt(COLUMNAS - 1) + 1;
        } while (laberinto[jugadorFila][jugadorColumna] != 0);
    }

    public void startJuegoDificil(Stage primaryStage) {
        this.stage = primaryStage;

        generarLaberintoConConexionDificil();

        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: #333333;");

        for (int i = 0; i < FILAS_DIFICIL; i++) {
            for (int j = 0; j < COLUMNAS_DIFICIL; j++) {
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
                imageView.setFitWidth(CELDA_SIZE_DIFICIL);
                imageView.setFitHeight(CELDA_SIZE_DIFICIL);
                gridPane.add(imageView, j, i);
            }
        }

        encontrarCeldaInicio();

        jugadorImageView = new ImageView(jugadorDer);
        jugadorImageView.setFitWidth(CELDA_SIZE_DIFICIL);
        jugadorImageView.setFitHeight(CELDA_SIZE_DIFICIL);
        gridPane.add(jugadorImageView, jugadorColumna, jugadorFila);

        Scene scene = new Scene(gridPane, COLUMNAS_DIFICIL * CELDA_SIZE_DIFICIL, FILAS_DIFICIL * CELDA_SIZE_DIFICIL);
        scene.widthProperty().addListener((observable, oldValue, newValue) -> escalarImagenes(COLUMNAS_DIFICIL,FILAS_DIFICIL));
        scene.heightProperty().addListener((observable, oldValue, newValue) -> escalarImagenes(COLUMNAS_DIFICIL,FILAS_DIFICIL));
        scene.setOnKeyPressed(event -> manejarTeclaPresionada(event.getCode(),FILAS_DIFICIL,COLUMNAS_DIFICIL));
        scene.setOnKeyReleased(event -> detenerMovimiento());

        // Restaurar el tamaño de la ventana al maximizado al volver al menú
        stage.setMaximized(true);

        primaryStage.setTitle("ExploCaves - Difícil");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generarLaberintoConConexionDificil() {
        laberinto = new int[FILAS_DIFICIL][COLUMNAS_DIFICIL];
        for (int i = 0; i < FILAS_DIFICIL; i++) {
            for (int j = 0; j < COLUMNAS_DIFICIL; j++) {
                laberinto[i][j] = 1;
            }
        }

        recursiveBacktrackingDificil(1, 1);
        laberinto[random.nextInt(FILAS_DIFICIL - 2) + 1][random.nextInt(COLUMNAS_DIFICIL - 2) + 1] = 2;
    }

    private void recursiveBacktrackingDificil(int x, int y) {
        laberinto[y][x] = 0;
        int[][] direcciones = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        Collections.shuffle(Arrays.asList(direcciones));

        for (int[] dir : direcciones) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx > 0 && nx < COLUMNAS_DIFICIL - 1 && ny > 0 && ny < FILAS_DIFICIL - 1 && laberinto[ny][nx] == 1) {
                laberinto[ny - dir[1] / 2][nx - dir[0] / 2] = 0;
                recursiveBacktrackingDificil(nx, ny);
            }
        }
    }
    private void escalarImagenes(int columnas, int filas) {
        double nuevoAncho = stage.getScene().getWidth() / columnas;
        double nuevoAlto = stage.getScene().getHeight() / filas;

        // Escala las imágenes de las celdas del laberinto
        for (Node node : gridPane.getChildren()) {
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;
                imageView.setFitWidth(nuevoAncho);
                imageView.setFitHeight(nuevoAlto);
            }
        }

        // Escala la imagen del jugador
        jugadorImageView.setFitWidth(nuevoAncho);
        jugadorImageView.setFitHeight(nuevoAlto);

        // Escala la imagen de la salida
        salidaImageView.setFitWidth(nuevoAncho);
        salidaImageView.setFitHeight(nuevoAlto);
    }
    private void mostrarMensaje(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

