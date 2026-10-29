package com.mmacedo.registroalumnos.bd;

import com.mmacedo.registroalumnos.modelo.Alumno;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BaseDatosAlumnos {

    private static final String URL = "jdbc:sqlite:alumnos.db";
    private static final String COLUMNAS = "nombre, apellido, carrera, semestre, "
            + "evidencia1, evidencia2, examen_parcial, evidencia3, evidencia4, examen_final";

    private Connection conexion;

    public BaseDatosAlumnos() {
        conectar();
        crearTabla();
    }

    public void conectar() {
        try {
            Class.forName("org.sqlite.JDBC");
            conexion = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            registrarError("No se pudo conectar a la base de datos", e);
        }
    }

    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            registrarError("Error al cerrar la conexion", e);
        }
    }

    private void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS alumnos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "apellido TEXT NOT NULL,"
                + "carrera TEXT NOT NULL,"
                + "semestre INTEGER NOT NULL,"
                + "evidencia1 REAL DEFAULT 0,"
                + "evidencia2 REAL DEFAULT 0,"
                + "examen_parcial REAL DEFAULT 0,"
                + "evidencia3 REAL DEFAULT 0,"
                + "evidencia4 REAL DEFAULT 0,"
                + "examen_final REAL DEFAULT 0,"
                + "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP)";

        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            registrarError("Error al crear la tabla", e);
        }
    }

    public boolean insertarAlumno(Alumno alumno) {
        String sql = "INSERT INTO alumnos (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            cargarParametros(ps, alumno);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            registrarError("Error al insertar alumno", e);
            return false;
        }
    }

    public List<Alumno> obtenerTodos() {
        List<Alumno> alumnos = new ArrayList<>();
        String sql = "SELECT * FROM alumnos ORDER BY id";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                alumnos.add(mapear(rs));
            }
        } catch (SQLException e) {
            registrarError("Error al obtener alumnos", e);
        }
        return alumnos;
    }

    public Alumno obtenerPorId(int id) {
        String sql = "SELECT * FROM alumnos WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            registrarError("Error al obtener alumno", e);
        }
        return null;
    }

    public boolean actualizarAlumno(Alumno alumno) {
        String sql = "UPDATE alumnos SET nombre = ?, apellido = ?, carrera = ?, semestre = ?, "
                + "evidencia1 = ?, evidencia2 = ?, examen_parcial = ?, "
                + "evidencia3 = ?, evidencia4 = ?, examen_final = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            cargarParametros(ps, alumno);
            ps.setInt(11, alumno.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            registrarError("Error al actualizar alumno", e);
            return false;
        }
    }

    public boolean eliminarAlumno(int id) {
        String sql = "DELETE FROM alumnos WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            registrarError("Error al eliminar alumno", e);
            return false;
        }
    }

    public int contarAlumnos() {
        String sql = "SELECT COUNT(*) AS total FROM alumnos";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            registrarError("Error al contar alumnos", e);
        }
        return 0;
    }

    public void limpiarBaseDatos() {
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("DELETE FROM alumnos");
        } catch (SQLException e) {
            registrarError("Error al limpiar la base de datos", e);
        }
    }

    private void cargarParametros(PreparedStatement ps, Alumno a) throws SQLException {
        ps.setString(1, a.getNombre());
        ps.setString(2, a.getApellido());
        ps.setString(3, a.getCarrera());
        ps.setInt(4, a.getSemestre());
        ps.setDouble(5, a.getEvidencia1());
        ps.setDouble(6, a.getEvidencia2());
        ps.setDouble(7, a.getExamenParcial());
        ps.setDouble(8, a.getEvidencia3());
        ps.setDouble(9, a.getEvidencia4());
        ps.setDouble(10, a.getExamenFinal());
    }

    private Alumno mapear(ResultSet rs) throws SQLException {
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

    // Único método genérico para manejar todas las excepciones
    private void registrarError(String mensaje, Exception e) {
        System.err.println(mensaje + ": " + e.getMessage());
    }
}
