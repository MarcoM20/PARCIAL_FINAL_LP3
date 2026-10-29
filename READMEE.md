# Registro de Alumnos

Aplicación de escritorio en JavaFX para registrar alumnos con sus calificaciones, guardarlos en SQLite y exportarlos a Excel.

## Requisitos

- JDK 17 o superior (usa la versión configurada en tu proyecto)
- Apache Maven
- NetBeans (el proyecto fue creado con NetBeans 25)
- Dependencias definidas en `pom.xml`:
  - JavaFX 21
  - SQLite JDBC (`org.xerial:sqlite-jdbc`)
  - Apache POI (`org.apache.poi:poi-ooxml`, versión 5.2.3)

## Estructura del proyecto

```
RegistroAlumnos/
├── src/main/java/com/mmacedo/registroalumnos/
│   ├── aplicacion/       MainApp.java
│   ├── bd/               BaseDatosAlumnos.java
│   ├── controladores/    ControladorPrincipal.java
│   │                     IngresoCalificacionesDialogo.java
│   ├── modelo/           Alumno.java
│   ├── tareas/           TareaBD.java
│   └── utilidades/       ExportadorExcel.java
└── pom.xml
```

## Cómo ejecutar el proyecto

### Opción 1: NetBeans

1. Abre NetBeans y selecciona **File > Open Project**.
2. Elige la carpeta `RegistroAlumnos`.
3. Espera a que Maven descargue las dependencias.
4. Haz clic derecho en el proyecto y selecciona **Run**, o presiona **F6**.

### Opción 2: Línea de comandos

Desde la carpeta del proyecto ejecuta:

```
mvn clean compile
```

Después ejecuta la aplicación desde NetBeans o con el plugin de JavaFX configurado en el `pom.xml`.

## Uso de la aplicación

1. **Registrar alumnos**
   - Haz clic en el botón **Guardar Alumno**.
   - Escribe la cantidad de alumnos que quieres registrar y presiona **Aceptar**.

2. **Ingresar datos**
   - En el cuadro **Registro de calificaciones**, escribe la carrera y el semestre. Estos datos se aplican a todos los alumnos del lote.
   - En la tabla, completa el nombre, el apellido y las notas de Evidencia 1 a 4, Parcial medio y Final. Las notas deben estar entre 0 y 20.
   - Para pasar de una celda a otra usa **Enter**, **Tab** o el mouse. Presiona **Esc** para cancelar la edición de una celda.
   - Las filas completamente vacías se ignoran.

3. **Guardar**
   - Presiona **Aceptar**. Si hay datos incompletos o inválidos, la aplicación mostrará un mensaje indicando la fila y el campo.
   - Si todo es correcto, los alumnos se guardan y la lista aparece en el área **Información de Alumnos**.

4. **Consultar**
   - El botón **Cargar Alumnos** actualiza la lista desde la base de datos.

5. **Exportar a Excel**
   - Haz clic en **Exportar Excel**.
   - Elige la carpeta y el nombre del archivo (por ejemplo `alumnos.xlsx`) y confirma.
   - Al terminar, la aplicación mostrará la ruta del archivo generado.

## Notas

- La base de datos se crea automáticamente como `alumnos.db` en la carpeta desde donde se ejecuta la aplicación.
- Para exportar a Excel primero debes cargar los alumnos con **Cargar Alumnos** o registrarlos con **Guardar Alumno**.
- Si se produce un error de compilación relacionado con dependencias, verifica que el `pom.xml` incluya SQLite JDBC y Apache POI.
- Actualmente la librería esta alojada en la siguiente dirección: C:\Users\USER\Downloads\imb\PARCIAL_FINAL_LP3\RegistroAlumnos\src\main\resources\fxml
- tanto en formato zip como descomprimido para probarlo defrente, solamente debe añadir una nueva librería con el nombre javafx21 e incluir los archivos .jar de la siguiente dirección: C:\Users\USER\Downloads\imb\PARCIAL_FINAL_LP3\RegistroAlumnos\src\main\resources\fxml\javafx-sdk-21.0.13\lib
- Usar el siguiente script en properties del nombre del proyecto y situarse en el punto RUN y en la sección VM options colocar:--module-path "C:\Users\USER\Downloads\imb\PARCIAL_FINAL_LP3\RegistroAlumnos\src\main\resources\fxml\javafx-sdk-21.0.13\lib" --add-modules javafx.controls,javafx.fxml cambiar la información en la ruta de la dirección si tiene otro nombre.