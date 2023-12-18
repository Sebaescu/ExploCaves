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
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

public class App extends Application {

    public static final int CELDA_SIZE = 40;
    private static final int FILAS = 20;
    private static final int COLUMNAS = 20;
    private static final int FILAS_DIFICIL = ((int)Math.ceil(FILAS * 1.5));
    private static final int COLUMNAS_DIFICIL = ((int)Math.ceil(COLUMNAS * 1.5));
    private static final int CELDA_SIZE_DIFICIL = (int) ((int) CELDA_SIZE / 1.5);
    private Stage stage; // Referencia al escenario principal
    private Timeline timeline;
    private ImageView salidaImageView;
    private Jugador jugador;
    private Image jugadorDer = new Image("com/sebaescu/mavenproject1/JugadorDer.png");
    private Image jugadorIzq = new Image("com/sebaescu/mavenproject1/JugadorIzq.png");
    private GridPane gridPane;
    private int cantidadCofresBuenos,cantidadCofresTrampa,cantidadCofresEspecial=0;
    private int salidaFila,salidaColumna;
    private int[][] laberinto;
    private Random random = new Random();
    private List<Cofre> cofres = new ArrayList<>();
    private boolean[][] cofresAbiertos; // Matriz para rastrear los cofres abiertos
    private List<Enemigo> enemigos = new ArrayList<>();
    private boolean generadoCofreEspecial = false,salidaEstaAbierta = false,modoDificil = false;

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
                        // Almacena las coordenadas de la celda de salida
                        salidaFila = i;
                        salidaColumna = j;
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

        jugador.getImageView().setImage(jugadorDer);  
        gridPane.add(jugador.getImageView(), jugador.getColumna(), jugador.getFila());
        agregarEtiquetaNivel(jugador);
        generarCofres(9,4,FILAS,COLUMNAS);
        agregarCofresAlGrid();
        generarEnemigos(4, FILAS, COLUMNAS,4);
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
        if (code == KeyCode.F) {
            // Realizar la acción de combate al presionar la tecla F
            if(modoDificil){
                confrontarEnemigo(FILAS_DIFICIL,COLUMNAS_DIFICIL);
                return;
            }else{
                confrontarEnemigo(FILAS,COLUMNAS);
                return;
            }
        }
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
                    jugador.getImageView().setImage(jugadorIzq);
                    break;
                case RIGHT:
                    deltaColumna = 1;
                    jugador.getImageView().setImage(jugadorDer);
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
        int nuevaFila = jugador.getFila() + deltaFila;
        int nuevaColumna = jugador.getColumna() + deltaColumna;

        if (esMovimientoValido(nuevaFila, nuevaColumna, filas, columnas)) {
            gridPane.getChildren().remove(jugador.getImageView());
            gridPane.add(jugador.getImageView(), nuevaColumna, nuevaFila);

            jugador.setFila(nuevaFila);
            jugador.setColumna(nuevaColumna);

            actualizarPosicionEtiquetaNivel(jugador);
            if (laberinto[jugador.getFila()][jugador.getColumna()] == 2 && salidaEstaAbierta) {
                //aasalidaImageView.setImage(new Image("com/sebaescu/mavenproject1/puertaAbierta.png"));
                mostrarMensaje("¡Has ganado!","Lograste Escapar");
            }
        }
    }




    private boolean esMovimientoValido(int fila, int columna, int filas, int columnas) {
        // Verifica que la celda esté dentro de los límites del laberinto
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            return false;
        }

        // Verifica si la celda es un obstáculo o tiene un enemigo no derrotado
        if (laberinto[fila][columna] == 1) {
            return false;
        }

        for (Enemigo enemigo : enemigos) {
            if (enemigo.getFila() == fila && enemigo.getColumna() == columna && !enemigo.isDerrotado()) {
                return false;
            }
        }

        return true;
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
            int jugadorFila = random.nextInt(FILAS - 1) + 1;
            int jugadorColumna = random.nextInt(COLUMNAS - 1) + 1;
            if (laberinto[jugadorFila][jugadorColumna] == 0) {
                jugador = new Jugador(jugadorFila, jugadorColumna, 1, jugadorDer);
                break;
            }
        } while (true);
    }
    
    private void generarCofres(int cantidad, int distanciaMinima, int filas, int columnas) {
        // Lógica para generar cofres en ubicaciones aleatorias de camino
        int cofresBuenosAGenerar = (int) Math.ceil((cantidad - 1) * 0.6);
        int cofresTrampaAGenerar = cantidad - 1 - cofresBuenosAGenerar;
        String tipoCofre;
        Image imagenCofre = new Image("com/sebaescu/mavenproject1/cofreCerrado.png");    
        boolean cofrePuertaGenerado = false;
        for (int i = 0; i < cantidad; i++) {
            int intentos = 0;
            while (intentos < 100) { // Limitar la cantidad de intentos para evitar bucles infinitos
                int cofreFila = random.nextInt(filas - 1) + 1;
                int cofreColumna = random.nextInt(columnas - 1) + 1;

                if (laberinto[cofreFila][cofreColumna] == 0 && !hayCofreEnUbicacion(cofreFila, cofreColumna) && cumpleDistanciaMinima(cofreFila, cofreColumna, distanciaMinima)) {
                    if(cofrePuertaGenerado == false){
                        Cofre cofreEspecial = new Cofre(cofreFila, cofreColumna, imagenCofre, "cofreEspecial", true);
                        cofres.add(cofreEspecial);
                        cofrePuertaGenerado = true;
                    }
                    if (i < cofresBuenosAGenerar) {
                        tipoCofre = "cofreBueno";
                    } else {
                        tipoCofre = "cofreTrampa";
                    }
                    Cofre cofre = new Cofre(cofreFila, cofreColumna, imagenCofre, tipoCofre, false);
                    cofres.add(cofre);
                    break; // Sale del bucle si el cofre se agregó correctamente
                }
                intentos++;
            }
        }
    }
    private void generarCofre(int distanciaMinima, int filas, int columnas, String tipoCofre, boolean esCambiaImagen) {
        int intentos = 0;
        int cantidadMaximaCofres = cantidadCofresBuenos + cantidadCofresTrampa + cantidadCofresEspecial;

        while (intentos < 100 && cofres.size() < cantidadMaximaCofres) {
            int cofreFila = random.nextInt(filas - 1) + 1;
            int cofreColumna = random.nextInt(columnas - 1) + 1;

            // Verificar si la distancia mínima a otros cofres se cumple
            if (cumpleDistanciaMinima(cofreFila, cofreColumna, distanciaMinima) &&
                laberinto[cofreFila][cofreColumna] == 0 &&
                !hayCofreEnUbicacion(cofreFila, cofreColumna)) {
                Image imagenCofre = new Image("com/sebaescu/mavenproject1/cofreCerrado.png");
                Cofre nuevoCofre = new Cofre(cofreFila, cofreColumna, imagenCofre, tipoCofre, esCambiaImagen && tipoCofre.equals("cofreEspecial"));
                cofres.add(nuevoCofre);

                if (nuevoCofre.getTipoCofre().equals("cofreEspecial")) {
                    cantidadCofresEspecial++;
                } else if (nuevoCofre.getTipoCofre().equals("cofreBueno")) {
                    cantidadCofresBuenos--;
                } else if (nuevoCofre.getTipoCofre().equals("cofreTrampa")) {
                    cantidadCofresTrampa--;
                }

                if (cofres.size() == cantidadMaximaCofres) {
                    break;
                }
            }

            intentos++;
        }
    }


    private boolean cumpleDistanciaMinima(int fila, int columna, int distanciaMinima) {
        for (Cofre cofre : cofres) {
            int distancia = Math.abs(cofre.getFila() - fila) + Math.abs(cofre.getColumna() - columna);
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
            System.out.println(cofre.getTipoCofre());
        }
    }
    private void abrirCofre() {
        int fila = jugador.getFila();
        int columna = jugador.getColumna();

        if (esCeldaConCofre(fila, columna) && !cofresAbiertos[fila][columna]) {
            cofresAbiertos[fila][columna] = true;
            // Cambiar la imagen del cofre por cofreAbierto.png
            for (Node node : gridPane.getChildren()) {
                if (node instanceof ImageView) {
                    ImageView imageView = (ImageView) node;
                    int row = GridPane.getRowIndex(imageView);
                    int col = GridPane.getColumnIndex(imageView);
                     if (row == fila && col == columna) {
                        imageView.setImage(new Image("com/sebaescu/mavenproject1/cofreAbierto.png"));
                     }
                }
            }
            for (Cofre cofre : cofres) {
                
                if (cofre.getFila() == fila && cofre.getColumna() == columna) {
                    String tipoCofre = cofre.getTipoCofre();

                    switch (tipoCofre) {
                        case "cofreBueno":
                            jugador.aumentarNivel();
                            System.out.println("¡Has encontrado un cofre bueno! Tu nivel ha aumentado.");
                            break;
                        case "cofreTrampa":
                            aplicarLogicaCofreTrampa();
                            break;
                        case "cofreEspecial":
                            aplicarLogicaCofreEspecial();
                            break;
                    }
                    break; // No necesitas seguir buscando
                }
            }
            jugador.getImageView().setImage(jugadorDer);
        }
    }

    private void aplicarLogicaCofreTrampa() {
        Random random = new Random();
        double probabilidad = random.nextDouble();

        if (probabilidad < 0.6) {
            jugador.disminuirNivel();
            System.out.println("¡Es un cofre trampa! Has perdido un nivel.");
            if (jugador.getNivel() <= 0) {
                mostrarMensaje("¡Perdiste!", "¡Tu nivel ha llegado a 0! Has perdido la partida.");
            }
        } else if (probabilidad < 0.95) {
            int nivelEnemigo = (probabilidad < 0.8) ? 3 : 4;
            int nivelPoder = random.nextInt(2) + 3;
            generarEnemigo(jugador, nivelPoder);
            System.out.println("¡Es un cofre trampa! Ha aparecido un enemigo de nivel " + nivelEnemigo + ".");
        } else {
            mostrarMensaje("¡Perdiste!", "¡Es un cofre trampa! Pierdes la partida.");
        }
    }

    private void aplicarLogicaCofreEspecial() {
        if (!generadoCofreEspecial) {
            cambiarImagenCeldaSalida();
            generadoCofreEspecial = true;
            salidaEstaAbierta = true;
            System.out.println("¡Has encontrado un cofre especial! La celda de salida ahora es una puerta abierta.");
        } else {
            System.out.println("¡Has encontrado un cofre especial! Pero ya has abierto uno antes.");
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
    private void cambiarImagenCeldaSalida() {
        // Obtener el ImageView de la celda de salida
        ImageView celdaSalida = encontrarImageViewPorCoordenadas(salidaFila, salidaColumna);

        // Verificar si se encontró el ImageView y si el jugador ha abierto un cofre que cambia la imagen
        if (celdaSalida != null ) {
            // Cambiar la imagen de la celda de salida
            celdaSalida.setImage(new Image("com/sebaescu/mavenproject1/puertaAbierta.png"));
        } 
        
        
    }

    // Método auxiliar para encontrar un ImageView por coordenadas
    private ImageView encontrarImageViewPorCoordenadas(int fila, int columna) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;
                int row = GridPane.getRowIndex(imageView);
                int col = GridPane.getColumnIndex(imageView);

                if (row == fila && col == columna) {
                    return imageView;
                }
            }
        }
        return null; // No se encontró ImageView para las coordenadas dadas
    }

    private void generarEnemigos(int cantidadEnemigosAGenerar, int filas, int columnas,int maxNivelPoder) {
        for (int i = 0; i < cantidadEnemigosAGenerar; i++) {
            int intentos = 0;

            while (intentos < 100) { // Limitar la cantidad de intentos para evitar bucles infinitos
                int enemigoFila = random.nextInt(filas - 1) + 1;
                int enemigoColumna = random.nextInt(columnas - 1) + 1;

                if (laberinto[enemigoFila][enemigoColumna] == 0 && !hayCofreEnUbicacion(enemigoFila, enemigoColumna)
                        && !hayEnemigoEnUbicacion(enemigoFila, enemigoColumna)) {
                    int nivelPoder = random.nextInt(maxNivelPoder) + 1; // Nivel de poder entre 1 y 5
                    String tipoEnemigo = Enemigo.tiposDeEnemigos.get(random.nextInt(3)); // Obtén un tipo de enemigo aleatorio

                    enemigos.add(new Enemigo(enemigoFila, enemigoColumna, nivelPoder, tipoEnemigo));
                    break; // Sale del bucle si el enemigo se agregó correctamente
                }
                intentos++;
            }
        }
    }

    private int[] celdaCaminoMasCercana(Jugador jugador) {
        int jugadorFila = jugador.getFila();
        int jugadorColumna = jugador.getColumna();

        // Define las direcciones posibles (arriba, abajo, izquierda, derecha)
        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // Mezcla aleatoriamente las direcciones para mayor variedad
        Collections.shuffle(Arrays.asList(direcciones));

        // Busca la celda más cercana que no sea un obstáculo
        for (int[] dir : direcciones) {
            int nuevaFila = jugadorFila + dir[0];
            int nuevaColumna = jugadorColumna + dir[1];

            // Verifica si la nueva celda está dentro de los límites del laberinto
            if(modoDificil){
                if (nuevaFila >= 0 && nuevaFila < FILAS_DIFICIL && nuevaColumna >= 0 && nuevaColumna < COLUMNAS_DIFICIL) {
                    // Verifica si la nueva celda no es un obstáculo
                    if (laberinto[nuevaFila][nuevaColumna] == 0) {
                        return new int[]{nuevaFila, nuevaColumna};
                    }
                }
            }else{
                if (nuevaFila >= 0 && nuevaFila < FILAS && nuevaColumna >= 0 && nuevaColumna < COLUMNAS) {
                    // Verifica si la nueva celda no es un obstáculo
                    if (laberinto[nuevaFila][nuevaColumna] == 0) {
                        return new int[]{nuevaFila, nuevaColumna};
                    }
                }
            }          
        }
        // Si no se encuentra ninguna celda válida, devuelve la celda actual del jugador
        return new int[]{jugadorFila, jugadorColumna};
    }

    private void generarEnemigo(Jugador jugador, int nivelPoder) {
        int[] celdaEnemigo = celdaCaminoMasCercana(jugador);
        int enemigoFila = celdaEnemigo[0];
        int enemigoColumna = celdaEnemigo[1];

        if (!hayCofreEnUbicacion(enemigoFila, enemigoColumna) && !hayEnemigoEnUbicacion(enemigoFila, enemigoColumna)) {
            // Obtén el tipo de enemigo según algún criterio (puedes personalizar esta lógica)
            int tipoEnemigoIndex = random.nextInt(3); // Se elige un tipo de enemigo aleatorio
            String tipoEnemigo = Enemigo.tiposDeEnemigos.get(tipoEnemigoIndex);

            // Puedes agregar el enemigo a tu lista de enemigos aquí si es necesario
            Enemigo nuevoEnemigo = new Enemigo(enemigoFila, enemigoColumna, nivelPoder, tipoEnemigo);
            enemigos.add(nuevoEnemigo);
            if(modoDificil){
                nuevoEnemigo.getImageView().setFitHeight(CELDA_SIZE_DIFICIL);
                nuevoEnemigo.getImageView().setFitWidth(CELDA_SIZE_DIFICIL);
            }else{
                nuevoEnemigo.getImageView().setFitHeight(CELDA_SIZE);
                nuevoEnemigo.getImageView().setFitWidth(CELDA_SIZE);
            }  
            gridPane.add(nuevoEnemigo.getImageView(), enemigoColumna, enemigoFila);
            // Llama al método existente para agregar la etiqueta del nivel del enemigo
            agregarEtiquetaNivelEnemigo(nuevoEnemigo);
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
            agregarEtiquetaNivelEnemigo(enemigo);
        }
    }

    private void agregarEtiquetaNivelEnemigo(Enemigo enemigo) {
        Label labelNivel = enemigo.getLabelNivel();

        Integer fila = enemigo.getFila();
        Integer columna = enemigo.getColumna();

        if (fila != null && columna != null) {
            gridPane.add(labelNivel, columna, fila);

            // Ajusta la posición vertical
            double yOffset = enemigo.getImageView().getBoundsInParent().getMinY() - 22;
            labelNivel.setTranslateY(yOffset);
        } else {
            System.out.println("La posición del enemigo no está definida.");
        }
    }



    private void agregarEtiquetaNivel(Jugador jugador) {
        Label labelNivel = jugador.getLabelNivel();

        Integer fila = jugador.getFila();
        Integer columna = jugador.getColumna();

        if (fila != null && columna != null) {
            gridPane.add(labelNivel, columna, fila);

            double yOffset = jugador.getImageView().getBoundsInParent().getMinY() - 22;
            labelNivel.setTranslateY(yOffset);
        } else {
            System.out.println("La ImageView del jugador no tiene posición en el GridPane.");
        }
    }

    private void actualizarPosicionEtiquetaNivel(Jugador jugador) {
        int fila = jugador.getFila();
        int columna = jugador.getColumna();

        gridPane.setRowIndex(jugador.getLabelNivel(), fila);
        gridPane.setColumnIndex(jugador.getLabelNivel(), columna);

        double offsetY = (jugador.getImageView().getY() - gridPane.getBoundsInParent().getMinY()) % CELDA_SIZE;
        jugador.getLabelNivel().setTranslateY(offsetY - 20);
    }

    private void confrontarEnemigo(int filas, int columnas) {
        int fila = jugador.getFila();
        int columna = jugador.getColumna();

        // Verifica enemigos en un radio de 1 alrededor del jugador
        for (int i = Math.max(0, fila - 1); i <= Math.min(filas - 1, fila + 1); i++) {
            for (int j = Math.max(0, columna - 1); j <= Math.min(columnas - 1, columna + 1); j++) {
                if (i != fila || j != columna) {  // Excluye la posición del jugador
                    confrontarEnemigoEnPosicion(i, j);
                }
            }
        }
    }

    private void confrontarEnemigoEnPosicion(int fila, int columna) {
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getFila() == fila && enemigo.getColumna() == columna) {
                confrontar(enemigo);
                break;
            }
        }
    }
    private void confrontar(Enemigo enemigo) {
        if (!enemigo.isDerrotado()) {
            if (jugador.getNivel() > enemigo.getNivelPoder()) {
                // El jugador tiene un nivel superior al enemigo, derrotarlo automáticamente
                enemigo.setDerrotado(true);
                System.out.println("Has derrotado al enemigo. ¡Bien hecho!");

                // Llama al método para actualizar la imagen del enemigo derrotado
                enemigo.getImageView().setImage(new Image("com/sebaescu/mavenproject1/" + enemigo.getTipo() + "Derrotado.png"));
                if(modoDificil){
                    enemigo.getImageView().setFitHeight(CELDA_SIZE_DIFICIL);
                    enemigo.getImageView().setFitWidth(CELDA_SIZE_DIFICIL);
                }
                // Deshabilitar la interacción con el enemigo derrotado
                enemigo.getImageView().setDisable(true);
            } else {
                // El jugador no tiene un nivel superior, realizar la lógica original
                int probabilidadDerrotar = calcularProbabilidad(enemigo.getNivelPoder(), jugador.getNivel());
                System.out.println(probabilidadDerrotar);
                int probabilidad = random.nextInt(100);
                System.out.println(probabilidad);
                if ( probabilidadDerrotar > probabilidad) {
                    enemigo.setDerrotado(true);
                    System.out.println("Has derrotado al enemigo. ¡Bien hecho!");
                    // Llama al método para actualizar la imagen del enemigo derrotado              
                    enemigo.getImageView().setImage(new Image("com/sebaescu/mavenproject1/" + enemigo.getTipo() + "Derrotado.png"));
                    // Deshabilitar la interacción con el enemigo derrotado
                    enemigo.getImageView().setDisable(true);
                } else {
                    System.out.println("No pudiste derrotar al enemigo. ¡Cuidado!");
                    jugador.disminuirNivel();
                    if (jugador.getNivel() <= 0) {
                        mostrarMensaje("¡Perdiste!","¡Tu nivel ha llegado a 0! Has perdido la partida.");
                    }
                }
            }
        } else {
            System.out.println("El enemigo ya está derrotado. Puedes pasar.");
        }
    }
    
    private int calcularProbabilidad(int nivelEnemigo, int nivelJugador) {
        int diferenciaNiveles = nivelEnemigo - nivelJugador;
        return (int) Math.ceil(50/Math.pow(2, diferenciaNiveles));
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
                        salidaFila = i;
                        salidaColumna = j;
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

        encontrarCeldaInicio();
        agregarEtiquetaNivel(jugador);
        jugador.getImageView().setImage(jugadorDer);
        gridPane.add(jugador.getImageView(), jugador.getColumna(), jugador.getFila());
        
        generarCofres(14,7,FILAS_DIFICIL,COLUMNAS_DIFICIL);
        agregarCofresAlGrid();
        generarEnemigos(8, FILAS_DIFICIL, COLUMNAS_DIFICIL,5);
        agregarEnemigosAlGrid();
        Scene scene = new Scene(gridPane, COLUMNAS_DIFICIL * CELDA_SIZE_DIFICIL, FILAS_DIFICIL * CELDA_SIZE_DIFICIL);
        modoDificil =true;
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
        jugador.getImageView().setFitWidth(nuevoAncho);
        jugador.getImageView().setFitHeight(nuevoAlto);

        // Escala la imagen de la salida
        salidaImageView.setFitWidth(nuevoAncho);
        salidaImageView.setFitHeight(nuevoAlto);
    }
    private void mostrarMensaje(String mensaje,String mensaje2) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(mensaje);
            alert.setHeaderText(null);
            alert.setContentText(mensaje2);
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

