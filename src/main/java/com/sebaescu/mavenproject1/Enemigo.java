/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sebaescu.mavenproject1;

/**
 *
 * @author Sebastian
 */
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Enemigo {
    private int fila;
    private int columna;
    private int nivelPoder;
    private Image imagenEnemigo;

    public Enemigo(int fila, int columna, int nivelPoder, Image imagenEnemigo) {
        this.fila = fila;
        this.columna = columna;
        this.nivelPoder = nivelPoder;
        this.imagenEnemigo = imagenEnemigo;
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
}
