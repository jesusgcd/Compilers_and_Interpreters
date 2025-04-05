
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class faseSintactica {

    private ArrayList<Token> tokens;
    private int currentTokenIndex;
    private final Stack<String> stack;
    private final TablaSimbolos tablaSimbolos;

    public faseSintactica(ArrayList<Token> tokens, TablaSimbolos tablaSimbolos) {
        this.tokens = tokens;
        this.tablaSimbolos = tablaSimbolos;
        this.currentTokenIndex = 0;
        this.stack = new Stack<>();
    }

    // Método para iniciar el análisis sintáctico y validar cada expresión
    public void iniciarfaseSintactica() throws IOException {
        // Dividir en sublistas de expresiones
        ArrayList<ArrayList<Token>> listaExpresiones = dividirEnExpresiones();

        // Recorrer cada sublista de tokens (cada expresión)
        for (ArrayList<Token> expresion : listaExpresiones) {

            // Reiniciar el índice de tokens y pila para cada expresión
            currentTokenIndex = 0;
            stack.clear();
            stack.push("PUNTO_COMA"); // Marca el final de la expresión
            stack.push("S");    // Estado inicial (S para start)

            // Establecer los tokens actuales como la nueva expresión
            this.tokens = expresion;

            // Procesar la cadena actual hasta el siguiente PUNTO_COMA
            while (!stack.isEmpty() && currentTokenIndex < tokens.size()) {
                String top = stack.peek(); // Simbolo en la cima de la pila

                if (isTerminal(top)) {
                    // Si es un terminal, debe coincidir con el token actual
                    if (top.equals(currentToken().getTipo())) {
                        //System.out.println("Coincidencia: " + top);
                        stack.pop();    // Consumir el símbolo
                        nextToken();    // Avanzar al siguiente token
                    } else {
                        // Reportar el error con el token esperado
                        reportarError(currentToken().getLinea(), "se esperaba el token " + top + " pero se encontró " + currentToken().getTipo());
                        return; // Detener el análisis al encontrar el error
                    }
                } else {
                    // Si no es terminal, aplicar la regla de producción
                    applyProduction(top);
                }
            }

            // Al final de la expresión, si llegamos a un `PUNTO_COMA`, continuar con la siguiente
            if (currentTokenIndex < tokens.size() && !currentToken().getTipo().equals("PUNTO_COMA")) {
                reportarError(currentToken().getLinea(), "falta token ;");
                return;
            } else if (currentTokenIndex < tokens.size()) {
                nextToken(); // Consumir el `PUNTO_COMA` y pasar a la siguiente expresión
            }

            //System.out.println("Expresión analizada correctamente.");
        }

        //System.out.println("Análisis sintáctico completado exitosamente.");
    }

    // Método para determinar si un símbolo es terminal
    private boolean isTerminal(String symbol) {
        return symbol.equals("IDENTIFICADOR") || symbol.equals("NUMERO") || symbol.equals("PUNTO_COMA")
                || symbol.equals("ASIGNACION") || symbol.equals("SUMA") || symbol.equals("RESTA")
                || symbol.equals("MULTIPLICACION") || symbol.equals("DIVISION") || symbol.equals("PARENTESIS_IZQ")
                || symbol.equals("PARENTESIS_DER");
    }

    // Aplicar una producción basada en la cima de la pila
    private void applyProduction(String nonTerminal) {
        stack.pop(); // Quitar el no terminal de la pila

        Token current = currentToken();
        String tipoToken = current.getTipo();

        switch (nonTerminal) {
            case "S" -> {
                // Estado inicial
                if (tipoToken.equals("IDENTIFICADOR")) {
                    stack.push("PUNTO_COMA");
                    stack.push("E");  // Manejar expresiones
                    stack.push("ASIGNACION");
                    stack.push("IDENTIFICADOR");
                } else {
                    reportarError(current.getLinea(), "falta un identificador en la asignación");
                    return;
                }
            }

            case "E" -> {
                // Expresión
                if (tipoToken.equals("IDENTIFICADOR") || tipoToken.equals("NUMERO") || tipoToken.equals("PARENTESIS_IZQ")) {
                    stack.push("E'");  // Expresión adicional (para manejar operadores)
                    stack.push("T");   // Termino
                } else {
                    reportarError(current.getLinea(), "error en la expresión, se esperaba un identificador, número o '('");
                    return;
                }
            }

            case "E'" -> {
                // Expresión adicional (manejo de suma/resta)
                switch (tipoToken) {
                    case "SUMA" -> {
                        stack.push("E'");
                        stack.push("T");
                        stack.push("SUMA");
                    }
                    case "RESTA" -> {
                        stack.push("E'");
                        stack.push("T");
                        stack.push("RESTA");
                    }
                    case "PUNTO_COMA", "PARENTESIS_DER" -> {
                    }
                    default -> {
                        reportarError(current.getLinea(), "error en la expresión, se esperaba '+', '-', ';', o ')'");
                        return;
                    }
                }
                // Producción épsilon (no se hace nada)
            }

            case "T" -> {
                // Termino
                if (tipoToken.equals("IDENTIFICADOR") || tipoToken.equals("NUMERO") || tipoToken.equals("PARENTESIS_IZQ")) {
                    stack.push("T'");
                    stack.push("F");
                } else {
                    reportarError(current.getLinea(), "error en el término, se esperaba un identificador, número o '('");
                    return;
                }
            }

            case "T'" -> {
                // Termino adicional (manejo de multiplicación/división)
                switch (tipoToken) {
                    case "MULTIPLICACION" -> {
                        stack.push("T'");
                        stack.push("F");
                        stack.push("MULTIPLICACION");
                    }
                    case "DIVISION" -> {
                        stack.push("T'");
                        stack.push("F");
                        stack.push("DIVISION");
                    }
                    case "SUMA", "RESTA", "PUNTO_COMA", "PARENTESIS_DER" -> {
                    }
                    default -> {
                        reportarError(current.getLinea(), "error en el término, se esperaba '*', '/', '+', '-', ';', o ')'");
                        return;
                    }
                }
                // Producción épsilon (no se hace nada)
            }

            case "F" -> {
                // Factor
                switch (tipoToken) {
                    case "IDENTIFICADOR" ->
                        stack.push("IDENTIFICADOR");
                    case "NUMERO" ->
                        stack.push("NUMERO");
                    case "PARENTESIS_IZQ" -> {
                        stack.push("PARENTESIS_DER");
                        stack.push("E");
                        stack.push("PARENTESIS_IZQ");
                    }
                    default -> {
                        reportarError(current.getLinea(), "error en el factor, se esperaba un identificador, número o '('");
                        return;
                    }
                }
            }

            default -> {
                reportarError(current.getLinea(), "producción no encontrada para " + nonTerminal);
                return;
            }
        }
    }

    // Método para dividir la lista de tokens en sublistas, cada una representando una expresión completa
    public ArrayList<ArrayList<Token>> dividirEnExpresiones() {
        ArrayList<ArrayList<Token>> listaExpresiones = new ArrayList<>();
        ArrayList<Token> expresionActual = new ArrayList<>();

        for (Token token : tokens) {
            expresionActual.add(token); // Añadir el token actual a la expresión

            // Si el token es un PUNTO_COMA, es el final de una expresión
            if (token.getTipo().equals("PUNTO_COMA")) {
                listaExpresiones.add(new ArrayList<>(expresionActual)); // Añadir la expresión completa a la lista
                expresionActual.clear(); // Reiniciar la lista para la siguiente expresión
            }
        }

        // Retornar la lista con sublistas de tokens que representan expresiones completas
        return listaExpresiones;
    }

    // Método para reportar un error en la fase sintáctica
    private void reportarError(int linea, String mensaje) {
        // Eliminar registros de la tabla de símbolos que coincidan con la línea del error
        tablaSimbolos.eliminarSimbolosPorLinea(linea);

        tablaSimbolos.mostrarSimbolos();
        // Lanza una excepción para que el try-catch de miCompilador.java pueda atraparla
        throw new RuntimeException("Error [Fase Sintáctica]: La línea " + linea + " contiene un error en su gramática, " + mensaje);
    }

    // Método para obtener el token actual
    private Token currentToken() {
        if (currentTokenIndex >= tokens.size()) {
            return null; // No hay más tokens
        }
        return tokens.get(currentTokenIndex);
    }

    // Método para avanzar al siguiente token
    private void nextToken() {
        currentTokenIndex++;
    }

    public TablaSimbolos getTablaSimbolos(){
        return tablaSimbolos;
    }

    public ArrayList<Token> getListaToken(){
        return tokens;
    }
}
