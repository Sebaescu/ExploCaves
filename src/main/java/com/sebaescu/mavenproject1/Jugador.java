/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sebaescu.mavenproject1;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Jugador {
    private int fila;
    private int columna;
    private int nivel;
    private Image imagen;
    private ImageView imageView;
    private Label labelNivel; 

    public Jugador(int fila, int columna, int nivel, Image imagen) {
        this.fila = fila;
        this.columna = columna;
        this.nivel = nivel;
        this.imagen = imagen;
        this.imageView = new ImageView(imagen);
        this.labelNivel = new Label("Nivel " + nivel); // Inicializar la etiqueta del nivel aquí
        configurarEstiloLabel(); // Método para configurar el estilo del label (puedes definirlo según tus necesidades)
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Label getLabelNivel() {
        return labelNivel;
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
        actualizarTextoLabel(); // Actualizar el texto del label cuando cambia el nivel
    }

    public void aumentarNivel() {
        nivel++;
        System.out.println(nivel);
        actualizarTextoLabel(); // Actualizar el texto del label al aumentar el nivel
    }
    public void disminuirNivel() {
        nivel--;
        actualizarTextoLabel(); // Actualizar el texto del label al aumentar el nivel
    }
    private void actualizarTextoLabel() {
        labelNivel.setText("Nivel " + nivel);
    }

    private void configurarEstiloLabel() {
        // Configurar el estilo del label según tus necesidades
        labelNivel.setFont(Font.font("Arial", 14));
        labelNivel.setTextFill(Color.WHITE);
    }
    
}

