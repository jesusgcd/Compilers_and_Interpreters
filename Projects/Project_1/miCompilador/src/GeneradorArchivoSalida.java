import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GeneradorArchivoSalida {

    // Método para escribir los resultados en el archivo
    public static void escribirResultadosEnArchivo(String archivoSalida, ArrayList<Token> tokens, TablaSimbolos tablaSimbolos, ArrayList<NodoAST> arbolesAST) {
        try {
            // Crear la carpeta 'out' si no existe
            File directorio = new File("out");
            if (!directorio.exists()) {
                directorio.mkdir();
            }

            // Crear el archivo en la carpeta 'out'
            File archivo = new File(directorio, archivoSalida);
            try (FileWriter escritor = new FileWriter(archivo)) {
                // Escribir los tokens
                escritor.write("Lista de Tokens:\n");
                for (Token token : tokens) {
                    escritor.write(token.toString() + "\n"); // Suponiendo que Token tiene un método toString
                }

                // Escribir la tabla de símbolos utilizando el toString de la clase TablaSimbolos
                escritor.write("\nTabla de Símbolos:\n" + tablaSimbolos.toString());

                // Escribir cada AST en el archivo
                escritor.write("\nRepresentación de los ASTs:\n");
                for (int i = 0; i < arbolesAST.size(); i++) {
                    escritor.write("\nAST " + (i + 1) + ":\n");
                    escribirASTEnArchivo(escritor, arbolesAST.get(i), "");
                }

                System.out.println("Resultados escritos en el archivo: " + archivo.getAbsolutePath());

            } catch (IOException e) {
                System.err.println("Error al escribir en el archivo: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error al crear la carpeta 'out': " + e.getMessage());
        }
    }

    // Método para imprimir todos los ASTs en consola
    public static void imprimirASTEnConsola(ArrayList<NodoAST> arbolesAST) {
        System.out.println("Representación de los ASTs:");
        for (int i = 0; i < arbolesAST.size(); i++) {
            System.out.println("\nAST " + (i + 1) + ":");
            recorrerAST(arbolesAST.get(i), "");
        }
    }

    // Método auxiliar para escribir el AST en el archivo
    private static void escribirASTEnArchivo(FileWriter escritor, NodoAST nodo, String prefijo) throws IOException {
        switch (nodo) {
            case NodoAsignacion asignacion -> {
                escritor.write(prefijo + "Asignacion: " + asignacion.identificador + "\n");
                escribirASTEnArchivo(escritor, asignacion.expresion, prefijo + "  ");
            }
            case NodoOperacionBinaria opBinaria -> {
                escritor.write(prefijo + "Operacion Binaria: " + opBinaria.operador + "\n");
                escribirASTEnArchivo(escritor, opBinaria.izquierda, prefijo + "  ");
                escribirASTEnArchivo(escritor, opBinaria.derecha, prefijo + "  ");
            }
            case NodoIdentificador identificador -> escritor.write(prefijo + "Identificador: " + identificador.nombre + "\n");
            case NodoNumero numero -> escritor.write(prefijo + "Numero: " + numero.valor + "\n");
            default -> {}
        }
    }

    // Método auxiliar para recorrer el AST e imprimir en consola
    private static void recorrerAST(NodoAST nodo, String prefijo) {
        switch (nodo) {
            case NodoAsignacion asignacion -> {
                System.out.println(prefijo + "Asignacion: " + asignacion.identificador);
                recorrerAST(asignacion.expresion, prefijo + "  ");
            }
            case NodoOperacionBinaria opBinaria -> {
                System.out.println(prefijo + "Operacion Binaria: " + opBinaria.operador);
                recorrerAST(opBinaria.izquierda, prefijo + "  ");
                recorrerAST(opBinaria.derecha, prefijo + "  ");
            }
            case NodoIdentificador identificador -> System.out.println(prefijo + "Identificador: " + identificador.nombre);
            case NodoNumero numero -> System.out.println(prefijo + "Numero: " + numero.valor);
            default -> {}
        }
    }
}
