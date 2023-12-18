/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sebaescu.mavenproject1;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Sebastian
 */

public class Cofre {
    private final int fila;
    private final int columna;
    private final Image imagen;
    private final String tipoCofre;
    private final boolean cambiaImagen;

    public Cofre(int fila, int columna, Image imagen, String tipoCofre, boolean cambiaImagen) {
        this.fila = fila;
        this.columna = columna;
        this.imagen = imagen;
        this.tipoCofre = tipoCofre;
        this.cambiaImagen = cambiaImagen;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public Image getImagen() {
        return imagen;
    }

    public String getTipoCofre() {
        return tipoCofre;
    }

    public boolean isCambiaImagen() {
        return cambiaImagen;
    }
}

