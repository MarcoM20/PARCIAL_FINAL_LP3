package modelo;

public class Alumno {
    private int id;
    private String nombre;
    private String apellido;
    private String carrera;
    private int semestre;
    
    // Calificaciones
    private double evidencia1;
    private double evidencia2;
    private double examenParcial;
    private double evidencia3;
    private double evidencia4;
    private double examenFinal;
    
    // Constructor vacío
    public Alumno() {
    }
    
    // Constructor con parámetros
    public Alumno(String nombre, String apellido, String carrera, int semestre) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.carrera = carrera;
        this.semestre = semestre;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getCarrera() {
        return carrera;
    }
    
    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }
    
    public int getSemestre() {
        return semestre;
    }
    
    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }
    
    public double getEvidencia1() {
        return evidencia1;
    }
    
    public void setEvidencia1(double evidencia1) {
        this.evidencia1 = evidencia1;
    }
    
    public double getEvidencia2() {
        return evidencia2;
    }
    
    public void setEvidencia2(double evidencia2) {
        this.evidencia2 = evidencia2;
    }
    
    public double getExamenParcial() {
        return examenParcial;
    }
    
    public void setExamenParcial(double examenParcial) {
        this.examenParcial = examenParcial;
    }
    
    public double getEvidencia3() {
        return evidencia3;
    }
    
    public void setEvidencia3(double evidencia3) {
        this.evidencia3 = evidencia3;
    }
    
    public double getEvidencia4() {
        return evidencia4;
    }
    
    public void setEvidencia4(double evidencia4) {
        this.evidencia4 = evidencia4;
    }
    
    public double getExamenFinal() {
        return examenFinal;
    }
    
    public void setExamenFinal(double examenFinal) {
        this.examenFinal = examenFinal;
    }
    
    // Método para calcular promedio
    public double calcularPromedio() {
        double suma = evidencia1 + evidencia2 + examenParcial + 
                      evidencia3 + evidencia4 + examenFinal;
        return suma / 6;
    }
    
    @Override
    public String toString() {
        return "Alumno{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", carrera='" + carrera + '\'' +
                ", semestre=" + semestre +
                '}';
    }
}
