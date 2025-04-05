import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class faseGeneracionCodigo {

    private final ArrayList<Token> tokens;
    private final TablaSimbolos tablaSimbolos;

    public faseGeneracionCodigo(ArrayList<Token> tokens, TablaSimbolos tablaSimbolos) {
        this.tokens = tokens;
        this.tablaSimbolos = tablaSimbolos;
    }

    public void generarCodigoPython(String archivoSalida) {
        // Crear la carpeta 'out' si no existe
        File directorio = new File("out");
        if (!directorio.exists()) {
            directorio.mkdir();
        }

        // Crear el archivo de salida en la carpeta 'out'
        File archivo = new File(directorio, archivoSalida);
        try (FileWriter escritor = new FileWriter(archivo)) {
            for (Token token : tokens) {
                // Verificar si el token es un ";"
                if (token.getTipo().equals("PUNTO_COMA")) {
                    escritor.write("\n"); // Agregar un salto de línea en vez de ";"
                } else {
                    // Escribir el valor del token en el archivo
                    escritor.write(token.getValor() + " ");
                }
            }

            // Agregar un salto de línea antes de los prints de los identificadores
            escritor.write("\n\n");

            // Añadir un `print` para cada identificador de la tabla de símbolos
            escritor.write("# Imprimir valores de cada identificador\n");
            for (Map.Entry<String, InformacionSimbolo> entry : tablaSimbolos.obtenerTodosSimbolos().entrySet()) {
                String identificador = entry.getKey();
                escritor.write("print(" + identificador + ")\n");
            }

            System.out.println("Código Python generado en: " + archivo.getAbsolutePath());


        } catch (IOException e) {
            System.err.println("Error al escribir o ejecutar el archivo de Python: " + e.getMessage());
        }
    }
}
