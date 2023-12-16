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
  private static final int FILAS = 15; // Aumentar el número de filas
  private static final int COLUMNAS = 15; // Aumentar el número de columnas
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
    generarLaberinto();

    // Crear el GridPane una vez
    gridPane = new GridPane();
    
    // Establecer un fondo oscuro
    gridPane.setStyle("-fx-background-color: #333333;"); // Puedes ajustar el color según tus preferencias

    conectarJugadorConSalida();

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
        salidaImageView.setImage(new Image("com/sebaescu/mavenproject1/salida.png"));
        mostrarMensaje("¡Has ganado!");
      }
    }
  }

  private boolean esMovimientoValido(int fila, int columna) {
    return fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS && laberinto[fila][columna] != 1;
  }

  private void generarLaberinto() {
    // Inicializar el laberinto con todos los valores a 0 (caminos)
    laberinto = new int[FILAS][COLUMNAS];

    // Generar el laberinto recursivamente
    generarLaberintoRecursivo(1, 1);
  }

  private boolean salidaGenerada = false;

  private void generarLaberintoRecursivo(int fila, int columna) {
        laberinto[fila][columna] = 1; // Marcar la celda actual como parte del laberinto

        // Definir los movimientos posibles en orden aleatorio
        List<int[]> movimientosPosibles = Arrays.asList(
            new int[]{-2, 0}, // Arriba
            new int[]{2, 0}, // Abajo
            new int[]{0, -2}, // Izquierda
            new int[]{0, 2}  // Derecha
        );

        // Barajar los movimientos para obtener un orden aleatorio
        Collections.shuffle(movimientosPosibles);

        for (int[] movimiento : movimientosPosibles) {
            int nuevaFila = fila + movimiento[0];
            int nuevaColumna = columna + movimiento[1];

            // Verificar que la nueva posición esté dentro de los límites
            if (nuevaFila >= 1 && nuevaFila < FILAS - 1 && nuevaColumna >= 1 && nuevaColumna < COLUMNAS - 1) {
                // Verificar que la nueva posición no haya sido visitada
                if (laberinto[nuevaFila][nuevaColumna] == 0) {
                    // Marcar la celda intermedia como parte del laberinto
                    laberinto[fila + movimiento[0] / 2][columna + movimiento[1] / 2] = 1;

                    // Llamada recursiva para continuar construyendo el laberinto
                    generarLaberintoRecursivo(nuevaFila, nuevaColumna);
                }
            }
        }

        // Generar la celda de salida solo si no se ha generado antes
        if (!salidaGenerada) {
            List<int[]> posiblesSalidas = encontrarPosiblesSalidas();
            if (!posiblesSalidas.isEmpty()) {
                // Elegir aleatoriamente una posición para la salida
                int[] posicionSalida = posiblesSalidas.get(random.nextInt(posiblesSalidas.size()));
                laberinto[posicionSalida[0]][posicionSalida[1]] = 2;
                salidaGenerada = true;

                // Conectar la salida al jugador mediante un camino
                conectarJugadorConSalida();
            }
        }
    }

  private void encontrarCeldaInicio() {
    // Buscar una celda aleatoria dentro del laberinto (evitando las filas y columnas de los bordes)
    do {
      jugadorFila = random.nextInt(FILAS - 2) + 1; // Excluir la primera y última fila
      jugadorColumna = random.nextInt(COLUMNAS - 2) + 1; // Excluir la primera y última columna
    } while (laberinto[jugadorFila][jugadorColumna] != 0);
  }


  private List<int[]> encontrarPosiblesSalidas() {
    List<int[]> posiblesSalidas = new ArrayList<>();

    for (int i = 1; i < FILAS - 1; i += 2) {
      for (int j = 1; j < COLUMNAS - 1; j += 2) {
        if (laberinto[i][j] == 0 && !esCeldaVecinaJugador(i, j)) {
          posiblesSalidas.add(new int[]{i, j});
        }
      }
    }

    return posiblesSalidas;
  }

  private boolean esCeldaVecinaJugador(int fila, int columna) {
    return Math.abs(fila - jugadorFila) == 1 && Math.abs(columna - jugadorColumna) == 1;
  }

  private void conectarJugadorConSalida() {
    Stack<int[]> pila = new Stack<>();
    boolean[][] visitado = new boolean[FILAS][COLUMNAS];

    // Iniciar la búsqueda desde el jugador
    pila.push(new int[]{jugadorFila, jugadorColumna});

    while (!pila.isEmpty()) {
      int[] actual = pila.pop();
      int fila = actual[0];
      int columna = actual[1];

      if (fila > 0 && fila < FILAS - 1 && columna > 0 && columna < COLUMNAS - 1) {
        // Verificar si ya hemos llegado a la salida
        if (laberinto[fila][columna] == 2) {
          return; // Ya hay un camino hasta la salida
        }
      }

      // Agregar movimientos posibles a la pila
      for (int[] movimiento : MOVIMIENTOS) {
        int nuevaFila = fila + movimiento[0];
        int nuevaColumna = columna + movimiento[1];

        // Verificar que la nueva posición esté dentro de los límites y no haya sido visitada
        if (nuevaFila >= 0 && nuevaFila < FILAS && nuevaColumna >= 0 && nuevaColumna < COLUMNAS
            && !visitado[nuevaFila][nuevaColumna] && laberinto[nuevaFila][nuevaColumna] != 1) {
          pila.push(new int[]{nuevaFila, nuevaColumna});
          visitado[nuevaFila][nuevaColumna] = true;
        }
      }
    }

    // Si no se ha llegado a la salida, volver a conectar el jugador con una nueva salida
    salidaGenerada = false;
    laberinto[jugadorFila][jugadorColumna] = 0;
    generarLaberintoRecursivo(jugadorFila, jugadorColumna);
  }

  private static final int[][] MOVIMIENTOS = {
      {-1, 0}, // Arriba
      {1, 0}, // Abajo
      {0, -1}, // Izquierda
      {0, 1}  // Derecha
  };
   
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