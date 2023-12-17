/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sebaescu.mavenproject1;

import javafx.scene.image.Image;

/**
 *
 * @author Sebastian
 */
public class Cofre {
        private final int fila;
        private final int columna;
        private final Image imagen;

        public Cofre(int fila, int columna, Image imagen) {
            this.fila = fila;
            this.columna = columna;
            this.imagen = imagen;
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
    }
