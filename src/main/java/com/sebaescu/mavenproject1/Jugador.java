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
public class Jugador {
    private int fila;
    private int columna;
    private int nivel;
    private Image imagen;
    private ImageView imageView;

    public Jugador(int fila, int columna, int nivel, Image imagen) {
        this.fila = fila;
        this.columna = columna;
        this.nivel = nivel;
        this.imagen = imagen;
        this.imageView = new ImageView(imagen); // Crear la instancia de ImageView aquí
    }
    public ImageView getImageView() {
        return imageView;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }
    public void aumentarNivel() {
        nivel++;
    }
    // Nuevo método para combatir con un enemigo
    public boolean combatir(Enemigo enemigo) {
        // Calcular la probabilidad de derrota
        double probabilidadDerrota = calcularProbabilidadDerrota(enemigo.getNivelPoder());

        // Generar un número aleatorio entre 0 y 1
        double resultadoCombate = Math.random();

        // El jugador derrota al enemigo si el resultado es menor que la probabilidad de derrota
        boolean jugadorGana = resultadoCombate < probabilidadDerrota;

        if (jugadorGana) {
            // Aumentar el nivel del jugador al derrotar al enemigo
            aumentarNivel();
        }

        return jugadorGana;
    }

    // Nuevo método para calcular la probabilidad de derrota
    private double calcularProbabilidadDerrota(int nivelEnemigo) {
        int diferenciaNiveles = nivelEnemigo - nivel;
        return Math.max(0.5, 0.5 - (diferenciaNiveles / 2.0) * 0.1);
    }
}
