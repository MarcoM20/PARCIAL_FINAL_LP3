package com.mmacedo.registroalumnos.utilidades;

import com.mmacedo.registroalumnos.modelo.Alumno;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Clase para exportar los registros de alumnos a un archivo Excel XLSX
 * Utiliza la librería Apache POI
 * 
 * DEPENDENCIA REQUERIDA: Agregar a pom.xml:
 * <dependency>
 *     <groupId>org.apache.poi</groupId>
 *     <artifactId>poi-ooxml</artifactId>
 *     <version>5.2.3</version>
 * </dependency>
 */
public class ExportadorExcel {
    
    /**
     * Exporta una lista de alumnos a un archivo Excel
     * @param alumnos Lista de alumnos a exportar
     * @param rutaArchivo Ruta donde guardar el archivo (ej: "alumnos.xlsx")
     * @return true si fue exitoso, false en caso contrario
     */
    public static boolean exportarAlumnos(List<Alumno> alumnos, String rutaArchivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Alumnos");
            
            // Crear estilos
            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            CellStyle dataStyle = crearEstiloDatos(workbook);
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] encabezados = {
                "ID", "Nombre", "Apellido", "Carrera", "Semestre",
                "Evidencia 1", "Evidencia 2", "Examen Parcial",
                "Evidencia 3", "Evidencia 4", "Examen Final", "Promedio"
            };
            
            for (int i = 0; i < encabezados.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(encabezados[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Alumno alumno : alumnos) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(alumno.getId());
                row.createCell(1).setCellValue(alumno.getNombre());
                row.createCell(2).setCellValue(alumno.getApellido());
                row.createCell(3).setCellValue(alumno.getCarrera());
                row.createCell(4).setCellValue(alumno.getSemestre());
                row.createCell(5).setCellValue(alumno.getEvidencia1());
                row.createCell(6).setCellValue(alumno.getEvidencia2());
                row.createCell(7).setCellValue(alumno.getExamenParcial());
                row.createCell(8).setCellValue(alumno.getEvidencia3());
                row.createCell(9).setCellValue(alumno.getEvidencia4());
                row.createCell(10).setCellValue(alumno.getExamenFinal());
                row.createCell(11).setCellValue(alumno.calcularPromedio());
                
                // Aplicar estilos a los datos
                for (int i = 0; i < 12; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }
            
            // Ajustar ancho de columnas
            for (int i = 0; i < encabezados.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
            
            System.out.println("✓ Archivo Excel exportado correctamente: " + rutaArchivo);
            return true;
            
        } catch (IOException e) {
            System.out.println("✗ Error al exportar archivo Excel");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Crea un estilo para los encabezados
     */
    private static CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
    
    /**
     * Crea un estilo para los datos
     */
    private static CellStyle crearEstiloDatos(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
}
