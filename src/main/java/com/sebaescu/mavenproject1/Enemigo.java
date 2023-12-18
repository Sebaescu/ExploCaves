/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sebaescu.mavenproject1;

/**
 *
 * @author Sebastian
 */
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Enemigo {
    private int fila;
    private int columna;
    private int nivelPoder;
    private Image imagenEnemigo;
    private boolean derrotado;
    private String tipo;
    public static final ArrayList<String> tiposDeEnemigos = new ArrayList<>(Arrays.asList("Lobo", "Gusano", "Arana"));
    private Random random = new Random();
    private Label labelNivel;
    private ImageView imageView;

    public Enemigo(int fila, int columna, int nivelPoder, String tipoEnemigo) {
        this.fila = fila;
        this.columna = columna;
        this.nivelPoder = nivelPoder;
        this.tipo = tiposDeEnemigos.get(random.nextInt(tiposDeEnemigos.size()));
        this.derrotado = false;
        this.labelNivel = new Label("Nivel " + nivelPoder); // Inicializar la etiqueta del nivel aquí
        configurarEstiloLabel();
        this.imagenEnemigo = new Image("com/sebaescu/mavenproject1/" + getNombreImagen());
    }

    public ImageView getImageView() {
        if (imageView == null) {
            // Si la instancia no ha sido creada, crea una nueva
            imageView = new ImageView(imagenEnemigo);
            imageView.setFitWidth(App.CELDA_SIZE);
            imageView.setFitHeight(App.CELDA_SIZE);
        }
        return imageView;
    }


    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public int getNivelPoder() {
        return nivelPoder;
    }

    public Label getLabelNivel() {
        return labelNivel;
    }

    public void setNivelPoder(int nivelPoder) {
        this.nivelPoder = nivelPoder;
        actualizarTextoLabel();
    }

    public boolean isDerrotado() {
        return derrotado;
    }

    public void setDerrotado(boolean derrotado) {
        this.derrotado = derrotado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    private void actualizarTextoLabel() {
        labelNivel.setText("Nivel " + nivelPoder);
    }

    private void configurarEstiloLabel() {
        // Configurar el estilo del label según tus necesidades
        labelNivel.setFont(Font.font("Arial", 14));
        labelNivel.setTextFill(Color.WHITE);
    }
    public void actualizarImagenEnemigoDerrotado() {
        // Cambia la imagen del enemigo derrotado
        if (isDerrotado()) {
            getImageView().setImage(new Image("com/sebaescu/mavenproject1/" + tipo + "Derrotado.png"));
        }
    }

    public String getNombreImagen() {
        return tipo + (derrotado ? "Derrotado" : "") + ".png";
    }
}
