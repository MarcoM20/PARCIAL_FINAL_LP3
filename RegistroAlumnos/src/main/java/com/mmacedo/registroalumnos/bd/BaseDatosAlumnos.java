package com.mmacedo.registroalumnos.bd;

import com.mmacedo.registroalumnos.modelo.Alumno;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para gestionar la conexión y operaciones con la base de datos SQLite
 * Implementa el patrón DAO (Data Access Object)
 */
public class BaseDatosAlumnos {
    
    private static final String URL = "jdbc:sqlite:alumnos.db";
    private Connection conexion;
    
    /**
     * Constructor que inicializa la base de datos
     */
    public BaseDatosAlumnos() {
        inicializarBaseDatos();
    }
    
    /**
     * Establece conexión con la base de datos SQLite
     */
    public void conectar() {
        try {
            Class.forName("org.sqlite.JDBC");
            conexion = DriverManager.getConnection(URL);
            System.out.println("✓ Conexión a BD establecida correctamente");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ Error: Driver JDBC no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("✗ Error al conectar a la BD");
            e.printStackTrace();
        }
    }
    
    /**
     * Cierra la conexión con la base de datos
     */
    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("✓ Conexión cerrada");
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al cerrar la conexión");
            e.printStackTrace();
        }
    }
    
    /**
     * Crea las tablas si no existen
     */
    private void inicializarBaseDatos() {
        conectar();
        
        String sqlCrearTabla = "CREATE TABLE IF NOT EXISTS alumnos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "apellido TEXT NOT NULL," +
                "carrera TEXT NOT NULL," +
                "semestre INTEGER NOT NULL," +
                "evidencia1 REAL DEFAULT 0," +
                "evidencia2 REAL DEFAULT 0," +
                "examen_parcial REAL DEFAULT 0," +
                "evidencia3 REAL DEFAULT 0," +
                "evidencia4 REAL DEFAULT 0," +
                "examen_final REAL DEFAULT 0," +
                "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sqlCrearTabla);
            System.out.println("✓ Tabla 'alumnos' lista");
        } catch (SQLException e) {
            System.out.println("✗ Error al crear tabla");
            e.printStackTrace();
        }
    }
    
    /**
     * Inserta un alumno en la base de datos
     * @param alumno El alumno a insertar
     * @return true si fue exitoso, false en caso contrario
     */
    public boolean insertarAlumno(Alumno alumno) {
        String sql = "INSERT INTO alumnos (nombre, apellido, carrera, semestre, " +
                "evidencia1, evidencia2, examen_parcial, evidencia3, evidencia4, examen_final) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, alumno.getNombre());
            pstmt.setString(2, alumno.getApellido());
            pstmt.setString(3, alumno.getCarrera());
            pstmt.setInt(4, alumno.getSemestre());
            pstmt.setDouble(5, alumno.getEvidencia1());
            pstmt.setDouble(6, alumno.getEvidencia2());
            pstmt.setDouble(7, alumno.getExamenParcial());
            pstmt.setDouble(8, alumno.getEvidencia3());
            pstmt.setDouble(9, alumno.getEvidencia4());
            pstmt.setDouble(10, alumno.getExamenFinal());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("✗ Error al insertar alumno");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene todos los alumnos de la base de datos
     * @return Lista de alumnos
     */
    public List<Alumno> obtenerTodos() {
        List<Alumno> alumnos = new ArrayList<>();
        String sql = "SELECT * FROM alumnos";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Alumno alumno = new Alumno();
                alumno.setId(rs.getInt("id"));
                alumno.setNombre(rs.getString("nombre"));
                alumno.setApellido(rs.getString("apellido"));
                alumno.setCarrera(rs.getString("carrera"));
                alumno.setSemestre(rs.getInt("semestre"));
                alumno.setEvidencia1(rs.getDouble("evidencia1"));
                alumno.setEvidencia2(rs.getDouble("evidencia2"));
                alumno.setExamenParcial(rs.getDouble("examen_parcial"));
                alumno.setEvidencia3(rs.getDouble("evidencia3"));
                alumno.setEvidencia4(rs.getDouble("evidencia4"));
                alumno.setExamenFinal(rs.getDouble("examen_final"));
                
                alumnos.add(alumno);
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al obtener alumnos");
            e.printStackTrace();
        }
        
        return alumnos;
    }
    
    /**
     * Obtiene un alumno por su ID
     * @param id El ID del alumno
     * @return El alumno encontrado o null
     */
    public Alumno obtenerPorId(int id) {
        String sql = "SELECT * FROM alumnos WHERE id = ?";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Alumno alumno = new Alumno();
                alumno.setId(rs.getInt("id"));
                alumno.setNombre(rs.getString("nombre"));
                alumno.setApellido(rs.getString("apellido"));
                alumno.setCarrera(rs.getString("carrera"));
                alumno.setSemestre(rs.getInt("semestre"));
                alumno.setEvidencia1(rs.getDouble("evidencia1"));
                alumno.setEvidencia2(rs.getDouble("evidencia2"));
                alumno.setExamenParcial(rs.getDouble("examen_parcial"));
                alumno.setEvidencia3(rs.getDouble("evidencia3"));
                alumno.setEvidencia4(rs.getDouble("evidencia4"));
                alumno.setExamenFinal(rs.getDouble("examen_final"));
                
                return alumno;
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al obtener alumno");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Actualiza los datos de un alumno
     * @param alumno El alumno con datos actualizados
     * @return true si fue exitoso
     */
    public boolean actualizarAlumno(Alumno alumno) {
        String sql = "UPDATE alumnos SET nombre = ?, apellido = ?, carrera = ?, " +
                "semestre = ?, evidencia1 = ?, evidencia2 = ?, examen_parcial = ?, " +
                "evidencia3 = ?, evidencia4 = ?, examen_final = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, alumno.getNombre());
            pstmt.setString(2, alumno.getApellido());
            pstmt.setString(3, alumno.getCarrera());
            pstmt.setInt(4, alumno.getSemestre());
            pstmt.setDouble(5, alumno.getEvidencia1());
            pstmt.setDouble(6, alumno.getEvidencia2());
            pstmt.setDouble(7, alumno.getExamenParcial());
            pstmt.setDouble(8, alumno.getEvidencia3());
            pstmt.setDouble(9, alumno.getEvidencia4());
            pstmt.setDouble(10, alumno.getExamenFinal());
            pstmt.setInt(11, alumno.getId());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("✗ Error al actualizar alumno");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina un alumno de la base de datos
     * @param id El ID del alumno
     * @return true si fue exitoso
     */
    public boolean eliminarAlumno(int id) {
        String sql = "DELETE FROM alumnos WHERE id = ?";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("✗ Error al eliminar alumno");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene la cantidad total de alumnos registrados
     * @return Cantidad de alumnos
     */
    public int contarAlumnos() {
        String sql = "SELECT COUNT(*) as total FROM alumnos";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al contar alumnos");
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Elimina todos los alumnos (para limpiar la BD)
     */
    public void limpiarBaseDatos() {
        String sql = "DELETE FROM alumnos";
        
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ Base de datos limpiada");
        } catch (SQLException e) {
            System.out.println("✗ Error al limpiar la base de datos");
            e.printStackTrace();
        }
    }
}
