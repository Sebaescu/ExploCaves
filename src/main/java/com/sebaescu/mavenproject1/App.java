package com.sebaescu.mavenproject1;

import java.util.ArrayList;
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
import java.util.List;
import java.util.Random;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

public class App extends Application {

    public static final int CELDA_SIZE = 40;
    private static final int FILAS = 20;
    private static final int COLUMNAS = 20;
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
    private List<Cofre> cofres = new ArrayList<>();
    private boolean[][] cofresAbiertos; // Matriz para rastrear los cofres abiertos
    private List<Enemigo> enemigos = new ArrayList<>();

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
        cofresAbiertos = new boolean[FILAS][COLUMNAS];
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
        
        generarCofres(9,4,FILAS,COLUMNAS);
        agregarCofresAlGrid();
        generarEnemigos(1, FILAS, COLUMNAS);
        agregarEnemigosAlGrid();
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

        if (code == KeyCode.E) {
            abrirCofre();
            return;
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(80), event -> {
            int deltaFila = 0;
            int deltaColumna = 0;

            switch (code) {
                case W:
                    deltaFila = -1;
                    break;
                case S:
                    deltaFila = 1;
                    break;
                case A:
                    deltaColumna = -1;
                    jugadorImageView.setImage(jugadorIzq);
                    break;
                case D:
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
    private void generarCofres(int cantidad, int distanciaMinima, int filas, int columnas) {
        // Lógica para generar cofres en ubicaciones aleatorias de camino
        for (int i = 0; i < cantidad; i++) {
            int intentos = 0;
            while (intentos < 100) { // Limitar la cantidad de intentos para evitar bucles infinitos
                int cofreFila = random.nextInt(filas - 1) + 1;
                int cofreColumna = random.nextInt(columnas - 1) + 1;

                if (laberinto[cofreFila][cofreColumna] == 0 && !hayCofreEnUbicacion(cofreFila, cofreColumna) && cumpleDistanciaMinima(cofreFila, cofreColumna, distanciaMinima)) {
                    cofres.add(new Cofre(cofreFila, cofreColumna, new Image("com/sebaescu/mavenproject1/cofreCerrado.png")));
                    break; // Sale del bucle si el cofre se agregó correctamente
                }
                intentos++;
            }
        }
    }

    private boolean cumpleDistanciaMinima(int fila, int columna, int distanciaMinima) {
        // Verifica si hay al menos la distancia mínima especificada de celdas de camino entre el cofre y cualquier otro cofre existente
        for (Cofre cofreExistente : cofres) {
            int distancia = Math.abs(fila - cofreExistente.getFila()) + Math.abs(columna - cofreExistente.getColumna());
            if (distancia < distanciaMinima) {
                return false;
            }
        }
        return true;
    }



    private boolean hayCofreEnUbicacion(int fila, int columna) {
        for (Cofre cofre : cofres) {
            if (cofre.getFila() == fila && cofre.getColumna() == columna) {
                return true;
            }
        }
        return false;
    }

    private void agregarCofresAlGrid() {
        for (Cofre cofre : cofres) {
            ImageView cofreImageView = new ImageView(cofre.getImagen());
            cofreImageView.setFitWidth(CELDA_SIZE_DIFICIL);
            cofreImageView.setFitHeight(CELDA_SIZE_DIFICIL);
            gridPane.add(cofreImageView, cofre.getColumna(), cofre.getFila());
        }
    }
    private void abrirCofre() {
        int fila = jugadorFila;
        int columna = jugadorColumna;

        // Verificar si el jugador está en una celda con un cofre cerrado y no abierto
        if (esCeldaConCofre(fila, columna) && !cofresAbiertos[fila][columna]) {
            // Marcar el cofre como abierto
            cofresAbiertos[fila][columna] = true;

            // Cambiar todas las imágenes de la celda a cofreAbierto.png
            for (Node node : gridPane.getChildren()) {
                if (node instanceof ImageView) {
                    ImageView imageView = (ImageView) node;
                    int row = GridPane.getRowIndex(imageView);
                    int col = GridPane.getColumnIndex(imageView);

                    // Verificar si la ImageView está en la misma celda que el cofre
                    if (row == fila && col == columna) {
                        imageView.setImage(new Image("com/sebaescu/mavenproject1/cofreAbierto.png"));
                    }
                }
            }

            // Restaurar la imagen del jugador después de abrir el cofre
            jugadorImageView.setImage(jugadorDer);
        }
    }

    private boolean esCeldaConCofre(int fila, int columna) {
        for (Cofre cofre : cofres) {
            if (cofre.getFila() == fila && cofre.getColumna() == columna) {
                return true;
            }
        }
        return false;
    }
    private void generarEnemigos(int maxEnemigosPorTipo, int filas, int columnas) {
        for (int i = 1; i <= 3; i++) { // Generar enemigos para cada tipo (1, 2, 3)
            int cantidadEnemigos = (maxEnemigosPorTipo == 1) ? 1 : 2;

            for (int j = 0; j < cantidadEnemigos; j++) {
                int intentos = 0;

                while (intentos < 100) { // Limitar la cantidad de intentos para evitar bucles infinitos
                    int enemigoFila = random.nextInt(filas - 1) + 1;
                    int enemigoColumna = random.nextInt(columnas - 1) + 1;

                    if (laberinto[enemigoFila][enemigoColumna] == 0 && !hayCofreEnUbicacion(enemigoFila, enemigoColumna)
                            && !hayEnemigoEnUbicacion(enemigoFila, enemigoColumna)) {
                        Image imagenEnemigo = new Image("com/sebaescu/mavenproject1/Enemigo" + i + ".png");
                        int nivelPoder = random.nextInt(4) + 1; // Nivel de poder entre 1 y 4

                        enemigos.add(new Enemigo(enemigoFila, enemigoColumna, nivelPoder, imagenEnemigo));
                        break; // Sale del bucle si el enemigo se agregó correctamente
                    }
                    intentos++;
                }
            }
        }
    }

    private boolean hayEnemigoEnUbicacion(int fila, int columna) {
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getFila() == fila && enemigo.getColumna() == columna) {
                return true;
            }
        }
        return false;
    }

    private void agregarEnemigosAlGrid() {
        for (Enemigo enemigo : enemigos) {
            ImageView enemigoImageView = enemigo.getImageView();
            gridPane.add(enemigoImageView, enemigo.getColumna(), enemigo.getFila());
        }
    }

    public void startJuegoDificil(Stage primaryStage) {
        this.stage = primaryStage;

        generarLaberintoConConexionDificil();

        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: #333333;");
        cofresAbiertos = new boolean[FILAS_DIFICIL][COLUMNAS_DIFICIL];
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
        
        generarCofres(14,7,FILAS_DIFICIL,COLUMNAS_DIFICIL);
        agregarCofresAlGrid();
        generarEnemigos(2, FILAS_DIFICIL, COLUMNAS_DIFICIL);
        agregarEnemigosAlGrid();
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
        Platform.runLater(() -> {
            // Crea una nueva instancia del MainMenu y muestra la ventana maximizada
            MainMenu menuPrincipal = new MainMenu();
            Stage primaryStageMenu = new Stage();
            menuPrincipal.start(primaryStageMenu);
            primaryStageMenu.setMaximized(true);
            stage.close();  // Cierra la ventana actual del juego
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

