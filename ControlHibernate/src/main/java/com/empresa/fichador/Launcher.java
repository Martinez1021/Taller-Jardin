package com.empresa.fichador;

import javafx.application.Application;

/**
 * Clase lanzadora para evitar el problema de JavaFX con el método main
 * en versiones modernas de Java (11+).
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
