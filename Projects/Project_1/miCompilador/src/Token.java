public class Token {

    private String tipo;   // El tipo del token (ejemplo: "IDENTIFICADOR", "NUMERO", etc.)
    private String valor;  // El valor literal del token (ejemplo: "b", "42", etc.)
    private int linea;     // La línea donde se encuentra el token (opcional, útil para reportar errores)

    // Constructor
    public Token(String valor, String tipo, int linea) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
    }

    // Método para obtener el tipo del token (ejemplo: "IDENTIFICADOR", "NUMERO", etc.)
    public String getTipo() {
        return tipo;
    }

    // Método para obtener el valor literal del token (ejemplo: "b", "42", etc.)
    public String getValor() {
        return valor;
    }

    // Método para obtener la línea donde se encuentra el token
    public int getLinea() {
        return linea;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tipo: '" + tipo + '\'' +
                ", valor: '" + valor + '\'' +
                ", linea: " + linea +
                '}';
    }
}
