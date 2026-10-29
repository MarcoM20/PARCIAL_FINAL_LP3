package com.mmacedo.registroalumnos.aplicacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import controladores.ControladorPrincipal;

import java.io.IOException;

/**
 * Clase principal de la aplicación
 * Punto de entrada para la aplicación JavaFX
 */
public class MainApp extends Application {
    
    private ControladorPrincipal controlador;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/principal.fxml")
            );
            Parent root = loader.load();
            
            // Obtener el controlador
            controlador = loader.getController();
            
            // Crear la escena
            Scene scene = new Scene(root);
            
            // Configurar el escenario
            primaryStage.setTitle("Registro de Alumnos - Sistema de Gestión");
            primaryStage.setScene(scene);
            primaryStage.setWidth(900);
            primaryStage.setHeight(700);
            primaryStage.setResizable(true);
            
            // Manejar el cierre de la aplicación
            primaryStage.setOnCloseRequest(event -> {
                controlador.cerrar();
                System.exit(0);
            });
            
            // Mostrar la ventana
            primaryStage.show();
            
            System.out.println("✓ Aplicación iniciada correctamente");
            
        } catch (IOException e) {
            System.err.println("✗ Error al cargar la interfaz gráfica");
            e.printStackTrace();
        }
    }
    
    /**
     * Punto de entrada de la aplicación
     */
    public static void main(String[] args) {
        launch(args);
    }
}
