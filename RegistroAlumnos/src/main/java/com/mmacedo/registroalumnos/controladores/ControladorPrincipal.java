package com.mmacedo.registroalumnos.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.mmacedo.registroalumnos.modelo.Alumno;
import com.mmacedo.registroalumnos.bd.BaseDatosAlumnos;
import com.mmacedo.registroalumnos.tareas.TareaBD;
import java.io.File;
import java.util.List;

/**
 * Controlador principal que maneja la interfaz gráfica y la base de datos
 * Ejemplo de cómo integrar JavaFX con SQLite
 */
public class ControladorPrincipal {
    
    // Variables de BD
    private BaseDatosAlumnos baseDatos;
    private List<Alumno> alumnosActuales;
    
    // Componentes de la UI (ejemplo - adapta según tu interfaz)
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnExportar;
    
    @FXML
    private Button btnCargar;
    
    @FXML
    private Label lblEstado;
    
    @FXML
    private ProgressBar pbProgreso;
    
    @FXML
    private TextArea taContenido;
    
    /**
     * Inicializa el controlador
     * Se ejecuta automáticamente después de cargar el FXML
     */
    @FXML
    public void initialize() {
        // Inicializar base de datos
        baseDatos = new BaseDatosAlumnos();
        
        // Configurar listeners
        configurarListeners();
        
        // Ocultar barra de progreso por defecto
        pbProgreso.setStyle("-fx-opacity: 0;");
        
        lblEstado.setText("✓ Sistema listo");
    }
    
    /**
     * Configura los listeners de los botones
     */
    private void configurarListeners() {
        btnGuardar.setOnAction(e -> guardarAlumno());
        btnCargar.setOnAction(e -> cargarAlumnos());
        btnExportar.setOnAction(e -> exportarAExcel());
    }
    
    /**
     * Guarda un alumno en la base de datos (en un thread separado)
     */
    private void guardarAlumno() {
        // TODO: Obtener datos de los campos de la UI
        Alumno alumno = new Alumno("Juan", "Pérez", "Ingeniería", 3);
        alumno.setEvidencia1(15);
        alumno.setEvidencia2(16);
        alumno.setExamenParcial(17);
        alumno.setEvidencia3(14);
        alumno.setEvidencia4(18);
        alumno.setExamenFinal(16);
        
        // Crear tarea en thread separado
        TareaBD.InsertarAlumno tarea = new TareaBD.InsertarAlumno(baseDatos, alumno);
        
        // Actualizar UI cuando termina
        tarea.setOnSucceeded(e -> {
            boolean resultado = tarea.getValue();
            if (resultado) {
                mostrarAlerta(Alert.AlertType.INFORMATION, 
                    "Éxito", "Alumno guardado correctamente");
                lblEstado.setText("✓ Alumno guardado");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, 
                    "Error", "No se pudo guardar el alumno");
                lblEstado.setText("✗ Error al guardar");
            }
        });
        
        tarea.setOnFailed(e -> {
            mostrarAlerta(Alert.AlertType.ERROR, 
                "Error", "Error en la operación: " + tarea.getException());
            lblEstado.setText("✗ Error");
        });
        
        lblEstado.setText(tarea.getMessage());
        
        // Ejecutar tarea en un thread separado
        Thread thread = new Thread(tarea);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Carga todos los alumnos de la base de datos
     */
    private void cargarAlumnos() {
        // Crear tarea
        TareaBD.ObtenerAlumnos tarea = new TareaBD.ObtenerAlumnos(baseDatos);
        
        tarea.setOnSucceeded(e -> {
            alumnosActuales = tarea.getValue();
            
            // Mostrar en la UI
            StringBuilder sb = new StringBuilder();
            sb.append("=== REGISTRO DE ALUMNOS ===\n");
            sb.append("Total registrados: ").append(alumnosActuales.size()).append("\n\n");
            
            for (Alumno alumno : alumnosActuales) {
                sb.append("ID: ").append(alumno.getId()).append("\n");
                sb.append("Nombre: ").append(alumno.getNombre()).append(" ")
                  .append(alumno.getApellido()).append("\n");
                sb.append("Carrera: ").append(alumno.getCarrera()).append("\n");
                sb.append("Semestre: ").append(alumno.getSemestre()).append("\n");
                sb.append("Promedio: ").append(String.format("%.2f", alumno.calcularPromedio()))
                  .append("\n");
                sb.append("---\n");
            }
            
            taContenido.setText(sb.toString());
            lblEstado.setText("✓ Alumnos cargados: " + alumnosActuales.size());
        });
        
        tarea.setOnFailed(e -> {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                "No se pudieron cargar los alumnos");
            lblEstado.setText("✗ Error al cargar");
        });
        
        // Ejecutar en thread separado
        Thread thread = new Thread(tarea);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Exporta los alumnos a un archivo Excel
     */
    private void exportarAExcel() {
        if (alumnosActuales == null || alumnosActuales.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", 
                "No hay alumnos para exportar. Carga los datos primero.");
            return;
        }
        
        // Diálogo para elegir ubicación del archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo Excel");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx")
        );
        fileChooser.setInitialFileName("alumnos.xlsx");
        
        File archivo = fileChooser.showSaveDialog(btnExportar.getScene().getWindow());
        
        if (archivo != null) {
            // Mostrar progreso
            pbProgreso.setStyle("-fx-opacity: 1.0;");
            
            // Crear tarea de exportación
            TareaBD.ExportarExcel tarea = new TareaBD.ExportarExcel(
            baseDatos, (List<Alumno>) alumnosActuales, archivo.getAbsolutePath()
            );
            
            // Actualizar barra de progreso
            pbProgreso.progressProperty().bind(tarea.progressProperty());
            lblEstado.textProperty().bind(tarea.messageProperty());
            
            tarea.setOnSucceeded(e -> {
                boolean resultado = tarea.getValue();
                pbProgreso.setStyle("-fx-opacity: 0;");
                
                if (resultado) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Archivo exportado correctamente:\n" + archivo.getAbsolutePath());
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo exportar el archivo");
                }
                
                pbProgreso.progressProperty().unbind();
            });
            
            tarea.setOnFailed(e -> {
                pbProgreso.setStyle("-fx-opacity: 0;");
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error durante la exportación: " + tarea.getException());
                pbProgreso.progressProperty().unbind();
            });
            
            // Ejecutar en thread separado
            Thread thread = new Thread(tarea);
            thread.setDaemon(true);
            thread.start();
        }
    }
    
    /**
     * Muestra una alerta al usuario
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(mensaje);
            alerta.showAndWait();
        });
    }
    
    /**
     * Se ejecuta al cerrar la aplicación
     */
    public void cerrar() {
        if (baseDatos != null) {
            baseDatos.desconectar();
        }
    }
}
