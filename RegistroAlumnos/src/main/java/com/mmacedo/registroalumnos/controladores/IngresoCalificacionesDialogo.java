package com.mmacedo.registroalumnos.controladores;

import com.mmacedo.registroalumnos.modelo.Alumno;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class IngresoCalificacionesDialogo {

    private static final String[] TITULOS_NOTAS = {
        "Evidencia 1", "Evidencia 2", "Evidencia 3", "Evidencia 4", "Parcial medio", "Final"
    };

    private final Dialog<List<Alumno>> dialogo = new Dialog<>();
    private final TableView<Fila> tabla = new TableView<>();
    private final TextField txtCarrera = new TextField();
    private final TextField txtSemestre = new TextField();
    private List<Alumno> alumnos;

    private IngresoCalificacionesDialogo(Window propietario, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            tabla.getItems().add(new Fila());
        }

        dialogo.setTitle("Registro de calificaciones");
        dialogo.initOwner(propietario);

        txtCarrera.setPromptText("Ej. Ingenieria");
        txtSemestre.setPromptText("Ej. 3");
        txtSemestre.setPrefColumnCount(5);
        HBox datos = new HBox(10, new Label("Carrera:"), txtCarrera,
                new Label("Semestre:"), txtSemestre);
        datos.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtCarrera, Priority.ALWAYS);

        tabla.setEditable(true);
        tabla.getColumns().add(columna("Nombre", f -> f.nombre, 140));
        tabla.getColumns().add(columna("Apellido", f -> f.apellido, 140));
        for (int i = 0; i < TITULOS_NOTAS.length; i++) {
            final int indice = i;
            tabla.getColumns().add(columna(TITULOS_NOTAS[i], f -> f.notas[indice], 95));
        }

        VBox contenido = new VBox(10, datos,
                new Label("Notas de 0 a 20. Las filas en blanco se ignoran."), tabla);
        contenido.setPadding(new Insets(12));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        DialogPane panel = dialogo.getDialogPane();
        panel.setContent(contenido);
        panel.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        panel.setPrefSize(950, 520);

        Button btnOk = (Button) panel.lookupButton(ButtonType.OK);
        btnOk.addEventFilter(ActionEvent.ACTION, ev -> {
            if (!validar()) {
                ev.consume();
            }
        });

        dialogo.setResultConverter(boton -> boton == ButtonType.OK ? alumnos : null);
    }

    public static Optional<List<Alumno>> mostrar(Window propietario, int cantidad) {
        return new IngresoCalificacionesDialogo(propietario, cantidad).dialogo.showAndWait();
    }

    private boolean validar() {
        try {
            alumnos = construirAlumnos();
            return true;
        } catch (IllegalArgumentException ex) {
            Alert alerta = new Alert(Alert.AlertType.WARNING, ex.getMessage());
            alerta.setHeaderText(null);
            alerta.showAndWait();
            return false;
        }
    }

    private List<Alumno> construirAlumnos() {
        String carrera = txtCarrera.getText().trim();
        if (carrera.isEmpty()) {
            throw new IllegalArgumentException("Ingrese la carrera.");
        }

        int semestre;
        try {
            semestre = Integer.parseInt(txtSemestre.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El semestre debe ser un numero entero.");
        }
        if (semestre < 1) {
            throw new IllegalArgumentException("El semestre debe ser mayor que 0.");
        }

        List<Alumno> lista = new ArrayList<>();
        for (int f = 0; f < tabla.getItems().size(); f++) {
            Fila fila = tabla.getItems().get(f);
            if (fila.estaVacia()) {
                continue;
            }

            String prefijo = "Fila " + (f + 1) + ": ";
            String nombre = fila.nombre.get().trim();
            String apellido = fila.apellido.get().trim();
            if (nombre.isEmpty() || apellido.isEmpty()) {
                throw new IllegalArgumentException(prefijo + "falta el nombre o el apellido.");
            }

            double[] notas = new double[TITULOS_NOTAS.length];
            for (int n = 0; n < notas.length; n++) {
                notas[n] = leerNota(fila.notas[n].get(), prefijo + TITULOS_NOTAS[n]);
            }

            Alumno alumno = new Alumno(nombre, apellido, carrera, semestre);
            alumno.setEvidencia1(notas[0]);
            alumno.setEvidencia2(notas[1]);
            alumno.setEvidencia3(notas[2]);
            alumno.setEvidencia4(notas[3]);
            alumno.setExamenParcial(notas[4]);
            alumno.setExamenFinal(notas[5]);
            lista.add(alumno);
        }

        if (lista.isEmpty()) {
            throw new IllegalArgumentException("No hay alumnos para guardar.");
        }
        return lista;
    }

    private static double leerNota(String texto, String campo) {
        String limpio = texto.trim().replace(',', '.');
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException(campo + ": falta la nota.");
        }

        double nota;
        try {
            nota = Double.parseDouble(limpio);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(campo + ": no es un numero valido.");
        }

        if (!(nota >= 0 && nota <= 20)) {
            throw new IllegalArgumentException(campo + ": debe estar entre 0 y 20.");
        }
        return nota;
    }

    // Método modificado para usar la nueva CeldaEditable
    private static TableColumn<Fila, String> columna(String titulo,
            Function<Fila, StringProperty> campo, double ancho) {
        TableColumn<Fila, String> columna = new TableColumn<>(titulo);
        columna.setPrefWidth(ancho);
        columna.setCellValueFactory(celda -> campo.apply(celda.getValue()));
        columna.setCellFactory(c -> new CeldaEditable());
        columna.setOnEditCommit(ev -> campo.apply(ev.getRowValue()).set(ev.getNewValue()));
        return columna;
    }

    private static class Fila {
        final StringProperty nombre = new SimpleStringProperty("");
        final StringProperty apellido = new SimpleStringProperty("");
        final StringProperty[] notas = new StringProperty[TITULOS_NOTAS.length];

        Fila() {
            for (int i = 0; i < notas.length; i++) {
                notas[i] = new SimpleStringProperty("");
            }
        }

        boolean estaVacia() {
            if (!nombre.get().trim().isEmpty() || !apellido.get().trim().isEmpty()) {
                return false;
            }
            for (StringProperty nota : notas) {
                if (!nota.get().trim().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

    // Clase añadida dentro de IngresoCalificacionesDialogo
    private static class CeldaEditable extends TableCell<Fila, String> {
        private final TextField editor = new TextField();

        CeldaEditable() {
            editor.setOnAction(e -> commitEdit(editor.getText()));
            editor.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
            editor.focusedProperty().addListener((obs, eraAntes, tieneFoco) -> {
                if (!tieneFoco && isEditing()) {
                    commitEdit(editor.getText());
                }
            });
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (!isEditing()) {
                return;
            }
            editor.setText(getItem() == null ? "" : getItem());
            setText(null);
            setGraphic(editor);
            editor.requestFocus();
            editor.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem());
            setGraphic(null);
        }

        @Override
        protected void updateItem(String item, boolean vacia) {
            super.updateItem(item, vacia);
            if (vacia) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                editor.setText(item);
                setText(null);
                setGraphic(editor);
            } else {
                setText(item);
                setGraphic(null);
            }
        }
    }
}
