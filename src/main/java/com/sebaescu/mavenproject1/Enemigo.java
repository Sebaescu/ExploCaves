/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sebaescu.mavenproject1;

/**
 *
 * @author Sebastian
 */
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Enemigo {
    private int fila;
    private int columna;
    private int nivelPoder;
    private Image imagenEnemigo;
    private boolean derrotado;
    private String tipo;
    public static final ArrayList<String> tiposDeEnemigos = new ArrayList<>(Arrays.asList("Lobo", "Gusano", "Araña"));
    private Random random = new Random();

    public Enemigo(int fila, int columna, int nivelPoder, String tipo) {
        this.fila = fila;
        this.columna = columna;
        this.nivelPoder = nivelPoder;
        this.tipo = tiposDeEnemigos.get(random.nextInt(tiposDeEnemigos.size()));
        this.derrotado = false;
        this.imagenEnemigo = new Image("com/sebaescu/mavenproject1/" + tipo + ".png");  // La imagen se basa en el tipo
    }

    public ImageView getImageView() {
        ImageView imageView = new ImageView(imagenEnemigo);
        imageView.setFitWidth(App.CELDA_SIZE);
        imageView.setFitHeight(App.CELDA_SIZE);
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
    public String getNombreImagen() {
        return tipo + (derrotado ? "Derrotado" : "") + ".png";
    }

}

