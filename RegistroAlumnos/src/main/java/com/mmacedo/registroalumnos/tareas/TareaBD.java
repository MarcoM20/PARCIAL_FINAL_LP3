package com.mmacedo.registroalumnos.tareas;

import javafx.concurrent.Task;
import modelo.Alumno;
import bd.BaseDatosAlumnos;
import utilidades.ExportadorExcel;
import java.util.List;

/**
 * Clase abstracta que extiende Task de JavaFX
 * Permite ejecutar operaciones de base de datos en un thread separado
 * sin bloquear la UI principal
 */
public abstract class TareaBD<V> extends Task<V> {
    
    protected BaseDatosAlumnos bd;
    
    public TareaBD(BaseDatosAlumnos bd) {
        this.bd = bd;
    }
    
    /**
     * Tarea para insertar un alumno
     */
    public static class InsertarAlumno extends TareaBD<Boolean> {
        private final Alumno alumno;
        
        public InsertarAlumno(BaseDatosAlumnos bd, Alumno alumno) {
            super(bd);
            this.alumno = alumno;
        }
        
        @Override
        protected Boolean call() {
            updateMessage("Insertando alumno...");
            boolean resultado = bd.insertarAlumno(alumno);
            updateMessage(resultado ? "✓ Alumno insertado" : "✗ Error al insertar");
            return resultado;
        }
    }
    
    /**
     * Tarea para obtener todos los alumnos
     */
    public static class ObtenerAlumnos extends TareaBD<List<Alumno>> {
        
        public ObtenerAlumnos(BaseDatosAlumnos bd) {
            super(bd);
        }
        
        @Override
        protected List<Alumno> call() {
            updateMessage("Cargando alumnos...");
            List<Alumno> alumnos = bd.obtenerTodos();
            updateMessage("✓ Alumnos cargados: " + alumnos.size());
            return alumnos;
        }
    }
    
    /**
     * Tarea para actualizar un alumno
     */
    public static class ActualizarAlumno extends TareaBD<Boolean> {
        private final Alumno alumno;
        
        public ActualizarAlumno(BaseDatosAlumnos bd, Alumno alumno) {
            super(bd);
            this.alumno = alumno;
        }
        
        @Override
        protected Boolean call() {
            updateMessage("Actualizando alumno...");
            boolean resultado = bd.actualizarAlumno(alumno);
            updateMessage(resultado ? "✓ Alumno actualizado" : "✗ Error al actualizar");
            return resultado;
        }
    }
    
    /**
     * Tarea para eliminar un alumno
     */
    public static class EliminarAlumno extends TareaBD<Boolean> {
        private final int id;
        
        public EliminarAlumno(BaseDatosAlumnos bd, int id) {
            super(bd);
            this.id = id;
        }
        
        @Override
        protected Boolean call() {
            updateMessage("Eliminando alumno...");
            boolean resultado = bd.eliminarAlumno(id);
            updateMessage(resultado ? "✓ Alumno eliminado" : "✗ Error al eliminar");
            return resultado;
        }
    }
    
    /**
     * Tarea para exportar a Excel
     */
    public static class ExportarExcel extends TareaBD<Boolean> {
        private final List<Alumno> alumnos;
        private final String rutaArchivo;
        
        public ExportarExcel(BaseDatosAlumnos bd, List<Alumno> alumnos, String rutaArchivo) {
            super(bd);
            this.alumnos = alumnos;
            this.rutaArchivo = rutaArchivo;
        }
        
        @Override
        protected Boolean call() {
            updateMessage("Exportando a Excel...");
            
            // Mostrar progreso
            int total = alumnos.size();
            for (int i = 0; i < total; i++) {
                updateProgress(i, total);
                updateMessage("Procesando alumno " + (i + 1) + " de " + total);
                try {
                    Thread.sleep(10); // Simular procesamiento
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            boolean resultado = ExportadorExcel.exportarAlumnos(alumnos, rutaArchivo);
            updateProgress(total, total);
            updateMessage(resultado ? "✓ Archivo exportado: " + rutaArchivo : 
                                    "✗ Error al exportar");
            return resultado;
        }
    }
}
