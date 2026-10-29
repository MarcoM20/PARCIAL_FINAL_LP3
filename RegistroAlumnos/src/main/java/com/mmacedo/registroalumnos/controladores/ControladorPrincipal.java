package com.mmacedo.registroalumnos.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import com.mmacedo.registroalumnos.modelo.Alumno;
import com.mmacedo.registroalumnos.bd.BaseDatosAlumnos;
import com.mmacedo.registroalumnos.tareas.TareaBD;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControladorPrincipal {

    private BaseDatosAlumnos baseDatos;
    private List<Alumno> alumnosActuales = new ArrayList<>();

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

    @FXML
    public void initialize() {
        baseDatos = new BaseDatosAlumnos();

        btnGuardar.setOnAction(e -> registrarAlumnos());
        btnCargar.setOnAction(e -> cargarAlumnos());
        btnExportar.setOnAction(e -> exportarAExcel());

        pbProgreso.setVisible(false);
        lblEstado.setText("Sistema listo");
    }

    private void registrarAlumnos() {
        Window ventana = btnGuardar.getScene().getWindow();

        TextInputDialog preguntaCantidad = new TextInputDialog("1");
        preguntaCantidad.initOwner(ventana);
        preguntaCantidad.setTitle("Cantidad de alumnos");
        preguntaCantidad.setHeaderText(null);
        preguntaCantidad.setContentText("Cuantos alumnos desea registrar?");

        Optional<String> respuesta = preguntaCantidad.showAndWait();
        if (!respuesta.isPresent()) {
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(respuesta.get().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ingrese un numero entero.");
            return;
        }

        if (cantidad < 1 || cantidad > 200) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "La cantidad debe estar entre 1 y 200.");
            return;
        }

        IngresoCalificacionesDialogo.mostrar(ventana, cantidad)
                .ifPresent(this::guardarAlumnos);
    }

    private void guardarAlumnos(List<Alumno> lista) {
        Task<Integer> tarea = new Task<Integer>() {
            @Override
            protected Integer call() {
                int guardados = 0;
                for (Alumno alumno : lista) {
                    if (baseDatos.insertarAlumno(alumno)) {
                        guardados++;
                    }
                }
                return guardados;
            }
        };

        tarea.setOnSucceeded(e -> {
            int guardados = tarea.getValue();
            lblEstado.setText("Alumnos guardados: " + guardados + " de " + lista.size());
            if (guardados < lista.size()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atencion",
                        "Algunos alumnos no pudieron guardarse.");
            }
            cargarAlumnos();
        });

        tarea.setOnFailed(e -> {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron guardar los alumnos.");
            lblEstado.setText("Error al guardar");
        });

        ejecutar(tarea);
    }

    private void cargarAlumnos() {
        TareaBD.ObtenerAlumnos tarea = new TareaBD.ObtenerAlumnos(baseDatos);

        tarea.setOnSucceeded(e -> {
            alumnosActuales = tarea.getValue();
            taContenido.setText(construirResumen(alumnosActuales));
            lblEstado.setText("Alumnos cargados: " + alumnosActuales.size());
        });

        tarea.setOnFailed(e -> {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los alumnos.");
            lblEstado.setText("Error al cargar");
        });

        ejecutar(tarea);
    }

    private String construirResumen(List<Alumno> lista) {
        StringBuilder sb = new StringBuilder();
        sb.append("Total registrados: ").append(lista.size()).append("\n\n");

        for (Alumno a : lista) {
            sb.append("ID: ").append(a.getId()).append("\n");
            sb.append("Nombre: ").append(a.getNombre()).append(" ")
                    .append(a.getApellido()).append("\n");
            sb.append("Carrera: ").append(a.getCarrera()).append("\n");
            sb.append("Semestre: ").append(a.getSemestre()).append("\n");
            sb.append("Promedio: ").append(String.format("%.2f", a.calcularPromedio())).append("\n");
            sb.append("---\n");
        }
        return sb.toString();
    }

    private void exportarAExcel() {
        if (alumnosActuales.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                    "No hay alumnos para exportar. Cargue los datos primero.");
            return;
        }

        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar archivo Excel");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
        selector.setInitialFileName("alumnos.xlsx");

        File archivo = selector.showSaveDialog(btnExportar.getScene().getWindow());
        if (archivo == null) {
            return;
        }

        TareaBD.ExportarExcel tarea = new TareaBD.ExportarExcel(
                baseDatos, alumnosActuales, archivo.getAbsolutePath());

        pbProgreso.setVisible(true);
        pbProgreso.progressProperty().bind(tarea.progressProperty());
        lblEstado.textProperty().bind(tarea.messageProperty());

        tarea.setOnSucceeded(e -> {
            terminarExportacion();
            boolean exportado = tarea.getValue();
            if (exportado) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Exito",
                        "Archivo exportado en:\n" + archivo.getAbsolutePath());
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo exportar el archivo.");
            }
        });

        tarea.setOnFailed(e -> {
            terminarExportacion();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrio un error durante la exportacion.");
        });

        ejecutar(tarea);
    }

    private void terminarExportacion() {
        pbProgreso.progressProperty().unbind();
        lblEstado.textProperty().unbind();
        pbProgreso.setVisible(false);
    }

    private void ejecutar(Task<?> tarea) {
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    public void cerrar() {
        if (baseDatos != null) {
            baseDatos.desconectar();
        }
    }
}