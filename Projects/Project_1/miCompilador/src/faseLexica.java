import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class faseLexica {

    private static final ArrayList<Token> tokens = new ArrayList<>();
    private static final TablaSimbolos tablaSimbolos = new TablaSimbolos(); // Tabla de símbolos
    private static int estado = 0;

    // Constructor
    public faseLexica() {
    }

    public void iniciarFaseLexica(String rutaArchivo) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo));
        String linea;
        int numeroLinea = 1;

        while ((linea = reader.readLine()) != null) {
            procesarLinea(linea, numeroLinea);
            numeroLinea++;
        }

        reader.close();
        //mostrarTokens();
        // System.out.println("\nTabla de símbolos:");
        // tablaSimbolos.mostrarSimbolos(); // Mostrar la tabla de símbolos al final
    }

    // Procesamiento manual de línea por DFA
    private static void procesarLinea(String linea, int numeroLinea) {
        int len = linea.length();
        StringBuilder lexemaActual = new StringBuilder();
        boolean errorIdentificador = false; // Marca si el identificador ya tiene un error
        boolean errorNumero = false; // Marca si el número ya tiene un error

        for (int i = 0; i < len; i++) {
            char actual = linea.charAt(i);
            switch (estado) {
                case 0 -> {
                    // Estado inicial
                    if (esLetra(actual)) {
                        estado = 1; // Posible identificador
                        lexemaActual.append(actual);
                    } else if (esDigito(actual)) {
                        estado = 2; // Posible número
                        lexemaActual.append(actual);
                    } else if (esOperador(actual)) {
                        agregarToken(String.valueOf(actual), tipoOperador(actual), numeroLinea);
                    } else if (esAsignacion(actual)) {
                        agregarToken(String.valueOf(actual), "ASIGNACION", numeroLinea);
                    } else if (actual == '(') {
                        agregarToken(String.valueOf(actual), "PARENTESIS_IZQ", numeroLinea);
                    } else if (actual == ')') {
                        agregarToken(String.valueOf(actual), "PARENTESIS_DER", numeroLinea);
                    } else if (actual == ';') {
                        agregarToken(String.valueOf(actual), "PUNTO_COMA", numeroLinea);
                    } else if (esEspacio(actual)) {
                        // Ignorar espacios en blanco
                    } else {
                        reportarError(numeroLinea, String.valueOf(actual));
                    }
                }

                case 1 -> {
                    // Reconociendo un identificador
                    for (; i < len; i++) {
                        actual = linea.charAt(i);
                        if (esLetra(actual) || esDigito(actual) || (actual >= 'A' && actual <= 'Z')) {
                            lexemaActual.append(actual);
                            if (esDigito(actual) || (actual >= 'A' && actual <= 'Z')) {
                                errorIdentificador = true;
                            }
                        } else {
                            if (errorIdentificador) {
                                reportarError(numeroLinea,
                                        lexemaActual.toString() + " (Identificador contiene caracteres inválidos)");
                                errorIdentificador = false;
                            } else if (lexemaActual.length() > 12) {
                                reportarError(numeroLinea,
                                        lexemaActual.toString() + " (Identificador contiene más de 12 caracteres)");
                            } else {
                                agregarToken(lexemaActual.toString(), "IDENTIFICADOR", numeroLinea);
                                // Agregar identificador a la tabla de símbolos
                                tablaSimbolos.agregarSimbolo(lexemaActual.toString(),
                                        new InformacionSimbolo("IDENTIFICADOR", "global", numeroLinea));
                            }
                            lexemaActual.setLength(0); // Reiniciar el lexema
                            estado = 0; // Volver al estado inicial
                            i--;
                            break;
                        }
                    }
                }

                case 2 -> {
                    // Reconociendo un número
                    for (; i < len; i++) {
                        actual = linea.charAt(i);
                        if (esLetra(actual) || esDigito(actual)) {
                            lexemaActual.append(actual);
                            if (esLetra(actual)) {
                                errorNumero = true;
                            }
                        } else {
                            if (errorNumero) {
                                reportarError(numeroLinea,
                                        lexemaActual.toString() + " (Número contiene caracteres inválidos)");
                                errorNumero = false;
                            } else {
                                agregarToken(lexemaActual.toString(), "NUMERO", numeroLinea);
                            }
                            lexemaActual.setLength(0); // Reiniciar el lexema
                            estado = 0; // Volver al estado inicial
                            i--; // Reprocesar el carácter actual
                            break;
                        }
                    }
                }

                default -> reportarError(numeroLinea, String.valueOf(actual));
            }
        }

        // Si queda algún token sin procesar al final de la línea
        if (estado == 1) {
            if (errorIdentificador || lexemaActual.length() > 12) {
                reportarError(numeroLinea, lexemaActual.toString() + " (Identificador contiene caracteres inválidos)");
            } else {
                agregarToken(lexemaActual.toString(), "IDENTIFICADOR", numeroLinea);
                // Agregar identificador a la tabla de símbolos
                tablaSimbolos.agregarSimbolo(lexemaActual.toString(),
                        new InformacionSimbolo("desconocido", "global", numeroLinea));
            }
        } else if (estado == 2) {
            if (errorNumero) {
                reportarError(numeroLinea, lexemaActual.toString() + " (Número contiene caracteres inválidos)");
            } else {
                agregarToken(lexemaActual.toString(), "NUMERO", numeroLinea);
            }
        }

        estado = 0; // Reiniciar estado al finalizar la línea
    }

    private static void agregarToken(String valor, String tipo, int linea) {
        tokens.add(new Token(valor, tipo, linea));
    }

    private static void reportarError(int linea, String mensaje) {
        throw new RuntimeException(
            "Error [Fase Léxica]: La línea " + linea + " contiene un error, lexema no reconocido: " + mensaje
        );
    }
    

    private static void mostrarTokens() {
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    // Métodos auxiliares para reconocer caracteres
    private static boolean esLetra(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean esOperador(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static boolean esAsignacion(char c) {
        return c == '=';
    }

    private static boolean esEspacio(char c) {
        return c == ' ' || c == '\t' || c == '\n';
    }

    // Obtener tipo de operador
    private static String tipoOperador(char c) {
        return switch (c) {
            case '+' -> "SUMA";
            case '-' -> "RESTA";
            case '*' -> "MULTIPLICACION";
            case '/' -> "DIVISION";
            default -> "DESCONOCIDO";
        };
    }

    public  TablaSimbolos getTablaSimbolos(){
        return tablaSimbolos;
    }

    public ArrayList<Token> getListaToken(){
        return tokens;
    }
}