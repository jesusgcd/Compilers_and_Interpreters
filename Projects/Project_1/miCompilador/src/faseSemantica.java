import java.util.ArrayList;

public class faseSemantica {

    private final ArrayList<Token> tokens;
    private final TablaSimbolos tablaSimbolos;
    private int currentTokenIndex;
    private int numeroLinea;

    public faseSemantica(ArrayList<Token> tokens, TablaSimbolos tablaSimbolos) {
        this.tokens = tokens;
        this.tablaSimbolos = tablaSimbolos;
        this.currentTokenIndex = 0;
        this.numeroLinea = 1;
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

    // Método para construir el AST para cada sublista de tokens
    public ArrayList<NodoAST> construirASTs() {
        ArrayList<NodoAST> arbolesAST = new ArrayList<>();
        ArrayList<ArrayList<Token>> expresiones = dividirEnExpresiones();

        for (ArrayList<Token> expresionTokens : expresiones) {
            currentTokenIndex = 0;
            tokens.clear();
            tokens.addAll(expresionTokens);
            NodoAST programa = construirPrograma();
            arbolesAST.add(programa);
            numeroLinea++;
        }
        return arbolesAST;
    }

    private NodoAST construirPrograma() {
        NodoAST nodoPrograma = construirExpresion();
        return nodoPrograma;
    }

    private NodoAST construirExpresion() {
        Token tokenActual = currentToken();
        NodoAST nodo;

        if (tokenActual.getTipo().equals("IDENTIFICADOR")) {
            String identificador = tokenActual.getValor();
            consumirToken();

            if (currentToken() != null && currentToken().getTipo().equals("ASIGNACION")) {
                consumirToken();
                NodoAST expresionDerecha = construirExpresion();
                nodo = new NodoAsignacion(identificador, expresionDerecha);
            } else {

                String valorIdentificador = tokenActual.getValor();
                InformacionSimbolo infoSimbolo = tablaSimbolos.obtenerSimbolo(valorIdentificador);
                // Verificar que el identificador exista en la tabla de símbolos y su línea sea <= a la actual
                if (infoSimbolo.getLinea() == numeroLinea) {
                    throw new RuntimeException("Error [Fase Semántica]: El identificador '" + valorIdentificador + "' no está declarado antes de su uso en la línea " + numeroLinea);
                }
                nodo = new NodoIdentificador(identificador);
                while (currentToken() != null && (esOperadorSumaResta(currentToken()) || esOperadorMultiplicacionDivision(currentToken()))) {
                    String operador = currentToken().getValor();
                    consumirToken();
                    nodo = new NodoOperacionBinaria(nodo, construirTermino(), operador);
                }
            }
        } else {
            nodo = construirTermino();
            while (currentToken() != null && esOperadorSumaResta(currentToken())) {
                String operador = currentToken().getValor();
                consumirToken();
                nodo = new NodoOperacionBinaria(nodo, construirTermino(), operador);
            }
        }
        return nodo;
    }

    private NodoAST construirTermino() {
        NodoAST nodo = construirFactor();
        while (esOperadorMultiplicacionDivision(currentToken())) {
            String operador = currentToken().getValor();
            consumirToken();
            nodo = new NodoOperacionBinaria(nodo, construirFactor(), operador);
        }
        return nodo;
    }

    private NodoAST construirFactor() {
        Token tokenActual = currentToken();
        NodoAST nodo;
    
        switch (tokenActual.getTipo()) {
            case "IDENTIFICADOR" -> {
                String valorIdentificador = tokenActual.getValor();
                InformacionSimbolo infoSimbolo = tablaSimbolos.obtenerSimbolo(valorIdentificador);
            
                // Verificar que el identificador exista en la tabla de símbolos y su línea sea <= a la actual
                if (infoSimbolo.getLinea() == numeroLinea) {
                    throw new RuntimeException("Error [Fase Semántica]: El identificador '" + valorIdentificador + "' no está declarado antes de su uso en la línea " + numeroLinea);
                }
    
                nodo = new NodoIdentificador(valorIdentificador);
                consumirToken();
            }
            case "NUMERO" -> {
                nodo = new NodoNumero(Integer.parseInt(tokenActual.getValor()));
                consumirToken();
            }
            case "PARENTESIS_IZQ" -> {
                consumirToken();
                nodo = construirExpresion();
                consumirToken();  // Consumir el paréntesis derecho
            }
            default -> throw new RuntimeException("Error [Fase Semántica]: Factor inesperado en la expresión.");
        }
        return nodo;
    }
    

    private boolean esOperadorSumaResta(Token token) {
        return token.getTipo().equals("SUMA") || token.getTipo().equals("RESTA");
    }

    private boolean esOperadorMultiplicacionDivision(Token token) {
        return token.getTipo().equals("MULTIPLICACION") || token.getTipo().equals("DIVISION");
    }

    private Token currentToken() {
        return currentTokenIndex < tokens.size() ? tokens.get(currentTokenIndex) : null;
    }

    private void consumirToken() {
        if (currentTokenIndex < tokens.size() - 1) {
            currentTokenIndex++;
        }
    }
}
