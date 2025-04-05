// para ejecutar en powershell -> java .\miCompilador [ARCHIVO DE ENTRADA] [ARCHIVO DE SALIDA]

import java.io.IOException;
import java.util.ArrayList;

public class miCompilador {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: miCompilador [ARCHIVO DE ENTRADA] [ARCHIVO DE SALIDA]");
            return;
        }

        String archivoEntrada = args[0];
        String archivoSalida = args[1];

        try {
            // 1. Fase Léxica
            faseLexica lexer = new faseLexica();
            lexer.iniciarFaseLexica(archivoEntrada);
            ArrayList<Token> tokens = lexer.getListaToken();
            @SuppressWarnings("unchecked")
            ArrayList<Token> tokensCopy = (ArrayList<Token>) tokens.clone();

            TablaSimbolos tablaSimbolos = lexer.getTablaSimbolos();

            // 2. Fase Sintáctica
            faseSintactica parser = new faseSintactica(tokensCopy, tablaSimbolos);
            parser.iniciarfaseSintactica();
            tablaSimbolos = parser.getTablaSimbolos();

            // 3. Fase Semántica: Construcción del AST y análisis semántico
            faseSemantica semantica = new faseSemantica(tokensCopy, tablaSimbolos);
            ArrayList<NodoAST> arbolesAST = semantica.construirASTs();
            GeneradorArchivoSalida.escribirResultadosEnArchivo(archivoSalida, tokens, tablaSimbolos, arbolesAST);

            // 4. Fase Generación de Código
            faseGeneracionCodigo generadorCodigo = new faseGeneracionCodigo(tokens, tablaSimbolos);
            generadorCodigo.generarCodigoPython("codigo_generado.py");

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}
